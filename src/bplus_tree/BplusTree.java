package src.bplus_tree;

import java.util.LinkedList;
import java.util.Queue;

public class BplusTree<V> {
    private TreeNode<V> root = null;
    private int n;

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
        this.n = n;
    }

    public TreeNode<V> search(int key) {
        if (root == null) return null;
        TreeNode<V> node = root;
        while (!node.isLeaf()) {
            node = (TreeNode<V>) node.getChildNode(key);
        }

        return node;
    }

    public void insert(int key, V value) {
        if (root == null) {
            root = new TreeNode<>(n);
            root.setLeaf(true);
            root.insertValue(key, value);
            return;
        }

        TreeNode<V> leafNode = search(key);

        if (!leafNode.isFull()) {
            leafNode.insertValue(key, value);
        } else {
            System.out.println("need t osplit leaf node");
            DataNode<V> newNode = new DataNode<>(value);

            TreeNode<V> newLeaf = leafNode.splitLeafNode(key, newNode);
//            System.out.println("need to split leaf node");
//            leafNode.display();
//            newLeaf.display();

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
            } else {
                System.out.println("need to try splitting parent node for key "+key);
                splitParentNode(leafNode.getParent(), newLeaf, newLeaf.getMinKey());
            }

        }
    }

    private void splitParentNode(TreeNode<V> parent, TreeNode<V> child, int key) {
        if (parent != null && !parent.isFull()) {
            System.out.println("no need to split parent node for key "+key);

            parent.insertInternalNode(child);
            child.setParent(parent);
            return;
        }
        System.out.println("need to split parent node for key "+key);

        TreeNode<V> newInternalNode = parent.splitInternalNode(key, child);
//        child.setParent(newInternalNode);

        if (parent == root) {
            System.out.println("split root and create new root");
            int[] tempKeys = new int[n];
            Node<V>[] tempChildren = new Node[n + 1];

            tempKeys[0] = newInternalNode.getPreviousKey();
            tempChildren[0] = parent;
            tempChildren[1] = newInternalNode;
            TreeNode<V> newRoot = new TreeNode<>(1, tempKeys, tempChildren, false);
            root = newRoot;
            parent.setParent(root);
            newInternalNode.setParent(root);
        } else {
            splitParentNode(parent.getParent(), newInternalNode, newInternalNode.getPreviousKey());
        }

    }
}
