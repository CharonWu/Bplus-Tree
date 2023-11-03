package src.bplus_tree;

public class NodePair<V> {
    public TreeNode<V> lockedNode;
    public TreeNode<V> leafNode;

    public NodePair(TreeNode<V> lockedNode, TreeNode<V> leafNode) {
        this.lockedNode = lockedNode;
        this.leafNode = leafNode;
    }
}
