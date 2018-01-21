package cashStrategy.twoLvlCache;

import CashUtils.ICashCalculate;
import cashStrategy.ICache;

import java.util.Map;

/**
 *
 * Created by PC on 21.01.2018.
 */
public class Cache2Lvl<Key, Value> {

    private final ICache<Key, Value> firstLvlCache;
    private final ICache<Key, Value> secondLvlCache;

    public static final int LVL_FIRST = 1;
    public static final int LVL_SECOND = 2;

    public Cache2Lvl(int sizeFirstLvl, int sizeSecondLvl, int typeFirstLvl, int typeSecondLvl, String dirPath) {
        this(CacheFabric.getCache(typeFirstLvl, sizeFirstLvl, dirPath),
                CacheFabric.getCache(typeSecondLvl, sizeSecondLvl, dirPath));
    }

    public Cache2Lvl(ICache<Key, Value> firstLvlCache, ICache<Key, Value> secondLvlCache) {
        this.firstLvlCache = firstLvlCache;
        this.secondLvlCache = secondLvlCache;
    }

    public ICache<Key, Value> getFirstLvlCache() {
        return firstLvlCache;
    }

    public ICache<Key, Value> getSecondLvlCache() {
        return secondLvlCache;
    }

    public Value deleteObj(Key key) {
        Value removedObj;
        if ((removedObj = firstLvlCache.deleteObj(key)) == null) {
            removedObj = secondLvlCache.deleteObj(key);
        }
        return removedObj;
    }

    public Long getUsageTime(Key key) {
        Long usageTime;
        if ((usageTime = firstLvlCache.getUsageTime(key)) == null) {
            usageTime = secondLvlCache.getUsageTime(key);
        }
        return usageTime;
    }

    public boolean isContainsKey(Key key) {
        return firstLvlCache.isContainsKey(key) || secondLvlCache.isContainsKey(key);
    }

    public Map.Entry<Key, Long> getOldestUsage() {
        return secondLvlCache.getOldestUsage();
    }

    public Value removeOldObj() {
        return secondLvlCache.removeOldObj();
    }

    public Value getCachedObj(Key key, ICashCalculate<Value> cashCalculate) {
        return null;
    }

    public Value getCachedObj(Key key) {
        Value cachedObj;
        if ((cachedObj = firstLvlCache.getCachedObj(key)) == null) {
            cachedObj = secondLvlCache.getCachedObj(key);
        }
        return cachedObj;
    }

    public Value saveInCache(Value objToCash, Key key) {
        Value removedObj;
        Map.Entry<Key, Long> usage = firstLvlCache.getOldestUsage();
        if ((removedObj = firstLvlCache.saveInCache(objToCash, key)) != null) {
            // если объект вытолкнут из первого уровня, то сохраняем его в начало второго уровня
            removedObj = secondLvlCache.saveInCache(removedObj, usage.getKey());
        }
        return removedObj;
    }

    public void clearCache() {
        firstLvlCache.clearCache();
        secondLvlCache.clearCache();
    }

    public int getMaxSize() {
        return firstLvlCache.getMaxSize() + secondLvlCache.getMaxSize();
    }

    public int getCurSize() {
        return firstLvlCache.getCurSize() + secondLvlCache.getCurSize();
    }

    public void setSize(int lvlCache, int size) {
        switch (lvlCache) {
            case LVL_FIRST:
                firstLvlCache.setSize(size);
                break;
            case LVL_SECOND:
                secondLvlCache.setSize(size);
                break;
        }
    }
}
