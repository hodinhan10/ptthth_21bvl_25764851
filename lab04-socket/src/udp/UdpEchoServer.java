package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class UdpEchoServer {
    private static final int PORT = 5001;

    public static void main(String[] args) {
        byte[] buffer = new byte[4096];
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("UDP server listening on port " + PORT);
            while (true) {
                DatagramPacket req = new DatagramPacket(buffer, buffer.length);
                socket.receive(req);

                String message = new String(req.getData(), req.getOffset(), req.getLength(), StandardCharsets.UTF_8);
                String text = message.toUpperCase(Locale.ROOT);
                byte[] resData = text.getBytes(StandardCharsets.UTF_8);
                DatagramPacket res = new DatagramPacket(resData, resData.length, req.getAddress(), req.getPort());
                socket.send(res);
            }
        } catch (IOException e) {
            System.err.println("UDP server error: " + e.getMessage());
        }
    }
}
