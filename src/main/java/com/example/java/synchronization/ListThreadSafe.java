package com.example.java.synchronization;

/**
 * ListThreadSafe
 * - synchronized 키워드로 스레드 안전하게 구현한 연결 리스트 예제
 * - Producer/Consumer 스레드가 동시에 접근해도 데이터 무결성 유지
 */
public class ListThreadSafe {

    /** 전역 연결 리스트 인스턴스 */
    private static final MyList db = new MyList();

    public static void main(String[] args) throws InterruptedException {

        // 소비자 스레드
        Thread consumer = new Thread(() -> {
            System.out.println("Consumer - begin");
            UserData node;
            while (true) {
                node = db.removeAtHead();
                if (node == null) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        System.out.println("Consumer - interrupted");
                        break;
                    }
                }
            }
            System.out.println("Consumer - end");
        });

        // 생산자 스레드
        Thread producer = new Thread(() -> {
            System.out.println("Producer - begin");
            for (int i = 0; i < 10_000_000; ++i) {
                db.appendNode("tester" + i);
            }
            System.out.println("Producer - end");
        });

        // Producer 실행
        producer.start();
        Thread.sleep(1000); // 1초 후 리스트 크기 출력
        System.out.println("List size: " + db.size());

        // Consumer 실행
        consumer.start();
        Thread.sleep(5000); // 5초 후 종료
        consumer.interrupt();

        // 남은 데이터 출력
        db.printAll();
    }

    /**
     * 연결 리스트의 노드 클래스
     */
    static class UserData {
        String name;
        UserData prev;
        UserData next;

        UserData(String name) {
            this.name = name;
        }
    }

    /**
     * 스레드 안전한 양방향 연결 리스트
     */
    static class MyList {
        protected int counter = 0;
        protected UserData head = new UserData("DummyHead");
        protected UserData tail = new UserData("DummyTail");

        MyList() {
            head.next = tail;
            tail.prev = head;
        }

        public synchronized boolean appendNode(String name) {
            UserData newUser = new UserData(name);
            newUser.prev = tail.prev;
            newUser.next = tail;
            tail.prev.next = newUser;
            tail.prev = newUser;
            ++counter;
            return true;
        }

        public synchronized UserData removeAtHead() {
            if (isEmpty()) return null;
            UserData node = head.next;
            node.next.prev = head;
            head.next = node.next;
            --counter;
            return node;
        }

        public synchronized boolean isEmpty() {
            return head.next == tail;
        }

        public synchronized int size() {
            return counter;
        }

        public synchronized void printAll() {
            System.out.println("\n-----------------------");
            System.out.println("Counter: " + counter);

            UserData tmp = head.next;
            while (tmp != tail) {
                System.out.println(tmp.name);
                tmp = tmp.next;
            }

            System.out.println("-----------------------\n");
        }
    }
}
