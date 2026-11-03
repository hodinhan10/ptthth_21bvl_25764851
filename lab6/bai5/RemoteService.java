package lab6.bai5;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteService extends Remote {
    void sendMessage(String message) throws RemoteException;
}
