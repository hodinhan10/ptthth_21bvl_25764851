package lab6.bai8;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1108);
            var service = new RemoteServiceImpl();
            Naming.rebind("rmi://localhost:1108/RemoteService", service);

            System.out.println("Server chay cong 1108");
        } catch (Exception e) {
            System.out.println("Loi: " + e.getMessage());
        }
    }
}
