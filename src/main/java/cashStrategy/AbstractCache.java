package cashStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * Created by PC on 19.01.2018.
 */
public abstract class AbstractCache<Key, Value> implements ICache<Key, Value> {

    protected ConcurrentMap<Key, Long> usageMap = new ConcurrentHashMap<>();
    protected final AtomicInteger size;

    public AbstractCache() {
        size = new AtomicInteger();
    }

    @Override
    public void setSize(int size) {
        this.size.set(size);
    }

    @Override
    public int getMaxSize() {
        return size.get();
    }

    @Override
    public Long getUsageTime(Key key) {
        return usageMap.get(key);
    }

    @Override
    public boolean isContainsKey(Key key) {
        return usageMap.containsKey(key);
    }

    @Override
    public int getCurSize() {
        return usageMap.size();
    }

    @Override
    public Map.Entry<Key, Long> getOldestUsage() {
        Map.Entry<Key, Long> oldestUsage = null;
        long minTime = Long.MAX_VALUE;
        for (Map.Entry<Key, Long> oneEntry : usageMap.entrySet()) {
            if (minTime > oneEntry.getValue()) {
                minTime = oneEntry.getValue();
                oldestUsage = oneEntry;
            }
        }
        return oldestUsage;
    }
}
