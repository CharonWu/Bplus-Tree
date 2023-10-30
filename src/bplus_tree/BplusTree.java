package src.bplus_tree;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BplusTree<V> {
    private TreeNode<V> root = null;
    private int n;

    private Semaphore semaphore;

    public void validate(){
        if (validateDFS(root, Integer.MIN_VALUE, Integer.MAX_VALUE)) {
            System.out.println("B+ Tree is valid!");
        }else{
            System.err.println("B+ Tree is invalid!");
        }
    }

    private boolean validateDFS(TreeNode<V> node, int min, int max){
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

    public TreeNode<V> search(int key) {
        if (root == null) return null;
        root.lockNode();
        TreeNode<V> node = root;
        while (!node.isLeaf()) {
            node = (TreeNode<V>) node.getChildNode(key);
            node.lockNode();
            if(!node.isFull()){
                node.unlockParentNode();
            }
        }

        return node;
    }

    public void insert(int key, V value) {
        if (root == null) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (root == null) {
                root = new TreeNode<>(n);
                root.setLeaf(true);
                root.insertValue(key, value);
                semaphore.release();
                return;
            }
            semaphore.release();
        }

        TreeNode<V> leafNode = search(key);

        if (!leafNode.isFull()) {
            leafNode.insertValue(key, value);
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
                newRoot.lockNode();
                root = newRoot;
                leafNode.setParent(root);
                newLeaf.setParent(root);
                newRoot.unlockNode();
            } else {
                splitParentNode(leafNode.getParent(), newLeaf, newLeaf.getMinKey());
            }

        }
        leafNode.unlockNode();
        leafNode.unlockParentNode();
    }

    private void splitParentNode(TreeNode<V> parent, TreeNode<V> child, int key) {
        if (parent != null && !parent.isFull()) {
            parent.insertInternalNode(child);
            child.setParent(parent);
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
            newRoot.lockNode();
            root = newRoot;
            parent.setParent(root);
            newInternalNode.setParent(root);
            newRoot.unlockParentNode();
        } else {
            splitParentNode(parent.getParent(), newInternalNode, newInternalNode.getPreviousKey());
        }
        parent.unlockNode();
        parent.unlockParentNode();

    }
}
