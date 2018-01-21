package cashStrategy.ramCache;

import CashUtils.ICashCalculate;
import cashStrategy.AbstractCache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * Created by PC on 10.01.2018.
 */

public abstract class AbstractSimpleRamCache<Key, Value> extends AbstractCache<Key, Value> {

    protected ConcurrentMap<Key, Value> cacheTable = new ConcurrentHashMap<>();

    public AbstractSimpleRamCache(int size) {
        super();
        setSize(size);
    }

    @Override
    public Value deleteObj(Key key) {
        usageMap.remove(key);
        return cacheTable.remove(key);
    }

    public Value removeOldObj() {
        Key keyToRemove = getOldestUsage().getKey();
        long minTime = Long.MAX_VALUE;
        for (Map.Entry<Key, Long> oneEntry : usageMap.entrySet()) {
            if (minTime > oneEntry.getValue()) {
                minTime = oneEntry.getValue();
                keyToRemove = oneEntry.getKey();
            }
        }

        usageMap.remove(keyToRemove);
        return cacheTable.remove(keyToRemove);
    }

    @Override
    public Value getCachedObj(Key key, ICashCalculate<Value> cashCalculate) {
        Value value = getCachedObj(key);
        if (value == null) {
            saveInCache(value = cashCalculate.calculate(), key);
        }
        return value;
    }

    protected Value saveInCache(Value objToCash, Key key, long time) {
        Value removedObj = null;
        if (size.get() != 0) {
            if (cacheTable.size() == size.get()) {
                removedObj = removeOldObj();
            }
            cacheTable.put(key, objToCash);
            usageMap.put(key, time);
        }
        return removedObj;
    }

    @Override
    public Value getCachedObj(Key key) {
        return cacheTable.get(key);
    }

    @Override
    public void clearCache() {
        cacheTable.clear();
        usageMap.clear();
    }

    @Override
    public void setSize(int size) {
        synchronized (this.size) {
            this.size.set(size);
            while (cacheTable.size() > size) {
                removeOldObj();
            }
        }
    }
}
