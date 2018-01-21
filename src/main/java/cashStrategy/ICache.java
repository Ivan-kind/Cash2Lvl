package cashStrategy;

import CashUtils.ICashCalculate;

import java.util.Map;

/**
 *
 * Created by PC on 07.01.2018.
 */
public interface ICache<Key, Value> {


    public Value deleteObj(Key key);

    public Long getUsageTime(Key key);

    /**
     * Возвращает true, если кэш содержит ключ
     * */
    public boolean isContainsKey(Key key);

    /**
     * Метод возвращает энтри из карты usage, который наиболее вероятен для удаления
     * */
    public Map.Entry<Key, Long> getOldestUsage();

    /**
     * Метод для удаления объекта, который первый в очереди на удаление
     * */
    public Value removeOldObj();

    /**
     * Метод возвращает значение из кэша по ключу
     */
    public Value getCachedObj(Key key, ICashCalculate<Value> cashCalculate);

    /**
     * Метод возвращает значение из кэша по ключу,
     * возвращает null если ключ отсутствует в кэше
     * */
    public Value getCachedObj(Key key);

    /**
     * Запись значения в кэш, возвращает значение в случае,
     * если из кеша вытеснен объект
     * */
    public Value saveInCache(Value objToCash, Key key);

    //public Value saveInCache(Value objToCash, Key key, long time);

    /**
     * Метод очищает кэш
     * */
    public void clearCache();

    /**
     * Возвращает максимальный размер кэша (для начала считаем, что количество объектов в
     * кэше не превышает int
     * */
    public int getMaxSize();

    /**
     * Возвращает текущее количество элементов в кэше
     * */
    public int getCurSize();

    /**
    * Метод для изменения размера кэша
    * */
    public void setSize(int size);
}
