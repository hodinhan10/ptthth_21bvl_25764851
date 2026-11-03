package lab6.bai4;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class RemoteServiceImpl extends UnicastRemoteObject implements RemoteService {
    public RemoteServiceImpl() throws RemoteException {
        super();
    }

    private final HashMap<String, String> contacts = new HashMap<>();

    @Override
    public void addContact(String name, String phone) throws RemoteException {
        if (name == null || name.isEmpty() || phone == null || phone.isEmpty()) {
            throw new RemoteException("Ten va SDT khong de trong");
        }
        contacts.put(name, phone);
    }

    @Override
    public String findContact(String name) throws RemoteException {
        return contacts.get(name);
    }

    @Override
    public boolean deleteContact(String name) throws RemoteException {
        return contacts.remove(name) != null;
    }
}
