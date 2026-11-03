package lab6.bai3;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class server {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1103);
            var service = new RemoteServiceImpl();
            Naming.rebind("rmi://localhost:1103/RemoteService", service);

            System.out.println("Server chay cong 1103");
        } catch (Exception e) {
            System.out.println("Loi: " + e.getMessage());
        }
    }
}
