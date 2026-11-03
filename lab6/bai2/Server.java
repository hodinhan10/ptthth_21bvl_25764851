package lab6.bai2;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1102);
            RemoteService service = new RemoteServiceImpl();
            Naming.rebind("rmi://localhost:1102/AddService", service);

            System.out.println("Server chay cong 1102");
        } catch (Exception e) {
            System.out.println("Khong mo duoc server: " + e.getMessage());
        }
    }
}
