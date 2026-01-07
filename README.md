## 서가영

### 1️⃣ Parallel Stream Overhead 
- Parrllel Stream : 각각의 스레드에서 스트림을 처리할 수 있도록 스트림 요소를 여러 청크로 분할한 것

#### 1. 가정한 문제 상황
   - 소규모 데이터를 처리하는 경우에는 순차 스트림이 성능 면에서 효율적이다.

#### 2. 문제 상황이 발생하는 이유
   - 스레드를 생성하고 할당하는 비용이 더 많이 들기 때문이다.

#### 3. 검증 방법
   - ArrayList를 생성하고 값을 할당한다.
   - sum 연산 수행을 sequential / parallel 하게 수행하여 실행 시간 오버헤드를 측정한다.
       - `System.currentTimeMillis();` 를 통해 현재 시각을 ms 단위로 기록한다.

#### 4. 실행 결과
**1. 데이터 개수 100,000,000개 (대규모 데이터)**

   - 기대한 결과 : sequential > parallel
<img width="608" height="250" alt="스크린샷 2026-01-07 170741" src="https://github.com/user-attachments/assets/2faa3824-d3fc-48d9-8050-82a183d591e6" />

  - 검증 결과와 기대한 결과가 일치함

2. 데이터 개수 500,000개 (소규모 데이터)
  - 기대한 결과 : sequential < parallel
      - '소규모 데이터에서는 병렬 스트림 사용이 오히려 비효율적일 것이다' 가정
  <img width="596" height="251" alt="스크린샷 2026-01-07 170941" src="https://github.com/user-attachments/assets/222f2701-ef69-4be7-aaef-6c556a4f7714" />


<br />


### 2️⃣ Overusing Intermediate Operations 
- 불필요한 중간 연산 체이닝은 오버헤드를 발생시켜 성능 비효율성을 초래한다.
  - Java Stream은 `filter().filter().map().map()` 처럼 코드를 작성해도 내부적으로 Stream Fusion을 통해 한 번의 순회(Loop)으로 합치기 때문에 for 문을 여러 번 돌지는 않는다.
  - 하지만 스테이지의 개수 자체가 늘기 때문에 메서드 호출 비용 등에 따른 연산 속도의 차이가 있다.
 
#### 1. 가정한 문제 상황
   - 중간 연산을 많이 사용하면 실행 시간이 증가할 것이다.

#### 2. 문제 상황이 발생하는 이유
   - 체이닝된 메서드들을 호출하는 상황은 호출 시간 등을 증가시켜 오버헤드를 발생시키기 때문이다.


#### 3. 검증 방법
   - 테스트용 ArrayList 데이터를 생성하고 값을 초기화 한다.
   - 연산이 분리된 경우 / 합쳐진 경우를 실행한다.
      - 각각의 경우는 메서드로 구현한다.
   - 각각의 연산이 영향을 미치지 않도록 중간에 GC를 실행하여 실행 상태를 분리한다.

#### 4. 실행 결과
- 데이터 개수 : 10,000,000개
<img width="637" height="256" alt="스크린샷 2026-01-07 172023" src="https://github.com/user-attachments/assets/d72e61f7-5bb7-4c13-a59a-5e7fd318983c" />
