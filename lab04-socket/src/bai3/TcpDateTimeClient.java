package bai3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpDateTimeClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port;
        try {
            port = args.length > 1 ? Integer.parseInt(args[1]) : 5003;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        try (Socket socket = new Socket(host, port);
                BufferedReader console = new BufferedReader(
                        new InputStreamReader(System.in, StandardCharsets.UTF_8));
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            System.out.println("Nhập DATE, TIME, DATETIME hoặc QUIT:");
            String request;
            while ((request = console.readLine()) != null) {
                out.println(request);
                String response = in.readLine();
                if (response == null) {
                    System.out.println("Server đã đóng kết nối");
                    break;
                }
                System.out.println("Server: " + response);
                if (request.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Mất kết nối đến TCP server: " + e.getMessage());
        }
    }
}
