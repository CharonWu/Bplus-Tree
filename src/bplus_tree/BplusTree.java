package src.bplus_tree;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BplusTree<V> {
    private volatile TreeNode<V> root = null;
    private final int n;
    private volatile Semaphore semaphore;

    private volatile String threadId;

    private void lock(){
        try {
            semaphore.acquire();
            threadId = Thread.currentThread().getName();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void unlock(){
        if(semaphore.availablePermits()==0 && threadId == Thread.currentThread().getName()){
            semaphore.release();
        }

    }

    public void validate(){
        if (validateDFS(root, Integer.MIN_VALUE, Integer.MAX_VALUE)) {
            System.out.println("B+ Tree is valid!");
        }else{
            System.err.println("B+ Tree is invalid!");
        }
    }

    private boolean validateDFS(TreeNode<V> node, int min, int max){
        if(node!=root&&!node.getParent().containsChild(node))return false;
        for (int i = 0; i < node.getKeySize(); i++) {
            int key = node.getKey(i);
            if(key>=min&&key<max){
                if(!node.isLeaf())
                    if(!validateDFS((TreeNode<V>) node.getChild(i), min, key)){
                        return false;
                    }
            } else {
                return false;
            }
            min = key;
        }
        if (!node.isLeaf() && !validateDFS((TreeNode<V>) node.getChild(node.getKeySize()), min, max)) {

            return false;
        }
        return true;
    }
    public void display() {
        Queue<TreeNode<V>> q = new LinkedList<>();
        q.offer(root);

        while (!q.isEmpty()) {
            int size = q.size();
            while (size-- > 0) {
                TreeNode<V> node = q.poll();
                if (node.isLeaf()) {
                    node.display();
                } else {
                    node.display();
                    for (int i = 0; i < node.getChildrenSize(); i++) {
                        q.offer((TreeNode<V>) node.getChild(i));
                    }
                }
            }
            System.out.println();

        }

    }

    public BplusTree(int n) {
        this.semaphore = new Semaphore(1);
        this.n = n;
    }

    public NodePair<V> search(int key) {
        lock();
        root.lockNode();
        TreeNode<V> node = root;
        TreeNode<V> lockedNode = root;
        while (!node.isLeaf()) {
            node = (TreeNode<V>) node.getChildNode(key);
            node.lockNode();
            if(!node.isFull()){
                if(node.unlockParentNode()==null){
                    unlock();
                }
                lockedNode = node;

            }
        }

        return new NodePair<>(lockedNode, node);
    }

    public synchronized void insert(int key, V value) {
        if (root == null) {
            lock();
            if (root == null) {
                root = new TreeNode<>(n);
                root.setLeaf(true);
                root.insertValue(key, value);
                unlock();
                return;
            }
            unlock();
        }

        NodePair<V> nodePair = search(key);
        TreeNode<V> lockedNode = nodePair.lockedNode;
        TreeNode<V> leafNode= nodePair.leafNode;
        TreeNode<V> tempNode = nodePair.leafNode;
        while(tempNode!=lockedNode){
            tempNode.unlockNode();
            tempNode = tempNode.getParent();
        }

        if (!leafNode.isFull()) {
            leafNode.insertValue(key, value);
//            leafNode.unlockNode();
//            if(leafNode.unlockParentNode()==null)
//                unlock();
            lockedNode.unlockNode();
            if(leafNode.getParent()==null){
                unlock();
            }
        } else {
            DataNode<V> newNode = new DataNode<>(value);

            TreeNode<V> newLeaf = leafNode.splitLeafNode(key, newNode);

            if (leafNode == root) {
                int[] tempKeys = new int[n];
                Node<V>[] tempChildren = new Node[n + 1];

                tempKeys[0] = newLeaf.getMinKey();
                tempChildren[0] = leafNode;
                tempChildren[1] = newLeaf;
                TreeNode<V> newRoot = new TreeNode<>(1, tempKeys, tempChildren, false);
                root = newRoot;
                leafNode.setParent(root);
                newLeaf.setParent(root);
                lockedNode.unlockNode();

                unlock();
            } else {
//                leafNode.unlockNode();
                splitParentNode(leafNode.getParent(), newLeaf, newLeaf.getMinKey(), lockedNode);

            }
        }
    }

    private void splitParentNode(TreeNode<V> parent, TreeNode<V> child, int key, TreeNode<V> lockedNode) {
        if (parent != null && !parent.isFull()) {
            parent.insertInternalNode(child);
            child.setParent(parent);

            lockedNode.unlockNode();
            if(lockedNode.getParent()==null){
                unlock();
            }

//            parent.unlockNode();
//            if(parent.unlockParentNode()==null)
//                unlock();
            return;
        }
        TreeNode<V> newInternalNode = parent.splitInternalNode(key, child);

        if (parent == root) {
            int[] tempKeys = new int[n];
            Node<V>[] tempChildren = new Node[n + 1];

            tempKeys[0] = newInternalNode.getPreviousKey();
            tempChildren[0] = parent;
            tempChildren[1] = newInternalNode;
            TreeNode<V> newRoot = new TreeNode<>(1, tempKeys, tempChildren, false);
            root = newRoot;
            parent.setParent(root);
            newInternalNode.setParent(root);
//            parent.unlockNode();

            lockedNode.unlockNode();

            unlock();
        } else {
//            parent.unlockNode();
            splitParentNode(parent.getParent(), newInternalNode, newInternalNode.getPreviousKey(), lockedNode);
        }


    }
}
