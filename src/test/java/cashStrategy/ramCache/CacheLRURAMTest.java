package cashStrategy.ramCache;

import cashStrategy.ICache;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * Created by PC on 11.01.2018.
 */
public class CacheLRURAMTest extends Assert {

    protected ICache<String, String> cache;

    @Before
    public void init() {
        cache = new CacheLRURAM<>(1);
    }

    @After
    public void drop() {
        cache.clearCache();
        cache = null;
    }

    @Test
    public void save() {
        cache.setSize(3);
        cache.saveInCache("Value_1", "Key_1");
        cache.saveInCache("Value_2", "Key_2");
        assertEquals("Value_1", cache.getCachedObj("Key_1"));
        assertEquals("Value_2", cache.getCachedObj("Key_2"));
        cache.saveInCache("New_Value_1", "Key_1");
        // Значение должно перезаписаться
        assertEquals("New_Value_1", cache.getCachedObj("Key_1"));
        assertTrue(cache.getCurSize() == 2);
    }

    @Test
    public void deleteOld() {
        cache.setSize(2);
        cache.saveInCache("Value_1", "Key_1");
        cache.saveInCache("Value_2", "Key_2");
        cache.saveInCache("Value_3", "Key_3");
        assertEquals("Value_2", cache.getCachedObj("Key_2"));
        assertEquals("Value_3", cache.getCachedObj("Key_3"));
        assertTrue(null == cache.getCachedObj("Key_1"));
    }

    @Test
    public void getWithCalculate() {
        cache.setSize(1);
        assertEquals("Value_1", cache.getCachedObj("Key_1", () -> "Value_1"));
        assertEquals("Value_1", cache.getCachedObj("Key_1"));
    }

    @Test
    public void clear() {
        cache.setSize(2);
        cache.saveInCache("Value_1", "Key_1");
        assertTrue(cache.getCurSize() == 1);
        cache.saveInCache("Value_2", "Key_2");
        assertTrue(cache.getMaxSize() == 2);
        cache.clearCache();
        assertTrue(cache.getCurSize() == 0);
    }

    @Test
    public void setSize() {
        cache.setSize(3);
        cache.saveInCache("Value_1", "Key_1");
        cache.saveInCache("Value_2", "Key_2");
        cache.saveInCache("Value_3", "Key_3");
        assertTrue(cache.getCurSize() == 3);
        cache.setSize(1);
        assertTrue(cache.getCurSize() == 1);
    }
}
