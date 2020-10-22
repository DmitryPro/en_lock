import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  Class responsible to provide synchronization mechanism similar to row-level DB locking
 *
 * @param <T> entity type
 */
public class EntityLocker<T> {

    /**
     *  Map for storing lock objects
     */
    private final Map<T, ReentrantLock> lockMap;

    /**
     * Base constructor
     */
    public EntityLocker(){
        this.lockMap = new ConcurrentHashMap<T, ReentrantLock>();
    }

    /**
     * Default function for locking without timeout
     *
     * @param entityId - id of entity for locking
     * @return true if locking was successful
     * @throws InterruptedException if any error occurs
     */
    public boolean lock(T entityId) throws InterruptedException{
        return lockByTimeOut(entityId, 0, null);
    }

    /**
     * @param entityId - id of entity for locking
     * @param timeout - for locking with timeout
     * @param unit - timeunit of timeout
     * @return true if locking was successful
     * @throws InterruptedException if any error occurs
     */
    public boolean lockByTimeOut(T entityId, long timeout, TimeUnit unit) throws InterruptedException{
        Objects.requireNonNull(entityId);

        while (true){
            ReentrantLock lock = lockMap.get(entityId);

            if (lock != null && lock.isHeldByCurrentThread()){
                return true;
            } else if (lock == null){
                lock = new ReentrantLock();
            }

            if(unit != null){
                if(!lock.tryLock(timeout, unit)){
                    return false;
                }
            } else {
                lock.lockInterruptibly();
            }

            lockMap.putIfAbsent(entityId, lock);

            if (lockMap.get(entityId) == lock){
                return true;
            }
        }
    }

    /**
     * Unlocks lock by entity ID
     *
     * @param entityId ID of entity for unlocking
     * @throws IllegalMonitorStateException if lock is helded not by current thread
     */
    public void unlock(T entityId) throws IllegalMonitorStateException{
        Objects.requireNonNull(entityId);

        ReentrantLock lock = lockMap.get(entityId);

        if(lock != null){
            if (!lock.isHeldByCurrentThread()) {
                throw new IllegalMonitorStateException();
            }

            lock.unlock();
            lockMap.remove(entityId);
        }
    }

}