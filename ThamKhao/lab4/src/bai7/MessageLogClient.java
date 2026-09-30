package ThamKhao.lab4.src.bai7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MessageLogClient {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Cách dùng: java bai7.MessageLogClient <clientId> [host] [port]");
            return;
        }
        String clientId = args[0];
        String host = args.length > 1 ? args[1] : "localhost";
        int port;
        try {
            port = args.length > 2 ? Integer.parseInt(args[2]) : 5012;
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
            out.println("HELLO " + clientId);
            String response = in.readLine();
            System.out.println("Server: " + response);
            if (response == null || response.startsWith("ERR")) return;

            System.out.println("Nhập nội dung cần lưu hoặc QUIT:");
            String message;
            while ((message = console.readLine()) != null) {
                out.println(message);
                response = in.readLine();
                if (response == null) break;
                System.out.println("Server: " + response);
                if (message.equalsIgnoreCase("QUIT")) break;
            }
        } catch (IOException e) {
            System.err.println("Lỗi kết nối: " + e.getMessage());
        }
    }
}
