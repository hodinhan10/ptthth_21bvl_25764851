package lab1_Basic;

import java.util.Scanner;

public class bai5 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		System.out.println(">>kiem tra chan le <<");
		System.out.println("Vui long nhap so can kiem tra: ");
        int so = sc.nextInt();
        if(so % 2 == 0)System.out.println("So " + so + " la so chan.");
        else System.out.println("So " + so + " la so le." );
        sc.close();
	}

}
