package lab6.bai6;

import java.rmi.Naming;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        var sc = new Scanner(System.in);
        try {
            var service = (RemoteService) Naming.lookup("rmi://localhost:1106/RemoteService");
            while (true) {
                System.out.println("\n1. Xem số dư | 2. Gửi tiền | 3. Rút tiền | 0. Thoát");
                var choice = sc.nextInt();
                if (choice == 0) break;

                switch (choice) {
                    case 1:
                        System.out.println("Xem số dư: " + service.getBalance());
                        break;
                    case 2:
                        System.out.print("Số tiền gửi: ");
                        var deposit = sc.nextDouble();
                        service.deposit(deposit);
                        break;
                    case 3:
                        System.out.print("Số tiền rút: ");
                        var amount = sc.nextDouble();
                        var isStatus = service.withdraw(amount);

                        if (isStatus) System.out.println("Rút thành công");
                        else System.out.println("Rút thất bại");
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Loi: " + e.getMessage());
        }
        sc.close();
    }
}
