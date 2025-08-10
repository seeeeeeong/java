package com.example.java.synchronization;

public class SynchronizedCounter {

    static int counter = 0;
    public synchronized static void incCounter() {
        ++counter;
    }

    public static class MyThread implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 100000; ++i) {
                incCounter();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("main - begin");

        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; ++i) {
            threads[i] = new Thread(new MyThread(), "TestThread" + i);
            threads[i].start();
        }

        for (int i = 0; i < 3; ++i) {
            threads[i].join();
        }

        System.out.println("main - end, counter: " + counter);
    }

}
