package lab6.bai3;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteServiceImpl extends UnicastRemoteObject implements RemoteService {
    public RemoteServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public boolean isPrime(int n) throws RemoteException {
        if (n < 2) return false;
        for (int i = 2; i < n / i; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
