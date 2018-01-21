package cashStrategy.fileCache;

import cashStrategy.ramCache.CacheLFURAMTest;

/**
 *
 * Created by PC on 21.01.2018.
 */
public class CacheLRUFileTest extends CacheLFURAMTest {

    @Override
    public void init() {
        cache = new CacheLRUFile<>(1, null);
    }
}
