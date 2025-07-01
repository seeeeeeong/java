package com.example.java.thread;


import static java.lang.Thread.sleep;

public class joinSample {

    static class MyThread implements Runnable {

        @Override
        public void run() {
            System.out.println("MyThread.run() - begin");
            for (int i = 0; i < 10; i++) {
                try {
                    sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("MyThread.run() - end");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread(new MyThread(), "TestThread");
            threads[i].start();
        }

        for (int i = 0; i < 3; i++) {
            threads[i].join();
        }
        System.out.println("main() - end");
    }
}
