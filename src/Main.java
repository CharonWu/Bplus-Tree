package src;

import src.bplus_tree.BplusTree;

public class Main {
    public static void main(String[] args){
        System.out.println("B+-Tree test");
        BplusTree<Integer> bplusTree = new BplusTree<>(3);

        for(int i = 0;i<100;i++){
            System.out.println("insert " + i);
            bplusTree.insert(i, i);
        }

        System.out.println("display B+ Tree");
        bplusTree.display();
    }
}
