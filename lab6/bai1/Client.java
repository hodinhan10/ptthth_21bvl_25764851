package lab6.bai1;

import java.rmi.Naming;

public class Client {
    public static void main(String[] args) {
        try {
            var service = (RemoteService) Naming.lookup("rmi://localhost/:1101/HelloService");
            System.out.println(service.sayHello());
        } catch (Exception e) {
            System.out.println("Loi: " + e.getMessage());
        }
    }
}
