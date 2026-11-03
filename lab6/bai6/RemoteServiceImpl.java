package lab6.bai6;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteServiceImpl extends UnicastRemoteObject implements RemoteService {

    public RemoteServiceImpl() throws RemoteException {
        super();
    }

    private double balance = 1_000;

    @Override
    public double getBalance() throws RemoteException {
        return balance;
    }

    @Override
    public void deposit(double amount) throws RemoteException {
        if (!Double.isFinite(amount)) throw new RemoteException("Số tiền gửi phải hữu hạn");
        if (amount <= 0) throw new RemoteException("Số tiền gửi phải lớn hơn 0");
        balance = balance + amount;
    }

    @Override
    public boolean withdraw(double amount) throws RemoteException {
        if (amount >= 0 && amount <= balance && Double.isFinite(amount)) {
            balance = balance - amount;
            return true;
        }
        return false;
    }

}
