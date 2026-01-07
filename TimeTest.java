package step02;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

public class TimeTest {

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
			result = data.parallelStream().map(n -> {
				return n * n;
			}).collect(Collectors.toList());
		} else {
			result = data.stream().map(n -> {
				return n * n;
			}).collect(Collectors.toList());
		}
		long endTime = System.currentTimeMillis();
		System.out.println(name + " : " + (endTime - startTime) + " ms");
	}

}
