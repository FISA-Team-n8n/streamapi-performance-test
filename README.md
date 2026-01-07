# 🔥 Java Stream Performance Test
Java Stream 사용 시 발생할 수 있는 성능 이슈를 직접 실험하고 비교하기 위한 예제 프로젝트입니다.  
특히 다음 두 가지 상황에 대해 코드 및 실행 속도를 비교했습니다.

- Parallel Stream 사용 시 오버헤드 문제
- Intermediate Operation을 과도하게 사용했을 때의 영향
  <br><br>
## 1번 병렬 스트림 실행 속도 측정
### ❓ 실행 목적
Parallel Stream이 항상 성능 향상을 보장하지 않으며,  
작은 데이터셋이나 가벼운 연산에서는 오히려 오버헤드가 발생할 수 있음을 확인합니다.
<br>
- 📝 TimeTest.java
<br>

### 1. 테스트 결과
<img width="788" height="163" alt="image" src="https://github.com/user-attachments/assets/8017c2ae-9cc3-4110-9196-45a41a403ae3" />

<br>

### ✅ 작은 데이터셋에서는 다수의 스레드를 생성하는 비용이 연산 비용보다 큼
<br>

### Why?


- 병렬 스트림은 내부적으로 ForkJoinPool을 사용

- 스레드 분할, 작업 분배, 병합 비용이 발생

- 하지만 연산이 너무 가볍고 데이터가 적음 → 오버헤드가 더 큼
<br>
➡️ 여러 스레드가 동원되지만 성능 이득은 없음! 병렬로 나눌 필요가 없는 일을 굳이 나눴다

<br><br>
## 2번 중간 연산과다 속도 측정
### ❓ 실행 목적
Stream 파이프라인에서 Intermediate Operation을 과도하게 사용할 경우
성능과 가독성에 어떤 영향을 주는지 비교합니다.
<br>
- 📝StreamPerformanceTest.java
<br>

### 2. 테스트 결과
<img width="793" height="178" alt="image" src="https://github.com/user-attachments/assets/e25cf526-ca01-441a-9ee2-e9cd5ba742bc" />

<br>

### ✅ filter 연산 통합으로 Stream pipeline 단순화

### Why?

- filter를 여러 번 사용 → 연산 단계 증가 → 스트림 파이프라인이 불필요하게 길어짐

- 대량 데이터 처리 시 미세한 성능 저하
<br>
➡️ 결과는 같지만 가독성 저하, 대량 데이터에서는 성능 저하




