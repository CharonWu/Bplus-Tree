package src;

import src.bplus_tree.BplusTree;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args){
//        customTest();
        shuffledTest();
//        concurrentTest();

//        semaphoreTest();
    }

    private static void semaphoreTest(){
        Semaphore semaphore = new Semaphore(0);
        System.out.println(semaphore.availablePermits());
        semaphore.release();
        System.out.println(semaphore.availablePermits());
        semaphore.release();
        System.out.println(semaphore.availablePermits());
        semaphore.release();
        System.out.println(semaphore.availablePermits());
        semaphore.release();
        System.out.println(semaphore.availablePermits());

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(semaphore.availablePermits());

    }

    private static void concurrentTest(){
        System.out.println("B+-Tree concurrent test");

        ArrayList<Integer> arr = new ArrayList<>();
        for(int i = 0;i<1000000;i++){
            arr.add(i);
        }
        Collections.shuffle(arr);

        BplusTree<Integer> bplusTree = new BplusTree<>(3);

        Thread[] ts = new Thread[2];
        Instant start = Instant.now();
        for(int i = 0;i<2;i++){
            int index = i;
            Thread t = new Thread(() -> {
                int end = index*500000+500000;
                for(int j = index *500000; j< end; j++){
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
        }
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: "+ timeElapsed.toMillis() +" milliseconds");

        bplusTree.validate();
//        System.out.println("display B+ Tree");
//        bplusTree.display();
    }

    private static void shuffledTest(){
        System.out.println("B+-Tree test with shuffled items");

        ArrayList<Integer> arr = new ArrayList<>();
        for(int i = 0;i<1000000;i++){
            arr.add(i);
        }
        Collections.shuffle(arr);

        BplusTree<Integer> bplusTree = new BplusTree<>(3);

        Instant start = Instant.now();
        for(int i = 0;i<arr.size();i++){
            bplusTree.insert(arr.get(i), arr.get(i));
        }
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: "+ timeElapsed.toMillis() +" milliseconds");
        bplusTree.validate();
//        System.out.println("display B+ Tree");
//        bplusTree.display();

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
