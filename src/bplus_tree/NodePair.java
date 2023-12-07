package src.bplus_tree;

public class NodePair<V> {
    public TreeNode<V> lockedNode;
    public TreeNode<V> leafNode;

    /**
     * Create a pair to hold two <code>TreeNode</code> references.
     * @param lockedNode The locked node that has been modified.
     * @param leafNode The new leaf node.
     */
    public NodePair(TreeNode<V> lockedNode, TreeNode<V> leafNode) {
        this.lockedNode = lockedNode;
        this.leafNode = leafNode;
    }
}
