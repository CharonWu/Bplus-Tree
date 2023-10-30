package src.bplus_tree;

import java.util.concurrent.Semaphore;

class Node<V> {
    protected Semaphore semaphore;
    public Node(){
        this.semaphore = new Semaphore(1);
    }

    public void lockNode(){
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean unlockNode(){
        if(semaphore.availablePermits()==0){
            semaphore.release();
            return true;
        }

        return false;
    }

}
