package com.example.java.thread;

import com.example.java.thread.joinSample.MyThread;

public class RaceConditionCounter {

    static int counter = 0;

    public static class MyThread implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 100000; ++i) {
                ++counter;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("main - begin");
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; ++i) {
            threads[i] = new Thread(new MyThread(), "TestThread" + i);
            threads[i].start();
        }

        for (int i = 0; i < 5; ++i) {
            threads[i].join();
        }

        System.out.println("main - end, counter: " + counter);
    }

}
