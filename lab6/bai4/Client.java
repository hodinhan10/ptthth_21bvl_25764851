package lab6.bai4;

import java.rmi.Naming;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        var sc = new Scanner(System.in);
        try {
            var service = (RemoteService) Naming.lookup("rmi://localhost:1104/RemoteService");

            while (true) {
                System.out.println("1: Add/Update | 2: find | 3: delete");
                var choice = sc.nextInt();
                sc.nextLine();
                System.out.print("Name: ");
                var name = sc.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("SDT: ");
                        var phone = sc.nextLine();
                        service.addContact(name, phone);
                        break;
                    case 2:
                        var findPhone = service.findContact(name);
                        if (findPhone != null) System.out.println("Tim thay SDT: " + findPhone);
                        else System.out.println("Ko tim thay");
                        break;
                    case 3:
                        var del = service.deleteContact(name);
                        if (del) System.out.println("Xoa");
                        else System.out.println("Ko xoa");
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
