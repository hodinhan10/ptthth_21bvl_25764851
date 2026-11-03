package lab6.bai3;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteService extends Remote {
    boolean isPrime(int n) throws RemoteException;
}
