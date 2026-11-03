package lab6.bai8;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class RemoteServiceImpl extends UnicastRemoteObject implements RemoteService {
    private final HashMap<String, Integer> capacity = new HashMap<>();
    private final HashMap<String, Integer> remaining = new HashMap<>();

    public RemoteServiceImpl() throws RemoteException {
        capacity.put("VN101", 50);
        capacity.put("VN202", 80);
        capacity.put("VN303", 100);
        remaining.putAll(capacity);
    }

    @Override
    public int availableSeats(String flightNumber) throws RemoteException {
        return remaining.getOrDefault(flightNumber, -1);
    }

    @Override
    public boolean bookTicket(String flightNumber, int seats) throws RemoteException {
        Integer available = remaining.get(flightNumber);
        if (available == null || seats <= 0 || seats > available) return false;
        remaining.put(flightNumber, available - seats);
        return true;
    }

    @Override
    public boolean cancelBooking(String flightNumber, int seats) throws RemoteException {
        Integer available = remaining.get(flightNumber);
        if (available == null || seats <= 0) return false;

        int booked = capacity.get(flightNumber) - available;
        if (seats > booked) return false;

        remaining.put(flightNumber, available + seats);
        return true;
    }
}
