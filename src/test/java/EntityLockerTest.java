import org.junit.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * Test class for {@link EntityLocker}
 */
public class EntityLockerTest {

    /**
     * Tests null object for locking
     *
     * In this test we expect that NPE will be thrown
     */
    @Test(expected = NullPointerException.class)
    public void testLockNullEntityId() throws InterruptedException {
        EntityLocker locker = new EntityLocker<Integer>();
        locker.lock(null);
    }

    /**
     * Tests null object for unlocking
     *
     * In this test we expect that NPE will be thrown
     */
    @Test(expected = NullPointerException.class)
    public void testUnlockNullEntityId() throws InterruptedException {
        EntityLocker locker = new EntityLocker<Integer>();
        locker.unlock(null);
    }

    /**
     * Tests object for locking
     *
     * In this test we expect that "true" will be return after successful locking
     */
    @Test
    public void testLock() throws InterruptedException {
        EntityLocker locker = new EntityLocker<Integer>();
        Random random = new Random();
        assertTrue(locker.lock(random.nextInt()));
    }

    /**
     * Tests object for locking by timeout
     *
     * In this test we expect that "true" will be return after successful locking
     */
    @Test
    public void testLockByGoodTimeOut() throws InterruptedException{
        EntityLocker locker = new EntityLocker<Integer>();
        Random random = new Random();

        assertTrue(locker.lockByTimeOut(random.nextInt(), 1, TimeUnit.SECONDS));
    }
}
