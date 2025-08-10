package com.example.java.synchronization;

public class CounterObject {
    static class MyCounter {
        private int counter = 0;

        public int getCounter() {
            return counter;
        }

        // 동기화 없는 증가 (Thread-safe X)
        public void incCounter() {
            ++counter;
        }

        // 동기화 블록으로 증가 (Thread-safe)
        public void syncIncCounter() {
            synchronized (this) { // MyCounter 인스턴스를 락으로 사용
                ++counter;
            }
        }
    }

    static class MyThread extends Thread {
        private final MyCounter counter;

        MyThread(MyCounter cnt) {
            this.counter = cnt;
        }

        // 현재 MyThread 인스턴스를 락으로 사용 → 각 스레드마다 락이 다름 → 공유 보호 불가
        synchronized void incMyThread() {
            counter.incCounter();
        }

        // 클래스 레벨 락 사용 → 모든 MyThread 인스턴스가 하나의 락 공유
        static synchronized void incStatic(MyThread myThread) {
            myThread.counter.incCounter();
        }

        @Override
        public void run() {
            // 안전한 방법 → MyCounter.syncIncCounter() 사용
            for (int i = 0; i < 100_000; ++i) {
                counter.syncIncCounter();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("main - begin");

        MyCounter cnt = new MyCounter();

        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; ++i) {
            threads[i] = new MyThread(cnt);
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("main - end, counter: " + cnt.getCounter());
    }
}
