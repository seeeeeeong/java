package com.example.java.synchronization;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class LockFreeQueueTest {

    /** =======================
     * 노드 클래스
     * ======================= */
    static class LockFreeUser {
        String name;
        AtomicReference<LockFreeUser> next;

        LockFreeUser(String name) {
            this.name = name;
            this.next = new AtomicReference<>(null);
        }
    }

    /** =======================
     * Lock-Free 큐 (단방향)
     * ======================= */
    static class LockFreeList {
        private final LockFreeUser headNode;
        private final AtomicReference<LockFreeUser> tail;

        LockFreeList() {
            headNode = new LockFreeUser("DummyHead");
            tail = new AtomicReference<>(headNode);
        }

        /** 큐 뒤에 새 노드 추가 (CAS 사용) */
        public void push(String name) {
            LockFreeUser newUser = new LockFreeUser(name);
            while (true) {
                LockFreeUser last = tail.get();
                LockFreeUser next = last.next.get();
                if (last == tail.get()) { // tail이 변하지 않았는지 재확인
                    if (next == null) { // 마지막 노드라면
                        if (last.next.compareAndSet(null, newUser)) {
                            tail.compareAndSet(last, newUser);
                            return;
                        }
                    } else {
                        // tail이 뒤쳐져 있으니 한 칸 전진
                        tail.compareAndSet(last, next);
                    }
                }
                // CAS 실패로 반복 시 CPU 점유율 급상승 방지
                // parkNanos(n) : n 나노초만큼 현재 스레드 잠시 대기
                // -> Busy-waiting 완화, 다른 스레드에게 CPU 양보 가능
                LockSupport.parkNanos(1);
            }
        }

        /** 전체 노드 개수 세기 */
        public int getCount() {
            int count = 0;
            LockFreeUser cur = headNode.next.get();
            while (cur != null) {
                count++;
                cur = cur.next.get();
            }
            return count;
        }
    }

    /** =======================
     * 테스트용 쓰레드
     * ======================= */
    static class LockFreeTestThread extends Thread {
        private final LockFreeList list;
        LockFreeTestThread(LockFreeList db) { this.list = db; }
        @Override
        public void run() {
            for (int i = 0; i < 1_000_000; i++) {
                list.push("TEST" + i);
            }
        }
    }

    /** =======================
     * 메인 테스트
     * ======================= */
    public static void main(String[] args) throws InterruptedException {
        LockFreeList db = new LockFreeList();
        int threadCount = 5;
        LockFreeTestThread[] threads = new LockFreeTestThread[threadCount];

        long beginTime = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new LockFreeTestThread(db);
            threads[i].start();
        }
        for (int i = 0; i < threadCount; i++) {
            threads[i].join();
        }
        long endTime = System.currentTimeMillis();

        System.out.println("Duration: " + (endTime - beginTime) + " ms");
        System.out.println("getCount(): " + db.getCount());
    }
}

/*
[Lock-Free 큐 특징 및 LockSupport.parkNanos 설명]

- CAS(Compare-And-Set) 기반으로 동작
  -> 별도의 Lock 없이 멀티 스레드 동시 접근 가능 (Lock-Free)

- 경쟁이 심하면 CAS 실패 재시도로 CPU 점유율 급상승 가능

- LockSupport.parkNanos(long nanos)
  -> 현재 스레드를 지정한 나노초(nanos) 만큼 잠시 대기
  -> Busy-waiting 완화, 다른 스레드에게 CPU 양보 가능
  -> Lock-Free 구조에서 반복 CAS 실패 시 CPU 사용을 줄이기 위해 활용

- 전체 노드 개수 확인 시 getCount() 사용
*/
