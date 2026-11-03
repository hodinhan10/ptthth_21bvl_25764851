package lab6.bai5;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteServiceImpl extends UnicastRemoteObject implements RemoteService {
    public RemoteServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void sendMessage(String message) throws RemoteException {
        System.out.println("Client: " + message);
        System.out.println("Server: Đã nhận tin.");
    }

}
