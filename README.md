# Java Stream Performance Test
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



