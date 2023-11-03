package src.bplus_tree;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BplusTree<V> {
    private TreeNode<V> root = null;
    private final int n;
    private final Semaphore semaphore;

    private String threadId;

    private void lock() {
        try {
            semaphore.acquire();
            threadId = Thread.currentThread().getName();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void unlock() {
        if (semaphore.availablePermits() == 0 && threadId.equals(Thread.currentThread().getName())) {
            threadId = "-1";
            semaphore.release();
        }

    }

    public void validate() {
        if (validateDFS(root, Integer.MIN_VALUE, Integer.MAX_VALUE)) {
            System.out.println("B+ Tree is valid!");
        } else {
            System.err.println("B+ Tree is invalid!");
        }
    }

    private boolean validateDFS(TreeNode<V> node, int min, int max) {
        if (node != root && !node.getParent().containsChild(node)) return false;
        for (int i = 0; i < node.getKeySize(); i++) {
            int key = node.getKey(i);
            if (key >= min && key < max) {
                if (!node.isLeaf())
                    if (!validateDFS((TreeNode<V>) node.getChild(i), min, key)) {
                        return false;
                    }
            } else {
                return false;
            }
            min = key;
        }
        return node.isLeaf() || validateDFS((TreeNode<V>) node.getChild(node.getKeySize()), min, max);
    }

    public void display() {
        if(root==null){
            System.out.println("B+ Tree is empty!");
        }
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

    public TreeNode<V> search(int key) {
        lock();
        root.lockNode();
        TreeNode<V> node = root;
        while (!node.isLeaf()) {
            node = (TreeNode<V>) node.getChildNode(key);
            node.lockNode();
            if (!node.isFull()) {
                if (node.unlockParentNode() == null) {
                    unlock();
                }

            }
        }

        return node;
    }

    public void insert(int key, V value) {
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
        TreeNode<V> leafNode = search(key);

        if (!leafNode.isFull()) {
            leafNode.insertValue(key, value);
            leafNode.unlockNode();
            if (leafNode.unlockParentNode() == null)
                unlock();
        } else {
            DataNode<V> newNode = new DataNode<>(value);

            TreeNode<V> newLeaf = leafNode.splitLeafNode(key, newNode);

            if (leafNode == root) {
                createNewRoot(leafNode, newLeaf);
            } else {
                leafNode.unlockNode();
                splitParentNode(leafNode.getParent(), newLeaf, newLeaf.getMinKey());
            }
        }
    }

    private void splitParentNode(TreeNode<V> parent, TreeNode<V> child, int key) {
        if (parent != null && !parent.isFull()) {
            parent.insertInternalNode(child);
            child.setParent(parent);
            parent.unlockNode();
            if (parent.unlockParentNode() == null)
                unlock();
            return;
        }
        TreeNode<V> newInternalNode = parent.splitInternalNode(key, child);

        if (parent == root) {
            createNewRoot(parent, newInternalNode);
        } else {
            parent.unlockNode();
            splitParentNode(parent.getParent(), newInternalNode, newInternalNode.getPreviousKey());
        }
    }

    private void createNewRoot(TreeNode<V> oldRoot, TreeNode<V> newNode){
        int[] tempKeys = new int[n];
        Node<V>[] tempChildren = new Node[n + 1];

        tempKeys[0] = newNode.getPreviousKey();
        tempChildren[0] = oldRoot;
        tempChildren[1] = newNode;

        root = new TreeNode<>(1, tempKeys, tempChildren, false);
        oldRoot.setParent(root);
        newNode.setParent(root);

        oldRoot.unlockNode();
        unlock();
    }
}
