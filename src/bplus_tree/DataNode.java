package src.bplus_tree;

class DataNode<V> extends Node<V> {
    private V data;

    public DataNode(V data){
        this.data = data;
    }

    public V getData() {
        return data;
    }

    public void setData(V data){
        this.data = data;
    }
}
