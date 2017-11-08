package examples.simple;

public class StringTest {

	public static boolean startsWith(int[] one, int[] two) {
		int idx = 0;
		boolean theSame = true;
		while (idx < two.length) {
			if (one[idx] != two[idx]) {
				theSame = false;
				break;
			}
			idx++;
		}
		return theSame;
	}
	
	public static void test(int[] in) {
		int[] match = {1,2,3,4};
		if (startsWith(in,match)) {
			System.out.println("Match ");
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
