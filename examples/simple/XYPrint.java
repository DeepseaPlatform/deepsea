package simple;

public class XYPrint {

	public static void main(String[] args) {
		print(3, 4);
	}

	private static void print(int x, int y) {
		if (x < y) {
			System.out.println(x + y);
		} else {
			System.out.println(x - y);
		}
	}
}
