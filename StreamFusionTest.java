package streamapi_mission;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StreamFusionTest {

    private static final int DATA_SIZE = 10_000_000; 

    public static void main(String[] args) {
    	
        System.out.println("데이터" + DATA_SIZE + "개)");
        
        List<Integer> list = new ArrayList<>(DATA_SIZE);
        
        Random r = new Random();
        
        for (int i = 0; i < DATA_SIZE; i++) {
            list.add(r.nextInt(100)); 
        }

        testSeparated(list); 
        testMerged(list); 
  
        // 분리된 연산 (비효율적인 경우)
        long start = System.nanoTime();
        
        testSeparated(list);
        
        long end = System.nanoTime();
        long badTime = end - start;

        System.gc(); 

        // 합쳐진 연산 (효율적인 경우)
        start = System.nanoTime();
        
        testMerged(list);
        
        end = System.nanoTime();
        long goodTime = end - start;

 
        System.out.printf("분리된 연산 (비효율적인 경우):  %,12d ns (%.2f ms)\n", badTime, badTime / 1_000_000.0);
        System.out.printf("합쳐진 연산 (효율적인 경우): %,12d ns (%.2f ms)\n", goodTime, goodTime / 1_000_000.0);
        
        double diff = (double) badTime / goodTime;
        System.out.printf("결과: 로직을 합친 것이 약 %.2f배 더 빠름\n", diff);
    }

    // 비효율: 연산을 쪼갬 -> 함수 호출 비용 증가
    private static long testSeparated(List<Integer> list) {
        return list.stream()
                .filter(i -> i % 2 == 0)      // 짝수인가?
                .filter(i -> i > 50)          // 50보다 큰가?
                .map(i -> i * 2)              // 2배 곱하기
                .map(i -> i + 10)             // 10 더하기
                .count();                     // 터미널 연산
    }

    // 효율: 연산을 하나로 합침 -> 함수 호출 최소화
    private static long testMerged(List<Integer> list) {
        return list.stream()
                .filter(i -> (i % 2 == 0) && (i > 50)) // 논리 연산자로 합침
                .map(i -> (i * 2) + 10)                // 수식으로 합침
                .count();
    }
}