package ThamKhao.lab4.src.bai5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port;
        try {
            port = args.length > 1 ? Integer.parseInt(args[1]) : 5006;
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
            System.out.println(in.readLine());
            String nickname = console.readLine();
            if (nickname == null) return;
            out.println(nickname);
            String hello = in.readLine();
            System.out.println("Server: " + hello);
            if (hello == null || hello.startsWith("ERR")) return;

            Thread receiver = new Thread(() -> receive(in), "chat-receiver");
            receiver.setDaemon(true);
            receiver.start();

            System.out.println("Dùng USERS, MSG <nội dung> hoặc QUIT");
            String request;
            while ((request = console.readLine()) != null) {
                out.println(request);
                if (request.equalsIgnoreCase("QUIT")) break;
            }
            try {
                receiver.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } catch (IOException e) {
            System.err.println("Lỗi kết nối: " + e.getMessage());
        }
    }

    private static void receive(BufferedReader in) {
        try {
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println("Server: " + response);
            }
        } catch (IOException e) {
            System.err.println("Mất kết nối đến server");
        }
    }
}
