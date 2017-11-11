package examples.strings;

public class First {

	public static int test(String str) {
		if (str.startsWith("bl")) {
			System.out.println("Match");
			return 1;
		}
		else {
			System.out.println("No match");
			return 0;
		}
	}
	
	public static void main(String[] args) {
		test("hel");
	}

}
