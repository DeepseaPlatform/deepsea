package simple;

public class XYChoice {

	public static void main(String[] args) {
		choose(3, 4);
	}

	private static int choose(int x, int y) {
		if (x < y) {
			if (y > 5) {
				return x - 1;
			} else {
				return y + 1;
			}
		} else if (x + y < 10) {
			return 0;
		} else {
			return x - y;
		}
	}

}
