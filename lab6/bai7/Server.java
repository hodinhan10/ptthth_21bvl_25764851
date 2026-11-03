package lab6.bai7;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1107);
            var service = new RemoteServiceImpl();
            Naming.rebind("rmi://localhost:1107/RemoteService", service);

            System.out.println("Server chay cong 1107");
        } catch (Exception e) {
            System.out.println("Loi: " + e.getMessage());
        }
    }
}
