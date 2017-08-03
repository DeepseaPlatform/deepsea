package simple;


public class GreeterN {

	public static void main(String[] args) {
		String name = "world";
		int count = 5;
		if (args.length > 0) {
			name = args[0];
		}
		if (args.length > 1) {
			count = Integer.parseInt(args[1]);
		}
		greet(name, count);
	}

	private static void greet(String name, int count) {
		for (int i = 0; i < count; i++) {
			System.out.println("Hello, " + name + "!");
		}
	}
	
}
