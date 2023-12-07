package src.bplus_tree;

import java.util.concurrent.Semaphore;

class Node<V> {
    protected Semaphore semaphore;

    protected String threadId;

    /**
     * This method init the Node, and init the semaphore with permit equals to 1.
     */
    public Node() {
        this.semaphore = new Semaphore(1);
    }

    /**
     * This method lock the Node, and remember the name of the thread that acquired the lock.
     * @exception InterruptedException on getting the name of the current thread.
     */
    public void lockNode() {
        try {
            semaphore.acquire();
            threadId = Thread.currentThread().getName();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method check is the node is locked.
     * @return boolean result.
     */
    public boolean isLocked() {
        return semaphore.availablePermits() == 0;
    }

    /**
     * This method will check is the node is locked by the current thread, and then try to unlock the node.
     * @return boolean result.
     */
    public boolean unlockNode() {
        if (semaphore.availablePermits() == 0 && threadId.equals(Thread.currentThread().getName())) {
            threadId = "-1";
            semaphore.release();
            return true;
        }

        return false;
    }

}
