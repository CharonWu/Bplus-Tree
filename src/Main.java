package src;

import src.bplus_tree.BplusTree;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
//        customTest();
//        shuffledTest();
        concurrentTest();
    }


    private static void concurrentTest() {
        System.out.println("B+-Tree concurrent test");

        ArrayList<Integer> arr = getTestArray(1000000, true);

        BplusTree<Integer> bplusTree = new BplusTree<>(3);

        Thread[] ts = new Thread[10];
        Instant start = Instant.now();
        for (int i = 0; i < 10; i++) {
            int index = i;
            Thread t = new Thread(() -> {
                int end = index * 100000 + 100000;
                for (int j = index * 100000; j < end; j++) {
                    bplusTree.insert(arr.get(j), arr.get(j));
                }
            });
            ts[i] = t;
            t.start();
        }

        try {
            for (Thread t : ts) {
                t.join();
            }
        } catch (Exception E) {
            E.printStackTrace();
        }
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: " + timeElapsed.toMillis() + " milliseconds");

        bplusTree.validate();
//        System.out.println("display B+ Tree");
//        bplusTree.display();
    }

    private static void shuffledTest() {
        System.out.println("B+-Tree test with shuffled items");

        ArrayList<Integer> arr = getTestArray(1000000, true);

        BplusTree<Integer> bplusTree = new BplusTree<>(3);

        Instant start = Instant.now();
        for (Integer integer : arr) {
            bplusTree.insert(integer, integer);
        }
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: " + timeElapsed.toMillis() + " milliseconds");
        bplusTree.validate();
//        System.out.println("display B+ Tree");
//        bplusTree.display();

    }

    private static void customTest() {
        System.out.println("B+-Tree test");
        BplusTree<Integer> bplusTree = new BplusTree<>(3);

        for (int i = 0; i < 30; i += 2) {
            System.out.println("insert " + i);
            bplusTree.insert(i, i);
        }
        for (int i = 1; i < 30; i += 2) {
            System.out.println("insert " + i);
            bplusTree.insert(i, i);
        }

        bplusTree.validate();
        System.out.println("display B+ Tree");
        bplusTree.display();
    }

    private static ArrayList<Integer> getTestArray(int size, boolean shuffle) {
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            arr.add(i);
        }
        if (shuffle)
            Collections.shuffle(arr);
        return arr;
    }
}
