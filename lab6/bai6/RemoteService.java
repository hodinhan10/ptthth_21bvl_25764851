package lab6.bai6;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteService extends Remote {
    double getBalance() throws RemoteException; // lấy số dư

    void deposit(double amount) throws RemoteException; // tiền gửi

    boolean withdraw(double amount) throws RemoteException; // rút tiền
}