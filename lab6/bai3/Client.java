package lab6.bai3;

import java.rmi.Naming;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        var sc = new Scanner(System.in);
        try {
            var service = (RemoteService) Naming.lookup("rmi://localhost:1103/RemoteService");

            System.out.print("Nhap so: ");
            var n = sc.nextInt();

            if (service.isPrime(n)) System.out.println("So nguyen to: " + n);
            else System.out.println("ko phai so nguyen to: " + n);
        } catch (Exception e) {
            System.out.println("Loi: " + e.getMessage());
        }
        sc.close();
    }
}
