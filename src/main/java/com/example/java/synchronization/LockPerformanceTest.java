package com.example.java.synchronization;

import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;

/**
 * 동기화 방식별 성능 비교 테스트
 * - SpinLock (CAS)
 * - SpinLockBool + parkNanos
 * - ReentrantLock
 */
public class LockPerformanceTest {

    /** =======================
     * 1. CAS 기반 SpinLock
     * ======================= */
    static class SpinLock {
        private final AtomicReference<Thread> owner = new AtomicReference<>();

        public void lock() {
            Thread currentThread = Thread.currentThread();
            // CAS로 락 획득 시도 (성공할 때까지 반복)
            while (!owner.compareAndSet(null, currentThread)) {
                // 바쁜 대기(Spin)
            }
        }

        public void unlock() {
            Thread currentThread = Thread.currentThread();
            owner.compareAndSet(currentThread, null);
        }
    }

    /** =======================
     * 2. parkNanos 기반 SpinLock
     * ======================= */
    static class SpinLockBool {
        private final AtomicBoolean owner = new AtomicBoolean(false);

        public void lock() {
            // false -> true 전환 성공할 때까지 반복
            while (!owner.compareAndSet(false, true)) {
                LockSupport.parkNanos(1); // 짧게 대기하여 CPU 점유율 완화
            }
        }

        public void unlock() {
            owner.set(false);
        }
    }

    /** =======================
     * 연결 리스트 노드
     * ======================= */
    static class UserData {
        String name;
        UserData next;
        UserData(String name) { this.name = name; }
    }

    /** =======================
     * 동기화 방식별 MyList
     * ======================= */
    static class MyList {
        private final Object lockImpl; // 사용할 락 객체
        private final AtomicInteger counter = new AtomicInteger();
        private final UserData head = new UserData("DummyHead");

        MyList(Object lockImpl) {
            this.lockImpl = lockImpl;
        }

        public int size() {
            return counter.get();
        }

        public boolean appendNode(String name) {
            lock();
            try {
                UserData newUser = new UserData(name);
                newUser.next = head.next;
                head.next = newUser;
                counter.incrementAndGet();
                return true;
            } finally {
                unlock();
            }
        }

        /** Lock 획득 */
        private void lock() {
            if (lockImpl instanceof SpinLock) {
                ((SpinLock) lockImpl).lock();
            } else if (lockImpl instanceof SpinLockBool) {
                ((SpinLockBool) lockImpl).lock();
            } else if (lockImpl instanceof ReentrantLock) {
                ((ReentrantLock) lockImpl).lock();
            } else if (lockImpl instanceof Object) {
                // synchronized 용도
                // 아무것도 안 함 — appendNode에서 synchronized 블록 처리 가능
            }
        }

        /** Lock 해제 */
        private void unlock() {
            if (lockImpl instanceof SpinLock) {
                ((SpinLock) lockImpl).unlock();
            } else if (lockImpl instanceof SpinLockBool) {
                ((SpinLockBool) lockImpl).unlock();
            } else if (lockImpl instanceof ReentrantLock) {
                ((ReentrantLock) lockImpl).unlock();
            }
        }
    }

    /** =======================
     * 쓰레드
     * ======================= */
    static class TestThread extends Thread {
        private final MyList list;
        TestThread(MyList db) { this.list = db; }
        @Override
        public void run() {
            for (int i = 0; i < 1_000_000; i++) {
                list.appendNode("TEST" + i);
            }
        }
    }

    /** =======================
     * 메인: 성능 테스트
     * ======================= */
    public static void main(String[] args) throws InterruptedException {
        testLock("SpinLock (CAS)", new SpinLock());
        testLock("SpinLockBool (parkNanos)", new SpinLockBool());
        testLock("ReentrantLock", new ReentrantLock());
    }

    private static void testLock(String name, Object lockImpl) throws InterruptedException {
        MyList db = new MyList(lockImpl);
        int threadCount = 10;
        TestThread[] threads = new TestThread[threadCount];

        long start = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) threads[i] = new TestThread(db);
        for (int i = 0; i < threadCount; i++) threads[i].start();
        for (int i = 0; i < threadCount; i++) threads[i].join();
        long end = System.currentTimeMillis();

        System.out.println(name + " -> Duration: " + (end - start) + " ms, Size: " + db.size());
    }
}

/*
[동기화 방식별 특징 정리]
1. SpinLock (CAS)
   - 스레드 전환 없이 짧은 임계영역에서 매우 빠른 성능
   - 경쟁이 심하면 CPU 점유율이 급상승하여 비효율 가능

2. SpinLockBool + parkNanos
   - SpinLock의 단점을 보완하여 CPU 점유율을 줄임
   - 대신 lock 획득 지연(약간의 대기 시간) 발생

3. ReentrantLock / synchronized
   - 안정적이며 코드 구현이 간단
   - 스레드 상태 전환(블로킹)으로 인한 오버헤드 존재
*/
