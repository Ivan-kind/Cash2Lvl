package cashStrategy.ramCache;

/**
 *
 * Created by PC on 14.01.2018.
 */
public class Cache2QRAMTest extends CacheLRURAMTest {

    @Override
    public void init() {
        cache = new Cache2QRAM<>(1);
    }

    @Override
    public void save() {
        super.save();
    }

    @Override
    public void deleteOld() {
        super.deleteOld();
    }

    @Override
    public void getWithCalculate() {
        super.getWithCalculate();
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void setSize() {
        super.setSize();
        setSize2QWithTopLvl(20);
        setSize2Q(10);
    }

    private void setSize2QWithTopLvl(int size) {
        int lowLvlSize = size / 5;
        setSize2Q(size);
        cache.setSize(size);
        for (int i = 0; i < lowLvlSize; i++) {
            cache.getCachedObj("Key_" + i);
        }
        for (int i = size - lowLvlSize; i < size; i++) {
            cache.saveInCache("Value_" + i, "Key_" + i);
        }
        assertTrue(cache.getCurSize() == size);
    }

    private void setSize2Q(int size) {
        cache.setSize(size);
        int lowLvlSize = size > 10 ? size / 5 : 0;
        for (int i = 0; i < size - lowLvlSize; i++) {
            cache.saveInCache("Value_" + i, "Key_" + i);
        }
        // в топ лвл не попадёт ничего, так как значения грузились без повторений
        assertTrue(cache.getCurSize() == size - lowLvlSize);
    }
}
