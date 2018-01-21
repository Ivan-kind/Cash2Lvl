package cashStrategy.fileCache;

import java.io.File;

/**
 *
 * Created by PC on 21.01.2018.
 */
public class CacheLFUFile<Key, Value> extends CacheLRUFile<Key, Value> {

    public CacheLFUFile(int size, String dirPath) {
        super(size, dirPath);
    }

    @Override
    protected long getStartUsageValue() {
        return 1L;
    }

    @Override
    public Value getCachedObj(Key key) {
        Long usageCount;
        Value cachedObj = super.getCachedObj(key);
        if ((usageCount = usageMap.get(key)) != null) {
            usageMap.replace(key, ++usageCount);
        }
        return cachedObj;
    }

    @Override
    protected String getFileName(Key key) {
        // в отличии от LRU, здесь в usageMap в качестве значения лежит количество общащений к объекту
        // нельзя опираться в имени файла на это значение, т.к. оно изменяется в следствии работы
        // если критично, то при каждом обращении необходимо перезаписывать файл
        return dirPath
                + (dirPath.endsWith(String.valueOf(File.separatorChar)) ? File.separatorChar : "")
                + String.valueOf(key.hashCode()) + ".tmp";
    }
}
