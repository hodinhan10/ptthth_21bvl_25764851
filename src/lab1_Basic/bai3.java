package lab1_Basic;

import java.util.Scanner;

public class bai3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		System.out.println("Vui long nhap so hang thu nhat: ");
		int soA = sc.nextInt();
		System.out.println("Vui long nhap so thu 2: ");
		int soB = sc.nextInt();
		int kg = soA + soB;
		System.out.println("Tinh tong: " + soA + " + " + soB + " = " + kg);
		sc.close();
	}

}
