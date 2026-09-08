package lab1_Basic;

import java.util.Scanner;

public class bai2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in);
		System.out.println("What's your name?");
		String str = scanner.nextLine();
		System.out.println("Hi. I am " + str);
		scanner.close();
	}

}
