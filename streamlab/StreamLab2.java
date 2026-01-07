package streamlab;

import java.util.Arrays;
import java.util.List;


// 4번 예시
public class StreamLab2 {

	public static void main(String[] args) {
		List<String> names = Arrays.asList("Alice", "Bob","Charlie","David","Eve");
		
		long startTime1 = System.currentTimeMillis();
		List<String> result = names.stream()
				.filter(name -> name.startsWith("A"))
				.filter(name -> name.length() > 3)
				.map(String::toUpperCase)
				.map(name -> name + "is a name")
				.toList();
		
		System.out.println(result);
		long endTime1 = System.currentTimeMillis();
		
		long startTime2 = System.currentTimeMillis();
		System.out.println(names + " : " + (endTime1 - startTime1) + " ms");
		
		List<String> result2 = names.stream()
				.filter(name -> name.startsWith("A") && name.length() > 3)
				.map(name -> name.toUpperCase() + "is a name")
				.toList();
		System.out.println(result2);
		
		long endTime2 = System.currentTimeMillis();
		System.out.println(names + " : " + (endTime2 - startTime2) + " ms");
				
	}

}
