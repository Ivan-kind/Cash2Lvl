package cashStrategy.twoLvlCache;

import cashStrategy.ICache;
import cashStrategy.fileCache.CacheLFUFile;
import cashStrategy.fileCache.CacheLRUFile;
import cashStrategy.ramCache.Cache2QRAM;
import cashStrategy.ramCache.CacheLFURAM;
import cashStrategy.ramCache.CacheLRURAM;

/**
 *
 * Created by PC on 21.01.2018.
 */
public class CacheFabric {

    public static final int CACHE_LRU_RAM = 1;
    public static final int CACHE_LFU_RAM = 2;
    public static final int CACHE_2Q_RAM = 3;
    public static final int CACHE_LRU_FILE = 4;
    public static final int CACHE_LFU_FILE = 5;
    public static final String UNKNOWN_TYPE_CACHE_ERROR = "Указан неизвестный тип кэша";

    public static ICache getCache(int cacheType, int size, String dirPath) {
        switch (cacheType) {
            case CACHE_LRU_RAM: return new CacheLRURAM<>(size);
            case CACHE_LFU_RAM: return new CacheLFURAM<>(size);
            case CACHE_2Q_RAM: return new Cache2QRAM<>(size);
            case CACHE_LFU_FILE:
                return new CacheLFUFile<>(size, dirPath);
            case CACHE_LRU_FILE:
                return new CacheLRUFile<>(size, dirPath);
            default:
                throw new RuntimeException(UNKNOWN_TYPE_CACHE_ERROR);

        }
    }
}
