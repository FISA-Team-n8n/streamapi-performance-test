# 🏃 Java Stream Performance Test

> 진행 기간 : 2026.01.07 ~ 2026.01.12

<br/>

## 👩🏻‍💻 팀원 소개

| <img src="https://avatars.githubusercontent.com/u/178015712?v=4" width="150" height="150"/> | <img src="https://avatars.githubusercontent.com/u/118096607?v=4" width="150" height="150"/> | <img src="https://avatars.githubusercontent.com/u/89902255?v=4" width="150" height="150"/> | 
| :-----------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------: |  
|                 권순재<br/>[@caminobelllo](https://github.com/Soooonnn)                 |                       서가영<br/>[@caminobelllo](https://github.com/caminobelllo)                       |                유예원<br/>[@Yewon0106](https://github.com/Yewon0106)                |   


<br />


## ⚠️ Java Stream에서 자주 하는 실수 7가지 ⚠️

1. collect(), forEach(), reduce() 등과 같은 Terminal Operation을 쓰지 않는 경우가 있다.
2. 데이터 구조를 바꿀 때 예상치 못한 결과가 나올 수도 있다.
3. 병렬로 처리하는 것이 항상 성능 향상에 좋은 것은 아니다. (작은 프로젝트에서는 성능을 떨어뜨릴 수도 있다.)
4. filter()나 map() 같은 중간 연산자들을 너무 많이 주렁주렁 연결하면 성능 저하(오버헤드) 가 발생할 수 있다.
5. findFirst()나 reduce() 같은 연산은 결과로 Optional 객체를 반환하는데, 이를 제대로 처리하지 않는 것이다
6. 병렬 스트림을 사용할 때 공유 변수를 동시에 바꾸면 race condition이 발생하거나 결과값이 뒤죽박죽이 될 수 있다.
7. 새로운 스트림을 리턴하는 중간 연산(Intermediate)과, 실제로 결과를 도출해 내는 최종 연산(Terminal)의 차이를 혼동하는 것이다.


<br />

## ⁉️ 7가지가 왜 문제가 될까?

### 1️⃣ Terminal Operation 미사용

> **스트림 파이프라인(filter, map 등)은 정의했지만, 정작 실행을 트리거하는 최종 연산을 호출하지 않은 실수**
>

**🚨 왜 문제가 될까?**

Stream의 실행 버튼으로, 최종 연산이 없다면 실행되지 않는다.

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
- **스트림 파이프라인이란?**

데이터가 소스에서 시작해 최종 결과로 이어지는 **일련의 처리 과정**을 의미한다.

<br/>

### 2️⃣ 데이터 구조를 바꿀 때 발생할 수 있는 예상치 못한 결과

> **스트림 파이프라인 내부(forEach, peek 등)에서 원본 데이터 소스의 요소를 직접 삭제하거나 추가하려고 하는 실수**
> 

**🚨 왜 문제가 될까?**

1. **에러 발생 (ConcurrentModificationException):** 리스트를 훑어보고 있는 도중에 내용을 넣거나 빼버리면 **즉시 에러를 발생**하고 프로그램이 멈춘다.
2. **Side-effects(부수 효과) 발생:** 스트림은 "원본 데이터를 변경하지 않는다"는 원칙을 따를 때 가장 강력하다. 원본을 건드리면 코드의 예측이 어려워지고 병렬 처리 시 버그를 유발할 수 있다.

**💡 해결책**

원본의 데이터 구조를 변경하지 말고 **조건에 맞는 새로운 collection을 생성**해야 한다. (마지막에 .collect(Collectors.toList())를 사용)

- **Side-effect(부수 효과)란?**

함수가 결과값만 만들어내는 것이 아니라, 함수 외부의 상태(변수, 리스트 등)를 변경하는 행위를 말한다.

<br/>

### 3️⃣ 병렬처리의 Overhead

> **데이터의 양이 적거나 연산이 단순한 경우에도 무조건 .parallelStream()을 사용하는 실수**
> 

**🚨 왜 문제가 될까?**

병렬처리가 항상 좋은 performance를 주는 것은 아니다. 작은 프로젝트에서는 병렬의 overhead가 더 클 수도 있기 때문에 잘 선택해야한다.

**💡 해결책**

무조건 병렬 처리를 쓰지 말고, NQ 모델을 고려해야 한다. **데이터의 양(N)이 매우 많거나 요소당 처리 비용(Q)이 비싼 경우에만 .parallelStream()을 사용**하고, 그 외에는 순차처리를 사용하는 것이 더 효율적이다.

<br/>

### 4️⃣ 중간 연산자 남발 (Chaining)

> **filter()나 map()과 같은 중간 연산자를 필요 이상으로 길게 연결하는 것**
> 

**🚨 왜 문제가 될까**

1. **가독성 저하**: 코드가 불필요하게 길어져 한눈에 로직을 파악하기가 어렵다.
2. **파이프라인 비용**: 각 중간 연산마다 새로운 스트림 단계 객체가 생성되므로, 초기화 비용이 증가한다.

**💡 해결책**

1. **조건 통합:** filter()를 길게 달지 말고, && (AND 연산자)를 써서 **조건을 한 번에 묶어서 검사**한다.
2. **메소드 분리:** 로직이 복잡해지면 스트림 괄호 안에 다 넣지 말고, **별도의 메소드**로 추출하여 깔끔하게 호출한다.

<br/>

### 5️⃣ Optional 값의 부적절한 처리

> **Optional을 반환하는 연산을 사용한 후에 값이 존재하는지 확인하지 않고 .get()을 호출하는 실수**
> 

**🚨 왜 문제가 될까?**

만약 비어있는 Optional 객체가 반환되었을 때, .get()을 호출하면 NoSuchElementException이 발생하며 프로그램이 비정상 종료된다.

- **Optional을 반환하는 연산의 예시**
1. **검색**: findAny(), findFirst(),
2. **리덕션**: reduce()
3. **최댓값/최솟값**: max(Comparator), min(Comparator)

**💡 해결책**

값을 꺼내기 전에 반드시 **.isPresent()로 값이 있는지 확인**하거나, **orElse()**, ifPresent() 같은 안전한 메서드를 사용해야 한다.

- **Optional이 뭘까?**

Wrapper Class로, 어떤 객체를 바로 쓰지 않고 한 번 감싸서 보호하는 역할을 한다. **있을 수도 있고, 없을 수도 있는 상태**이다.

다시 말해, 프로그래밍에서 가장 골치 아픈 null을 안전하게 처리하기 위한 보호장비이다. 즉, 개발자가 null을 잊지 않고 강제로 처리하게 만들어, 런타임에 발생하는 NullPointerException(NPE) 오류를 컴파일 단계에서 미리 예방해준다.

<br/>

### 6️⃣ 스레드 안전성 무시

> **병렬처리를 수행하면서 공유 변수에 접근하는 실수이다. 여러 스레드가 동시에 하나의 리스트에 접근해 데이터를 쓰려고 할 때 충돌이 발생한다. 이를 경쟁상태라고 한다.**
> 

**🚨 왜 문제가 될까?**

여러 스레드가 동시에 같은 메모리 공간에 값을 쓰려고 할 때 서로의 값을 덮어쓰기 하거나 데이터의 개수가 일치하지 않는 등의 **데이터 무결성이 훼손**된다.

**💡 해결책**

전역 변수와 같은 외부 상태를 변경하려 하지 말고, **use thread-safe collections을 사용**하거나, **지역 변수를 사용**해야 한다.

- **Race Condition이 뭘까?**

두 개 이상의 스레드가 공유 변수에 동시에 접근해서 값을 바꾸려 할 때, 누가 먼저냐에 따라 결과가 엉망진창이 되는 현상을 말한다.

<br/>

### 7️⃣ 중간 연산과 최종 연산의 혼동

> **중간 연산은 Stream 객체를 반환한다. 이를 List나 Set 같은 실제 결과값으로 착각하고 변수에 할당하려는 실수**
> 

**🚨 왜 문제가 될까?**

호환되지 않은 타입 컴파일 에러가 발생한다.

**💡 해결책**


**변수에 할당하기 전에 반환 타입을 확인**해야 한다. Stream객체라면 결과를 얻으려면 반드시 끝에 collect(), count() 같은 최종 연산을 붙여야 한다.

<br />
<br/>


## 💻 실제 구현 💻

→ 실행 속도에 영향을 주는 **3,4번 실수를 구현** 해 볼 것이다.


### 📌 1. 큰 데이터와 작은 데이터에서 순차처리와 병렬처리의 실행 속도 비교(StreamLab.java)

- **가정한 문제 상황**
    - 소규모 데이터를 처리하는 경우에는 순차 스트림이 성능 면에서 효율적이다.
- **문제 상황이 발생하는 이유**
    - 스레드를 생성하고 할당하는 비용이 더 많이 들기 때문이다.
- **검증 방법**
    - 리스트의 각 숫자를 제곱하여 다시 리스트에 넣는 연산을 sequential / parallel 하게 수행하여 실행시간 오버헤드를 측정한다.
        - `System.currentTimeMillis();` 를 통해 현재 시각을 ms 단위로 기록한다.
    - 데이터 개수 1,000개 (소규모 데이터)
    - 데이터 개수 10,000,000개 (대규모 데이터)
    
<br/>

### 📝 구현 코드

```java
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
        System.out.println("데이터 준비 완료.\\n");

        // ----------------------------------------------------

        System.out.println("=== 1. 작은 데이터셋 (1,000개) ===");
        runTest("Small - Sequential", smallData, false);
        runTest("Small - Parallel  ", smallData, true);

        System.out.println("\\n----------------------------------------------------\\n");

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

<br/>

### ✅ 결과 분석 (Result)

1. **소규모 데이터**
- 기대한 결과 : sequential > parallel
1. **대규모 데이터**
- 기대한 결과 : sequential < parallel
    - '소규모 데이터에서는 병렬 스트림 사용이 오히려 비효율적일 것이다' 가정

**🔑 실행 결과**

| 데이터 크기 | 처리 방식 (Method) | 소요 시간 (측정마다 다르게 나옴) |
| --- | --- | --- |
| **소규모 데이터** | **Stream (Sequential)** | 1ms |
|  | **Stream (Parallel)** | 12ms |
| **대규모 데이터** | **Stream (Sequential)** | 445ms |
|  | **Stream (Parallel)** | 336ms |

 **➡️ 큰 데이터를 처리할 때에는 병렬처리 방식이 더 빠르지만 작은 데이터를 처리할 때에는 순차처리 방식이 더 빠르다.**

<br>

**🤯 작은 데이터에서 병렬처리가 느린 이유**

병렬처리를 하기 위해서는 새로운 스레드를 할당하고, CPU가 여러 스레드를 왔다 갔다 하는 context switching 비용이 발생한다. 

데이터가 작은 경우 데이터를 순차적으로 처리하는 비용보다 context switch하는 비용이 더 크기 때문에 느리게 나온다.

<br/>



### 📌 2. filter()나 map() 같은 중간 연산자들을 많이 사용했을 때의 성능 비교 (StreamLab2.java)

- **가정한 문제 상황**
    - 중간 연산을 많이 사용하면 실행 시간이 증가할 것이다.
- **문제 상황이 발생하는 이유**
    - 체이닝된 메서드들을 호출하는 상황은 호출 시간 등을 증가시켜 오버헤드를 발생시키기 때문이다.
- **검증 방법**
    - 테스트용 ArrayList 데이터를 생성하고 값을 초기화 한다.
    - 연산이 분리된 경우 / 합쳐진 경우를 실행한다.
        - 각각의 경우는 메서드로 구현한다.

<br/>

### 📝 구현 코드

```java
package streamlab;

import java.util.Arrays;
import java.util.List;

// 4번 예시
public class StreamLab2 {

	public static void main(String[] args) {
		List<String> names = Arrays.asList("Alice", "Bob","Charlie","David","Eve");

		names.stream().filter(n -> true).map(String::toUpperCase).toList();
		System.out.println("--- JVM 준비 완료 ---");
		
		long startTime1 = System.currentTimeMillis();
		List<String> result = names.stream()
				.filter(name -> name.startsWith("A"))
				.filter(name -> name.length() > 3)
				.map(String::toUpperCase)
				.map(name -> name + "is a name")
				.toList();
		
		System.out.println(result);
		long endTime1 = System.currentTimeMillis();
		System.out.println(names + " : " + (endTime1 - startTime1) + " ms");
		
		long startTime2 = System.currentTimeMillis();
		
		List<String> result2 = names.stream()
				.filter(name -> name.startsWith("A") && name.length() > 3)
				.map(name -> name.toUpperCase() + "is a name")
				.toList();
		System.out.println(result2);
		
		long endTime2 = System.currentTimeMillis();
		System.out.println(names + " : " + (endTime2 - startTime2) + " ms");
		
	}

}
```

<br/>


### ✅ 결과 분석 (Result)

**🔑 실행 결과**

| 처리 방식 (Method) | 소요 시간 (측정마다 다르게 나옴) |
| --- | --- |
| **중간 연산자가 많을 때** | 5ms |
| **중간 연산자가 적을 때** | 2ms |

**➡️ 중간 연산자가 적을 때 더 빠르다.**

<br/>

**🤯 중간 연산자가 많을 때 더 느린 이유** 

1. 연산자를 사용할 때마다 새로운 스트림 객체가 힙 메모리에 계속 만들어진다. 객체를 생성하며 드는 비용으로 인해 속도가 느려진다.
2. 연산자가 나뉘어 있으면 데이터가 지나갈 때마다 함수를 호출해야한다. 즉, 함수 호출 비용이 든다.

<br />

### 추가로 생각해 본 내용 💬

- **성능상 효율적이지 않은 이유(오버헤드를 발생 시키는 원인)로 메모리 사용량도 있을 것이라 생각한다.**
    - 메서드 코드 자체는 Method Area에 적재된다.
    - 메서드가 필요로 하는 인자나 반환값이 임시 Stream 객체인 경우 Heap Area에 객체를 생성한다.
    - 메서드를 실행하면서 생기는 변수 등은 Stack Area에 생성된다.
