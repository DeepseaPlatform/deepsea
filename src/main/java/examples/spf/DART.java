package examples.spf;

public class DART {

	public static void test(int x, int y) {
		if (x*x*x > 0){
			if(x>0 && y==10)
				abort();
		} else {
			if (x>0 && y==20)
				abort();
		}
	}
	
	public static void abort() {
		System.out.println("ABORT");
	}
	
	public static void main(String[] args) {
		test(2,9);
	}
	
}
