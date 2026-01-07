package step02;

import java.util.List;
import java.util.stream.IntStream;

public class StreamPerformanceTest {

	public static void main(String[] args) {
        List<String> names = IntStream.range(0, 5_000_000)
                .mapToObj(i -> i % 2 == 0 ? "Alice" : "Bob")
                .toList();

        // Case 1: Overusing intermediate operations
        long start = System.currentTimeMillis();
        List<String> result1 = names.stream()
                .filter(name -> name.startsWith("A"))
                .filter(name -> name.length() > 3)
                .map(name -> name + " is a name")
                .toList();
        long end = System.currentTimeMillis();
        System.out.println("Overused operations time: " + (end - start) + " ms");

        // Case 2: Optimized stream pipeline
        start = System.currentTimeMillis();
        List<String> result2 = names.stream()
                .filter(name -> name.startsWith("A") && name.length() > 3)
                .map(name -> name + " is a name")
                .toList();
        end = System.currentTimeMillis();
        System.out.println("Optimized pipeline time: " + (end - start) + " ms");
    }

}
