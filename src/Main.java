package src;

import src.bplus_tree.BplusTree;

import java.util.ArrayList;
import java.util.Collections;

public class Main {
    public static void main(String[] args){
        customTest();
//        shuffledTest();
    }

    private static void shuffledTest(){
        System.out.println("B+-Tree test with shuffled items");

        ArrayList<Integer> arr = new ArrayList<>();
        for(int i = 0;i<1000;i++){
            arr.add(i);
        }
        Collections.shuffle(arr);

        BplusTree<Integer> bplusTree = new BplusTree<>(2);
        for(int i = 0;i<arr.size();i++){
            System.out.println("insert " + arr.get(i));
            bplusTree.insert(arr.get(i), arr.get(i));

        }

        bplusTree.validate();
        System.out.println("display B+ Tree");
        bplusTree.display();

    }
    private static void customTest(){
        System.out.println("B+-Tree test");
        BplusTree<Integer> bplusTree = new BplusTree<>(3);

        for(int i = 0;i<30;i+=2){
            System.out.println("insert " + i);
            bplusTree.insert(i, i);
        }
        for(int i = 1;i<30;i+=2){
            System.out.println("insert " + i);
            bplusTree.insert(i, i);
        }

        bplusTree.validate();
        System.out.println("display B+ Tree");
        bplusTree.display();
    }
}
