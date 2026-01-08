# 🏃 Java Stream Performance Test

## ❌ Java Stream에서 자주 하는 실수 7가지
1. collect(), forEach(), reduce() 등과 같은 Terminal Operation을 쓰지 않는 경우가 있다.
2. 데이터 구조를 바꿀 때 예상치 못한 결과가 나올 수도 있다.
3. 병렬로 처리하는 것이 항상 성능 향상에 좋은 것은 아니다. (작은 프로젝트에서는 성능을 떨어뜨릴 수도 있다.)
4. filter()나 map() 같은 중간 연산자들을 너무 많이 주렁주렁 연결하면 성능 저하(오버헤드) 가 발생할 수 있다.
5. findFirst()나 reduce() 같은 연산은 결과로 Optional 객체를 반환하는데, 이를 제대로 처리하지 않는 것이다
6. 병렬 스트림을 사용할 때 공유 변수를 동시에 바꾸면 race condition이 발생하거나 결과값이 뒤죽박죽이 될 수 있다.
7. 새로운 스트림을 리턴하는 중간 연산(Intermediate)과, 실제로 결과를 도출해 내는 최종 연산(Terminal)의 차이를 혼동하는 것이다.

## ⁉️ 7가지가 왜 문제가 될까?
### 1번 실수: Terminal Operation 미사용
- **Terminal Operation 이란?**
  > Stream의 실행 버튼으로, 최종 연산이 없다면 실행되지 않는다.

- **Terminal Operation의 종류**
    - **결과 출력 및 수행**
        - forEach(action): 요소를 하나씩 꺼내서 작업을 수행한다. 주로 출력용으로 사용된다.
    - **결과 모으기**
        - collect(Collector): List,Set,Map 등의 collection으로 변환한다. 즉, 데이터를 collection에 모을 때 사용한다.
        - toArray(): 배열로 변환한다.
    - **통계 및 계산**
        - count(): 요소의 개수를 센다. long타입으로 반환한다.
        - max(), min(): 최댓값/최솟값을 반환한다. Optional로 반환한다.
    - **조건 검사**
        - anyMatch(Predicate): 하나라도 조건을 만족하면 true를 반환한다.
        - allMatch(Predicate): 모두 조건을 만족하면 true를 반환한다.
        - noneMatch(Predicate): 모두 조건을 만족하지 않으면 true를 반환한다.
    - **검색**
        - findFirst(): 첫 번째 요소를 반환한다.
        - findAny(): 아무거나 하나 반환한다 (병렬 처리에서 findFirst보다 빠르다).

### 2번 실수: Terminal Operation 미사용
### 3번 실수: 데이터 구조를 바꿀 때 예상치 못한 결과가 나올 수도 있다.
### 4번 실수: Terminal Operation 미사용
### 5번 실수: Terminal Operation 미사용
### 6번 실수: Terminal Operation 미사용
### 7번 실수: Terminal Operation 미사용

## 실제 구현
실행 속도에 영향을 주는 3,4번 실수를 구현 해 볼 것이다.

### 1. 큰 데이터와 작은 데이터에서 순차처리와 병렬처리의 실행 속도 비교(StreamLab.java)
1. **데이터 크기**: 작은 데이터: 1,000개, 큰 데이터: 10,000,000개
2. **연산 작업**: 리스트의 각 숫자를 제곱하여 다시 리스트에 넣기

### 구현 코드
```
package streamlab;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


// 3번 예시
public class StreamLab {

    public static void main(String[] args) {
        // 1. 데이터 준비
        System.out.println("데이터 생성 중");
        // 작은 데이터 (1,000개)
        List<Integer> smallData = IntStream.rangeClosed(1, 1_000).boxed().collect(Collectors.toList());
        // 큰 데이터 (1,000만 개)
        List<Integer> largeData = IntStream.rangeClosed(1, 10_000_000).boxed().collect(Collectors.toList());
        System.out.println("데이터 준비 완료.\n");

        // ----------------------------------------------------

        System.out.println("=== 1. 작은 데이터셋 (1,000개) ===");
        runTest("Small - Sequential", smallData, false);
        runTest("Small - Parallel  ", smallData, true);

        System.out.println("\n----------------------------------------------------\n");

        System.out.println("=== 2. 큰 데이터셋 (1,000만 개) ===");
        
        runTest("Large - Sequential", largeData, false);
      
        runTest("Large - Parallel  ", largeData, true);
    }

    public static void runTest(String name, List<Integer> data, boolean isParallel) {
        long startTime = System.currentTimeMillis();

        List<Integer> result;
        if (isParallel) {
            result = data.parallelStream()
                .map(n -> {
                    return n * n;
                })
                .collect(Collectors.toList());
        } else {
            result = data.stream()
                .map(n -> {
                    return n * n;
                })
                .collect(Collectors.toList());
        }

        long endTime = System.currentTimeMillis();
        System.out.println(name + " : " + (endTime - startTime) + " ms");
    }
}
```
### 결과 분석 (Result)

| 데이터 크기 | 처리 방식 (Method) | 소요 시간 (측정마다 다르게 나옴) |
|:---:|:---:|:---|
| **작은 데이터** | **Stream (Sequential)** | 1ms |
|  | **Stream (Parallel)** | 12ms | 
| **큰 데이터** | **Stream (Sequential)** | 445ms | 
| | **Stream (Parallel)** | 336ms |
- 큰 데이터를 처리할 때에는 병렬처리 방식이 더 빠르지만 작은 데이터를 처리할 때에는 순차처리 방식이 더 빠르다.

<br>

**작은 데이터에서 병렬처리가 느린 이유**
  > 병렬처리를 하기 위해서는 새로운 스레드를 할당하고, CPU가 여러 스레드를 왔다 갔다 하는 context switching 비용이 발생한다. 데이터가 작은 경우 데이터를 순차적으로 처리하는 비용보다 context switch하는 비용이 더 크기 때문에 느리게 나온다

### 2. filter()나 map() 같은 중간 연산자들을 많이 사용했을 때의 성능 비교 (StreamLab2.java)

### 결과 분석 (Result)

| 처리 방식 (Method) | 소요 시간 (측정마다 다르게 나옴) |
|:---:|:---|
| **중간 연산자가 많을 때** | 5ms |
| **중간 연산자가 적을 때**  | 2ms |
- 중간 연산자가 적을 때 더 빨랐다.

<br>

**중간 연산자가 많을 때 더 느린 이유**
  1. 연산자를 사용할 때마다 새로운 스트림 객체가 힙 메모리에 계속 만들어진다. 객체를 생성하며 드는 비용으로 인해 속도가 느려진다.
  2. 연산자가 나뉘어 있으면 데이터가 지나갈 때마다 함수를 호출해야한다. 즉, 함수 호출 비용이 든다.
