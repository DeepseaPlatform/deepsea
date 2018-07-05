package examples.kmp;

public class KMP {

	public static int search(int[] haystack, int[] needle) {
		return KMP_Anuvrat.search(haystack, needle);
		// return KMP_Lang.search(haystack, needle);
		// return KMP_Sanfoundry.search(haystack, needle);
		// return KMP_Scgilardi.search(haystack, needle);
		// return KMP_Schoenig.search(haystack, needle);
		// return KMP_Vboutchkova.search(haystack, needle);
	}

	public static void main(String[] args) {
		int[] haystack = { 1, 2, 3, 4 };
		int[] needle = { 1, 2 };
		System.out.println(search(haystack, needle));
	}

}
