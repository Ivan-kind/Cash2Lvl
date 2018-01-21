package cashStrategy.twoLvlCache;

import cashStrategy.ICache;
import cashStrategy.fileCache.CacheLFUFile;
import cashStrategy.fileCache.CacheLRUFile;
import cashStrategy.ramCache.Cache2QRAM;
import cashStrategy.ramCache.CacheLFURAM;
import cashStrategy.ramCache.CacheLRURAM;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * Created by PC on 21.01.2018.
 */
public class CacheFabricTest extends Assert {

    @Test
    public void getCache() {
        ICache buf = CacheFabric.getCache(CacheFabric.CACHE_LRU_RAM, 1, null);
        assertTrue(buf instanceof CacheLRURAM);
        assertTrue(buf.getMaxSize() == 1);
        buf = CacheFabric.getCache(CacheFabric.CACHE_LFU_RAM, 1, null);
        assertTrue(buf instanceof CacheLFURAM);
        assertTrue(buf.getMaxSize() == 1);
        buf = CacheFabric.getCache(CacheFabric.CACHE_LFU_FILE, 1, null);
        assertTrue(buf instanceof CacheLFUFile);
        assertTrue(buf.getMaxSize() == 1);
        buf = CacheFabric.getCache(CacheFabric.CACHE_LRU_FILE, 1, null);
        assertTrue(buf instanceof CacheLRUFile);
        assertTrue(buf.getMaxSize() == 1);
        buf = CacheFabric.getCache(CacheFabric.CACHE_2Q_RAM, 1, null);
        assertTrue(buf instanceof Cache2QRAM);
        assertTrue(buf.getMaxSize() == 1);
    }
}
