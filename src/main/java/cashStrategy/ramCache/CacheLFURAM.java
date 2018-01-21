package cashStrategy.ramCache;



/**
 *
 * Created by PC on 10.01.2018.
 */
public class CacheLFURAM<Key, Value> extends AbstractSimpleRamCache<Key, Value> {


    public CacheLFURAM(int size) {
        super(size);
    }

    @Override
    public Value saveInCache(Value objToCash, Key key) {
        return saveInCache(objToCash, key, 1L);
    }

    @Override
    public Value getCachedObj(Key key) {
        Long usageCount;
        if ((usageCount = usageMap.get(key)) != null) {
            usageMap.replace(key, ++usageCount);
        }
        return super.getCachedObj(key);
    }
}
