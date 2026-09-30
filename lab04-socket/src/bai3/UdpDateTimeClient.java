package bai3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class UdpDateTimeClient {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        int port;
        try {
            port = args.length > 1 ? Integer.parseInt(args[1]) : 5004;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        InetAddress server = InetAddress.getByName(host);
        try (BufferedReader console = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
                DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(3000);
            System.out.println("Nhập DATE, TIME, DATETIME hoặc QUIT để thoát:");
            String command;
            while ((command = console.readLine()) != null) {
                if (command.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }

                byte[] requestData = command.getBytes(StandardCharsets.UTF_8);
                socket.send(new DatagramPacket(requestData, requestData.length,
                        server, port));

                byte[] buffer = new byte[1024];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(response);
                    String text = new String(response.getData(), response.getOffset(),
                            response.getLength(), StandardCharsets.UTF_8);
                    System.out.println("Server: " + text);
                } catch (SocketTimeoutException e) {
                    System.err.println("Hết 3 giây nhưng chưa nhận được phản hồi");
                }
            }
        }
    }
}
