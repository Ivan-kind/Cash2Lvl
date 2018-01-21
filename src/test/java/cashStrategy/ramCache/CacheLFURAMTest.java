package cashStrategy.ramCache;

/**
 *
 * Created by PC on 12.01.2018.
 */
public class CacheLFURAMTest extends CacheLRURAMTest {

    @Override
    public void deleteOld() {
        cache.setSize(2);
        cache.saveInCache("Value_1", "Key_1");
        cache.saveInCache("Value_2", "Key_2");
        cache.getCachedObj("Key_2");
        cache.saveInCache("Value_3", "Key_3");
        assertEquals("Value_2", cache.getCachedObj("Key_2"));
        assertEquals("Value_3", cache.getCachedObj("Key_3"));
        assertTrue(null == cache.getCachedObj("Key_1"));
    }

    @Override
    public void init() {
        cache = new CacheLFURAM<>(1);
    }
}
