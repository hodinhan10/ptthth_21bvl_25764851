package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpEchoServer {
    private static final int PORT = 5001;

    public static void main(String[] args) {
        byte[] buffer = new byte[4096];
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("UDP benchmark server listening on port " + PORT);
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                DatagramPacket response = new DatagramPacket(request.getData(),
                        request.getLength(), request.getAddress(), request.getPort());
                socket.send(response);
            }
        } catch (IOException e) {
            System.err.println("UDP server error: " + e.getMessage());
        }
    }
}
