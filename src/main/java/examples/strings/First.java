package examples.strings;

public class First {

	public static int test(String str) {
		if (str.startsWith("blah")) {
			return 0;
		}
		else {
			return 1;
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		test("hello");
	}

}
