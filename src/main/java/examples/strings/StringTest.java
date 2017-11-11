package examples.strings;

import examples.strings.StringMethods;

public class StringTest {
	
	public static void test(int[] in) {
		int[] match = {1,2,3,4};
		if (StringMethods.startsWith(in,match)) {
			System.out.println("Match "); assert false;
		} else {
			System.out.println("No Match ");
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] in = {1,2,2,4,5};
		test(in);
	}

}
