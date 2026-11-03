package lab6.bai5;

import java.rmi.Naming;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        var sc = new Scanner(System.in);
        try {
            var service = (RemoteService) Naming.lookup("rmi://localhost:1105/RemoteService");
            System.out.println("Nhập tin nhắn; /exit để thoát.");
            while (true) {
                var message = sc.nextLine();
                if (message.equals("/exit")) break;
                service.sendMessage(message);
                System.out.println("Server: Đã nhận tin nhắn.");
            }
        } catch (Exception e) {
            System.out.println("Loi: " + e.getMessage());
        }
        sc.close();
    }
}
