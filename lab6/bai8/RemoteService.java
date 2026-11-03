package lab6.bai8;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteService extends Remote {
    boolean bookTicket(String flightNumber, int seats) throws RemoteException;

    int availableSeats(String flightNumber) throws RemoteException;

    boolean cancelBooking(String flightNumber, int seats) throws RemoteException;
}
