package com.example.java.thread;

import com.example.java.thread.ThreadIdName.MyThread;

public class WorkMemoryError {

    static boolean exitFlag = false;

    public static class MyThread implements Runnable {

        @Override
        public void run() {
            System.out.println("MyThread.run() - begin");
            int counter = 0;
            while (!exitFlag) {
                ++counter;
            }

            System.out.println("MyThread.run() - end, exitFlag: " + exitFlag);
            System.out.println("MyThread.run() - end, counter: " + counter);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("[main] begin");

        MyThread myThread = new MyThread();
        Thread t1 = new Thread(myThread, "TestTrhead");
        t1.start();

        Thread.sleep(100);
        exitFlag = true;
        Thread.sleep(2000);

        System.out.println("[main] end, exitFlag: " + exitFlag);
    }

}
