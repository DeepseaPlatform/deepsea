package examples.strings;

public class StringMethods {
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
}
