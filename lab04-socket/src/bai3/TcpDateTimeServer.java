package bai3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpDateTimeServer {
    private static final int PORT = 5003;

    public static void main(String[] args) {
        int port;
        try {
            port = args.length > 0 ? Integer.parseInt(args[0]) : PORT;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("TCP date-time server đang chạy tại port " + port);
            while (true) {
                try (Socket socket = server.accept()) {
                    serve(socket);
                } catch (IOException e) {
                    System.err.println("Lỗi phiên client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Không mở được TCP server: " + e.getMessage());
        }
    }

    private static void serve(Socket socket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            String request;
            while ((request = in.readLine()) != null) {
                out.println(DateTimeService.process(request));
                if (request.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
        }
    }
}
