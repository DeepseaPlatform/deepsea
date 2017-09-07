package examples.spf;

public class ArrayTestSimple {

	public static boolean test(int x, int y) {
		int[] arr = new int[2];
		arr[0] = x;
		arr[1] = y;
		if (arr[0] > arr[1])
			return true;
		else
			return false;
	}
	
	public static boolean test2(int[] arr) {
		if (arr[0] > arr[1])
			return true;
		else
			return false;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] a = new int[2];
		a[0] = 1;
		a[1] = 2;
		//test2(a[0],a[1]);
		test(a[0],a[1]);
	}

}
