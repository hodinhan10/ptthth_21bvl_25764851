package lab6.bai1;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteService extends Remote {
    String sayHello() throws RemoteException;
}
