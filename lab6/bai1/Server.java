package lab6.bai1;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1101);
            var service = new RemoteServiceImpl();
            Naming.rebind("rmi://localhost:1101/HelloService", service);
            System.out.println("ket noi cong 1101");
        } catch (Exception e) {
            System.out.println("Ko mo duoc:" + e.getMessage());
        }
    }
}
