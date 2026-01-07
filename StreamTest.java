package streamapi_mission;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StreamTest {

	private static final int DATA_SIZE = 100_000_000;
//	private static final int DATA_SIZE = 10;
//	private static final int DATA_SIZE = 500000;
//	private static final int DATA_SIZE = 550000;
	
	
	public static void main(String[] args) {
		 
        
        // 테스트용 데이터
        List<Integer> testDataList = new ArrayList<>(DATA_SIZE);
        
        int[] primitiveArray = new int[DATA_SIZE];
        
        Random random = new Random();

        for (int i = 0; i < DATA_SIZE; i++) {
        	
            int price = random.nextInt(1000); 
            testDataList.add(price);
            primitiveArray[i] = price;
        }

       
        testParallelOverhead(testDataList);

	}


	private static void testParallelOverhead(List<Integer> testDataList) {
		

	    // Sequential
	    long start = System.currentTimeMillis();
	    
	    long sumSeq = testDataList.stream()
	            .mapToLong(i -> i)
	            .sum();
	    
	    long end = System.currentTimeMillis();
	    
	    System.out.println("sequential: " + (end - start) + "ms");
	    
	    
	    
	    
	    // Parallel
	    start = System.currentTimeMillis();
	    
	    long sumPar = testDataList.parallelStream()
	            .mapToLong(i -> i)
	            .sum();
	    end = System.currentTimeMillis();
	    
	    System.out.println("parrel: " + (end - start) + "ms");
	    
	}

}
