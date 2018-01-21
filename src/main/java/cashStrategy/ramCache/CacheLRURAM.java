package cashStrategy.ramCache;

import java.util.GregorianCalendar;

/**
 *
 * Created by PC on 10.01.2018.
 */
public class CacheLRURAM<Key, Value> extends AbstractSimpleRamCache<Key, Value> {

    public CacheLRURAM(int size) {
        super(size);
    }

    @Override
    public Value saveInCache(Value objToCash, Key key) {
        return saveInCache(objToCash, key, new GregorianCalendar().getTimeInMillis());
    }
}
