package src.bplus_tree;

import java.util.concurrent.Semaphore;

class Node<V> {
    protected volatile Semaphore semaphore;

    protected volatile String threadId;
    public Node(){
        this.semaphore = new Semaphore(1);
    }

    public void lockNode(){
        try {
            semaphore.acquire();
            threadId = Thread.currentThread().getName();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isLocked(){
        return semaphore.availablePermits()==0;
    }

    public boolean unlockNode(){
        if(semaphore.availablePermits()==0&& threadId.equals(Thread.currentThread().getName())){
            threadId = "-1";
            semaphore.release();
            return true;
        }

        return false;
    }

}
