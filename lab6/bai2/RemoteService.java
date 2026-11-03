package lab6.bai2;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteService extends Remote {
    int add(int a, int b) throws RemoteException;
}