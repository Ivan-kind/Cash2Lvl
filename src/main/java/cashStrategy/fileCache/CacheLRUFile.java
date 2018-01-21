package cashStrategy.fileCache;

import CashUtils.ICashCalculate;
import cashStrategy.AbstractCache;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.GregorianCalendar;

/**
 *
 * Created by PC on 18.01.2018.
 */
public class CacheLRUFile<Key, Value> extends AbstractCache<Key, Value> {

    /*
    * Храним ключ-время сохранения
    * Время можно было бы смотреть и у файла, но посчитал, что хранить long в количестве inе не слишком напряжно
    * а поиск самого старого объекта будет быстрее, чем пробегаться по всем файлам
    * */

    protected final String dirPath;

    public CacheLRUFile(int size, String dirPath) {
        super();
        setSize(size);
        this.dirPath = dirPath != null ? dirPath : "";
    }

    @Override
    public void setSize(int size) {
        synchronized (this) {
            this.size.set(size);
            while (getCurSize() > size) {
                removeOldObj();
            }
        }
    }

    // файла сохраняем с маской hash ключа + время сохранения + ".tmp"
    protected String getFileName(Key key) {
        return dirPath
                + (dirPath.endsWith(String.valueOf(File.separatorChar)) ? File.separatorChar : "")
                + String.valueOf(key.hashCode()) + String.valueOf(usageMap.get(key)) + ".tmp";
    }

    // возвращает объект из файла
    private Value getObjFromFile(String fileName) {
        Value obj = null;
        InputStream fis = null;
        ObjectInputStream ois = null;
        try {
            obj = (Value) (ois = new ObjectInputStream(fis = new FileInputStream(fileName))).readObject();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла " + fileName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    @Override
    public Value deleteObj(Key key) {
        String fileName = getFileName(key);
        Value deletedObj = getObjFromFile(fileName);
        synchronized (this) {
            try {
                Files.delete(Paths.get(fileName));
                usageMap.remove(key);
            } catch (IOException e) {
                throw new RuntimeException("Ошибка удаления файла " + fileName);
            }
        }
        return deletedObj;
    }

    @Override
    public Value removeOldObj() {
        return deleteObj(getOldestUsage().getKey());
    }

    @Override
    public Value getCachedObj(Key key, ICashCalculate<Value> cashCalculate) {
        Value cachedObj = getCachedObj(key);
        if (cachedObj == null) {
            saveInCache(cachedObj = cashCalculate.calculate(), key);
        }
        return cachedObj;
    }

    @Override
    public Value getCachedObj(Key key) {
        return usageMap.containsKey(key) ? getObjFromFile(getFileName(key)) : null;
    }

    protected long getStartUsageValue() {
        return new GregorianCalendar().getTimeInMillis();
    }

    @Override
    public Value saveInCache(Value objToCash, Key key) {
        Value removedObj = null;
        if (getCurSize() == getMaxSize()) {
            removedObj = removeOldObj();
        }
        if (usageMap.containsKey(key)) {
            deleteObj(key);
        }
        ObjectOutputStream oos = null;
        OutputStream fos = null;
        usageMap.put(key, getStartUsageValue());
        try {
            oos= new ObjectOutputStream(fos = new FileOutputStream(getFileName(key)));
            oos.writeObject(objToCash);
            //usageMap.put(key, getStartUsageValue());
        } catch (IOException e) {
            usageMap.remove(key);
            throw new RuntimeException("Ошибка сохранения объекта " + objToCash.toString() + " в файл");
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return removedObj;
    }

    @Override
    public void clearCache() {
        usageMap.keySet().forEach(this::deleteObj);
        usageMap.clear();
    }
}
