package bai3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class UdpDateTimeServer {
    private static final int PORT = 5004;

    public static void main(String[] args) {
        int port;
        try {
            port = args.length > 0 ? Integer.parseInt(args[0]) : PORT;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        byte[] buffer = new byte[1024];
        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("UDP date-time server đang chạy tại port " + port);
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                String command = new String(request.getData(), request.getOffset(),
                        request.getLength(), StandardCharsets.UTF_8);
                byte[] responseData = DateTimeService.process(command)
                        .getBytes(StandardCharsets.UTF_8);
                DatagramPacket response = new DatagramPacket(responseData,
                        responseData.length, request.getAddress(), request.getPort());
                socket.send(response);
            }
        } catch (IOException e) {
            System.err.println("Lỗi UDP server: " + e.getMessage());
        }
    }
}
