package src.bplus_tree;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BplusTree<V> {
    private TreeNode<V> root = null;
    private final int n;
    private final Semaphore semaphore;

    private String threadId;

    /**
     * This method trys to lock the B+ tree, and record the name of current thread.
     *
     * @throws InterruptedException on getting the name of current thread.
     */
    private void lock() {
        try {
            semaphore.acquire();
            threadId = Thread.currentThread().getName();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method will unlock the B+ tree if the B+ tree is locked by the current thread.
     */
    private void unlock() {
        if (semaphore.availablePermits() == 0 && threadId.equals(Thread.currentThread().getName())) {
            threadId = "-1";
            semaphore.release();
        }

    }

    /**
     * This method is used to validate the B+ tree and print the result.
     */
    public void validate() {
        if (validateDFS(root, Integer.MIN_VALUE, Integer.MAX_VALUE)) {
            System.out.println("B+ Tree is valid!");
        } else {
            System.err.println("B+ Tree is invalid!");
        }
    }

    /**
     * This method is used by method <code>validate()</code> to recursively check if subtrees are also B+ tree.
     *
     * @param node The node which is going to be validated.
     * @param min  The smallest key value for this node.
     * @param max  The largest key value for this node.
     * @return boolean result.
     */
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

    /**
     * This method will print the structure of the B+ tree.
     */
    public void display() {
        if (root == null) {
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

    /**
     * This constructor init the B+ tree. It sets the maximum value for the number of keys, and init the semaphore with a value of 1
     *
     * @param n The maximum number of keys.
     */
    public BplusTree(int n) {
        this.semaphore = new Semaphore(1);
        this.n = n;
    }

    /**
     * This method will search the node with the corresponding key, and return the leaf node which may hold the key and value.
     * @param key The key of the node.
     * @return TreeNode The leaf node that may contain the key.
     */
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

    /**
     * This method will look for the leaf node to insert the new value. This method will use double check locking to create a new root if the B+ tree is empty.
     * This method will call <code>splitParentNode()</code> to split the parent TreeNode if it is full.
     * @param key The key of the node
     * @param value The value of the node
     */
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

    /**
     * This method will insert the new key and the child TreeNode into the parent TreeNode if the parent is not full.
     * Otherwise, the parent needs to split and try to call this method again to try to insert its sibling TreeNode into its parent TreeNode.
     * @param parent
     * @param child
     * @param key
     */
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

    /**
     * This method will try to create a new root if the original root is full and needs to be splitted.
     * @param oldRoot The original root of the B+ Tree.
     * @param newNode The sibling TreeNode of the original root.
     */
    private void createNewRoot(TreeNode<V> oldRoot, TreeNode<V> newNode) {
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
