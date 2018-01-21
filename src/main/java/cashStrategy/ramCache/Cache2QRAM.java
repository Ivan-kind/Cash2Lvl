package cashStrategy.ramCache;

import CashUtils.ICashCalculate;
import cashStrategy.ICache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * Created by PC on 10.01.2018.
 */
public class Cache2QRAM<Key, Value> implements ICache<Key, Value> {

    private Map<String, AbstractSimpleRamCache<Key, Value>> cacheLvlMap;

    private final String LOW_LVL = "LOW_LVL";
    private final String MID_LVL = "MID_LVL";
    private final String TOP_LVL = "TOP_LVL";

    public Cache2QRAM(int size) {
        if (size < 10) {
            cacheLvlMap = new HashMap<String, AbstractSimpleRamCache<Key, Value>>() {{
                put(LOW_LVL, new CacheLRURAM<>(size));
                put(TOP_LVL, new CacheLRURAM<Key, Value>(0));
                put(MID_LVL, new CacheLRURAM<Key, Value>(0));
               // put(MID_LVL, getEmptyCache());
            }};

        } else {
            int lowLvlSize = size / 5;
            cacheLvlMap = new HashMap<String, AbstractSimpleRamCache<Key, Value>>() {{
                put(LOW_LVL, new CacheLRURAM<>(lowLvlSize));
                put(TOP_LVL, new CacheLRURAM<>(lowLvlSize));
                put(MID_LVL, new CacheLRURAM<>(size - 2 * lowLvlSize));
            }};
        }
    }

    private AbstractSimpleRamCache<Key, Value> getEmptyCache() {
        AbstractSimpleRamCache<Key, Value> cache = new CacheLRURAM<>(1);
        cache.clearCache();
        return cache;
    }

    @Override
    public boolean isContainsKey(Key key) {
        boolean isContains = false;
        Iterator<AbstractSimpleRamCache<Key, Value>> it = cacheLvlMap.values().iterator();
        while (!isContains && it.hasNext()) {
            isContains = it.next().isContainsKey(key);
        }
        return isContains;
    }

    @Override
    public Map.Entry<Key, Long> getOldestUsage() {
        return cacheLvlMap.containsKey(MID_LVL) ?
                cacheLvlMap.get(MID_LVL).getOldestUsage()
                : cacheLvlMap.get(LOW_LVL).getOldestUsage();
    }

    private void initEmptyCache() {

    }

    @Override
    public Value removeOldObj() {
        return cacheLvlMap.containsKey(MID_LVL) ?
                cacheLvlMap.get(MID_LVL).removeOldObj()
                : cacheLvlMap.get(LOW_LVL).removeOldObj();
    }

    @Override
    public Value getCachedObj(Key key, ICashCalculate<Value> cashCalculate) {
        Value value = getCachedObj(key);
        if (value == null) {
            saveInCache(value = cashCalculate.calculate(), key);
        }
        return value;
    }

    @Override
    public Long getUsageTime(Key key) {
        Long usageTime = null;
        Iterator<AbstractSimpleRamCache<Key, Value>> it = cacheLvlMap.values().iterator();
        while (usageTime == null && it.hasNext()) {
            usageTime = it.next().getUsageTime(key);
        }
        return usageTime;
    }

    @Override
    public Value getCachedObj(Key key) {
        Value value = null;
        for (String oneKey : cacheLvlMap.keySet()) {
            ICache<Key, Value> oneCache = cacheLvlMap.get(oneKey);
            if ((value = oneCache.getCachedObj(key)) != null) {
                if (MID_LVL.equals(oneKey)) {
                    AbstractSimpleRamCache<Key, Value> topLvlCache = cacheLvlMap.get(TOP_LVL);
                    if (topLvlCache.getMaxSize() == topLvlCache.getCurSize()) {
                        Map.Entry<Key, Long> topLvlUsage = topLvlCache.getOldestUsage();
                        saveInCache(topLvlCache.deleteObj(topLvlUsage.getKey()), topLvlUsage.getKey());
                    }
                    long deletedTime = oneCache.getUsageTime(key);
                    Value deletedValue = oneCache.deleteObj(key);
                    if (deletedValue != null) {
                        topLvlCache.saveInCache(deletedValue, key, deletedTime);
                    }
                }
                break;
            }
        }
        return value;
    }

    @Override
    public Value deleteObj(Key key) {
        Value value = null;
        Iterator<AbstractSimpleRamCache<Key, Value>> it = cacheLvlMap.values().iterator();
        while (value == null && it.hasNext()) {
            value = it.next().deleteObj(key);
        }
        return value;
    }

    @Override
    public Value saveInCache(Value objToCash, Key key) {
        AbstractSimpleRamCache<Key, Value> lowLvlCache = cacheLvlMap.get(LOW_LVL);
        if (lowLvlCache.getMaxSize() == lowLvlCache.getCurSize() && cacheLvlMap.get(MID_LVL).getMaxSize() != 0) {
            Map.Entry<Key, Long> usageEntry = lowLvlCache.getOldestUsage();
            cacheLvlMap.get(MID_LVL).saveInCache(lowLvlCache.deleteObj(usageEntry.getKey()),
                    usageEntry.getKey(), usageEntry.getValue());
        }
        return lowLvlCache.saveInCache(objToCash, key);
    }

    @Override
    public void clearCache() {
        cacheLvlMap.values().forEach(ICache::clearCache);
    }

    @Override
    public int getMaxSize() {
        int size = 0;
        for (ICache<Key, Value> oneCache : cacheLvlMap.values()) {
            size += oneCache.getMaxSize();
        }
        return size;
    }

    @Override
    public int getCurSize() {
        int size = 0;
        for (ICache<Key, Value> oneCache : cacheLvlMap.values()) {
            size += oneCache.getCurSize();
        }
        return size;
    }

    @Override
    public void setSize(int size) {
        //cacheLvlMap.values().forEach(cache -> cache.setSize());
        if (size < 10) {
            //cacheLvlMap = ImmutableMap.of(LOW_LVL, new CacheLRURAM<>(size));
            cacheLvlMap.get(LOW_LVL).setSize(size);
        } else {
            int lowLvlSize = size / 5;
            cacheLvlMap.get(LOW_LVL).setSize(lowLvlSize);
            cacheLvlMap.get(TOP_LVL).setSize(lowLvlSize);
            cacheLvlMap.get(MID_LVL).setSize(size - 2 * lowLvlSize);
            /*cacheLvlMap = ImmutableMap.of(
                    LOW_LVL, new CacheLRURAM<>(lowLvlSize),
                    MID_LVL, new CacheLRURAM<>(size - 2 * lowLvlSize),
                    TOP_LVL, new CacheLRURAM<>(lowLvlSize));*/
        }

    }

    /*class MidLvlCash<KeyLvl, ValueLvl> extends CacheLRURAM<KeyLvl, ValueLvl> {

        private MidLvlCash<KeyLvl, ValueLvl> cashToReplace;

        public MidLvlCash(int size, MidLvlCash<KeyLvl, ValueLvl> cashToReplace) {
            super(size);
            this.cashToReplace = cashToReplace;
        }

        private ValueLvl saveInCache(ValueLvl objToCache, KeyLvl keyLvl, long time) {
            if ()
        }

        @Override
        public ValueLvl saveInCache(ValueLvl objToCache, KeyLvl keyLvl, long time) {
            if (this.getMaxSize() == this.cacheTable.size()) {

            }
            return super.saveInCache(objToCache, keyLvl, time);
        }

        @Override
        public ValueLvl removeOldObj() {
            KeyLvl keyToRemove = getOldestUsage().getKey();
            long minTime = Long.MAX_VALUE;
            for (Map.Entry<KeyLvl, Long> oneEntry : usageMap.entrySet()) {
                if (minTime > oneEntry.getValue()) {
                    minTime = oneEntry.getValue();
                    keyToRemove = oneEntry.getKey();
                }
            }

            usageMap.remove(keyToRemove);
            return cacheTable.remove(keyToRemove);
        }
    }*/
}
