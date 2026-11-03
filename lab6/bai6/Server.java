package lab6.bai6;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1106);
            var service = new RemoteServiceImpl();
            Naming.rebind("rmi://localhost:1106/RemoteService", service);

            System.out.println("Server chay cong 1106");
        } catch (Exception e) {
            System.out.println("Loi: " + e.getMessage());
        }
    }
}
