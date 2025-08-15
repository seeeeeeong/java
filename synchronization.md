# 스레드 동기화 방식 비교: SpinLock, ReentrantLock, Lock-Free Queue

멀티스레드 환경에서 **동기화(Synchronization)**는 성능과 안정성 측면에서 매우 중요합니다.  
이번 글에서는 자바에서 흔히 사용하는 동기화 방식들을 비교하고, 언제 어떤 방식을 쓰는 것이 좋은지 정리합니다.

---

## 1. 동기화 방식 개요

### 1.1 `synchronized` / `ReentrantLock`
- **장점**
    - 구현이 쉽고 안정적
    - 재진입 가능(ReentrantLock)
    - 경쟁이 많아도 안전하게 대기
- **단점**
    - 스레드 상태 전환(블로킹)으로 인한 오버헤드
    - 임계 영역이 짧으면 오히려 느릴 수 있음
- **추천 상황**
    - 임계 영역이 비교적 길거나 복잡할 때
    - 코드 안정성이 최우선일 때
    - 경쟁이 자주 발생하는 환경

---

### 1.2 CAS 기반 SpinLock
- **장점**
    - 스레드 상태 전환 없이 동작 → 짧은 임계 영역에서 빠름
    - Lock-Free / Wait-Free 구조 구현 가능
- **단점**
    - 경쟁이 심하면 반복 CAS → CPU 점유율 급상승
    - 구현이 복잡하고 디버깅 어려움
- **추천 상황**
    - 임계 영역이 아주 짧고 빠르게 처리될 때
    - 경쟁이 많지 않은 환경
    - 고성능, low-latency가 필요할 때

---

### 1.3 SpinLock + `LockSupport.parkNanos()`
- **장점**
    - SpinLock의 CPU 점유율 완화
    - CAS 실패 시 잠시 대기 → Busy-waiting 감소
- **단점**
    - 약간의 지연 발생
    - 경쟁이 심하면 성능 저하 가능
- **추천 상황**
    - Lock-Free 구조 유지하면서 CPU 점유율도 고려할 때
    - 짧은 임계 영역 + 멀티코어 환경

**`LockSupport.parkNanos(long nanos)` 설명**
- 현재 스레드를 지정한 나노초(nanos) 만큼 잠시 대기
- Busy-waiting 완화, 다른 스레드에게 CPU 양보
- Lock-Free 구조에서 반복 CAS 실패 시 CPU 사용을 줄이기 위해 활용

---

### 1.4 Lock-Free Queue / AtomicReference
- **장점**
    - 완전히 Lock-Free → 스레드 전환 없이 동시 접근 가능
    - 높은 동시성 처리 가능
- **단점**
    - 구현이 어렵고 디버깅 까다로움
    - CAS 반복 실패 시 성능 저하 가능
- **추천 상황**
    - 고성능 멀티스레드 환경
    - 짧고 단순한 데이터 구조
    - 안정성보다 성능/동시성이 최우선

---

## 2. 동기화 방식별 비교표

| 환경                         | 추천 방식                          |
|-------------------------------|-----------------------------------|
| 안정성 최우선, 경쟁 많음       | synchronized / ReentrantLock      |
| 임계 영역 짧고 초고속 필요     | CAS 기반 SpinLock                 |
| CPU 점유율 고려한 Lock-Free    | SpinLock + parkNanos              |
| 완전한 고성능 동시성           | Lock-Free Queue / AtomicReference |

💡 팁:
- 짧은 임계 영역 → SpinLock / CAS가 빠름
- 긴 임계 영역 → 블로킹 방식(ReentrantLock) 안정적
- 실제 시스템에서는 대부분 ReentrantLock / synchronized 사용
- 성능이 문제되면 Lock-Free 구조로 튜닝

---
