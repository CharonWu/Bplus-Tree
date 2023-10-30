package src.bplus_tree;

class TreeNode<V> extends Node {
    private int[] keys;
    private TreeNode<V> parent;

    private Node<V>[] children;
    private int keySize;

    private boolean isLeaf = false;

    private int previousKey;

    protected void display() {
        System.out.print('(');
        System.out.print(parent == null ? "null" : parent.getMinKey());
        System.out.print(')');
//        System.out.print('(');
//        System.out.print(semaphore.availablePermits());
//        System.out.print(')');

        for (int i = 0; i < keySize; i++) {
            System.out.print(keys[i]);
            if(isLeaf){
                System.out.print('[');
                System.out.print(((DataNode<V>) children[i]).getData());
                System.out.print(']');
            }
            System.out.print(' ');

        }

        System.out.print('|');

    }

    protected TreeNode(int n) {
        this(n, null);
    }

    protected TreeNode(int n, TreeNode<V> parent) {
        this.keys = new int[n];
        this.children = new Node[n + 1];
        this.parent = parent;
        this.keySize = 0;
    }

    protected TreeNode(int keySize, int[] keys, Node<V>[] children, boolean isLeaf) {
        this.keys = keys;
        this.children = children;
        this.parent = null;
        this.keySize = keySize;
        this.isLeaf = isLeaf;
    }

    public void setLeaf(boolean type) {
        this.isLeaf = type;
    }

    public boolean isLeaf() {
        return this.isLeaf;
    }

    public int getPreviousKey() {
        return previousKey;
    }

    public void setPreviousKey(int key) {
        previousKey = key;
    }

    public TreeNode<V> getParent() {
        return this.parent;
    }

    public void setParent(TreeNode<V> parent) {
       this.parent=parent;
    }

    public void setParentForChildren(){
        for(int i = 0;i<keySize+1;i++){
            ((TreeNode<V>)children[i]).setParent(this);
        }
    }

    public boolean isFull() {
        return keySize == keys.length;
    }

    public int getMinKey() {
        return keys[0];
    }

    public int getMaxKey() {
        return keys[keySize - 1];
    }

    public Node<V> getChild(int index) {
        return children[index];
    }

    public Node<V> getChildNode(int key) {
        for (int i = 0; i < keySize; i++) {
            if (keys[i] >= key) {
                return children[i];
            }
        }
        return children[keySize];
    }

    public int getChildrenSize() {
        return keySize + 1;
    }

    public int getKeySize(){
        return keySize;}

    public int getKey(int index) {
        return keys[index];
    }

    public boolean containsKey(int key) {
        for (int i = 0; i < keys.length; i++) {
            if (key == keys[i]) {
                return true;
            }
        }

        return false;
    }


    public void insertValue(int key, V value) {
        int insertIndex = 0;

        while (insertIndex < keySize && keys[insertIndex] < key) {
            insertIndex += 1;
        }
        for (int i = keySize; i > insertIndex; i--) {
            keys[i] = keys[i - 1];
            children[i] = children[i - 1];
        }
        keys[insertIndex] = key;
        children[insertIndex] = new DataNode<>(value);
        keySize += 1;
    }

    public void insertInternalNode(TreeNode<V> node) {
        int key = node.getPreviousKey();

        int keyIndex = 0;
        while (keyIndex < keySize && keys[keyIndex] < key) {
            keyIndex += 1;
        }
        for (int i = keySize; i > keyIndex; i--) {
            keys[i] = keys[i - 1];
            children[i + 1] = children[i];
        }

        keys[keyIndex] = key;
        children[keyIndex + 1] = node;
        keySize += 1;

    }

    public TreeNode<V> splitLeafNode(int key, Node<V> node) {
        int n = keys.length;

        int[] tempKeys = new int[n + 1];
        int[] newKeys = new int[n];

        Node<V>[] tempChildren = new Node[n + 2];
        Node<V>[] newChildren = new Node[n + 1];

        int leftSize = (n + 1) / 2;
        int rightSize = n + 1 - leftSize;

        int insertIndex = 0;
        while (insertIndex < keySize && keys[insertIndex] < key) {
            tempKeys[insertIndex] = keys[insertIndex];
            tempChildren[insertIndex] = children[insertIndex];
            insertIndex += 1;
        }
        tempKeys[insertIndex] = key;
        tempChildren[insertIndex++] = node;

        while (insertIndex <=keySize) {
            tempKeys[insertIndex] = keys[insertIndex-1];
            tempChildren[insertIndex] = children[insertIndex-1];
            insertIndex += 1;
        }

        this.keySize = leftSize;
        for (int i = 0; i < this.keySize; i++) {
            keys[i] = tempKeys[i];
            children[i] = tempChildren[i];
        }

        for (int i = leftSize; i < tempKeys.length; i++) {
            newKeys[i - leftSize] = tempKeys[i];
            newChildren[i - leftSize] = tempChildren[i];
        }

        TreeNode<V> newNode = new TreeNode<>(rightSize, newKeys, newChildren, true);
        newNode.setPreviousKey(tempKeys[leftSize]);
        return newNode;
    }

    public TreeNode<V> splitInternalNode(int key, TreeNode<V> node) {
        int n = keys.length;

        int[] tempKeys = new int[n + 1];
        Node<V>[] tempChildren = new Node[n + 2];

        int keyIndex = 0;
        int childIndex = 0;
        while (keyIndex < keySize && keys[keyIndex] < key) {
            tempKeys[keyIndex] = keys[keyIndex++];
            tempChildren[childIndex] = children[childIndex++];
        }

        tempKeys[keyIndex++] = key;
        tempChildren[childIndex] = children[childIndex++];
        tempChildren[childIndex++] = node;

        while (keyIndex <= keySize) {
            tempKeys[keyIndex] = keys[keyIndex++-1];
            tempChildren[childIndex] = children[childIndex++-1];
        }

        int leftSize = (n + 1) / 2;
        int rightSize = n - leftSize;

        int[] newKeys = new int[n];
        Node<V>[] newChildren = new Node[n + 1];

        this.keySize = leftSize;
        for (int i = 0; i < leftSize; i++) {
            keys[i] = tempKeys[i];
            children[i] = tempChildren[i];
        }
        children[leftSize] = tempChildren[leftSize];
        this.setParentForChildren();

        for (int i = leftSize + 1, j = 0; i < tempKeys.length; i++, j++) {
            newKeys[j] = tempKeys[i];
            newChildren[j] = tempChildren[i];
        }
        newChildren[rightSize] = tempChildren[n + 1];

        TreeNode<V> newInternalNode =  new TreeNode<>(rightSize, newKeys, newChildren, false);

        newInternalNode.setPreviousKey(tempKeys[leftSize]);
        newInternalNode.setParentForChildren();

        return newInternalNode;
    }

    public void unlockParentNode(){
        TreeNode<V> pNode = parent;

        while (pNode!=null && pNode.unlockNode()){
            pNode = pNode.parent;
        }
    }

}
