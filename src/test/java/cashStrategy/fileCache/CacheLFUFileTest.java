package cashStrategy.fileCache;

import cashStrategy.ramCache.CacheLFURAMTest;

/**
 *
 * Created by PC on 21.01.2018.
 */
public class CacheLFUFileTest extends CacheLFURAMTest {

    @Override
    public void init() {
        cache = new CacheLFUFile<>(1, null);
    }
}
