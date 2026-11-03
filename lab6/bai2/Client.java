package lab6.bai2;

import java.rmi.Naming;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            var service = (RemoteService) Naming.lookup("rmi://localhost:1102/AddService");
            var scanner = new Scanner(System.in);
            System.out.println("Nhap a: ");
            var a = scanner.nextInt();

            System.out.println("Nhap b: ");
            var b = scanner.nextInt();

            var tong = service.add(a, b);
            System.out.println("Tong: " + tong);
            scanner.close();
        } catch (Exception e) {
            System.out.println("loi: " + e.getMessage());
        }
    }
}
