package bai6;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpBenchmarkServer {
    private static final int DEFAULT_PORT = 5011;

    public static void main(String[] args) {
        int port;
        try {
            port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        byte[] buffer = new byte[4096];
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("UDP benchmark server tại port " + port);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                socket.send(new DatagramPacket(packet.getData(), packet.getLength(),
                        packet.getAddress(), packet.getPort()));
            }
        } catch (IOException e) {
            System.err.println("Lỗi UDP server: " + e.getMessage());
        }
    }
}
