package ThamKhao.lab4.src.bai5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private static final int DEFAULT_PORT = 5006;
    private static final int MAX_CLIENTS = 20;
    private static final ConcurrentMap<String, PrintWriter> CLIENTS = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int port;
        try {
            port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTS);
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Chat server đang chạy tại port " + port);
            while (true) {
                Socket socket = server.accept();
                pool.submit(() -> handle(socket));
            }
        } catch (IOException e) {
            System.err.println("Lỗi chat server: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void handle(Socket socket) {
        String nickname = null;
        try (socket;
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            out.println("INFO Nhập nickname");
            String requestedName = in.readLine();
            if (requestedName == null || !requestedName.matches("[A-Za-z0-9_-]{1,20}")) {
                out.println("ERR INVALID_NICKNAME");
                return;
            }
            if (CLIENTS.putIfAbsent(requestedName, out) != null) {
                out.println("ERR NICKNAME_EXISTS");
                return;
            }

            nickname = requestedName;
            out.println("OK HELLO " + nickname);
            broadcast(nickname, "INFO " + nickname + " đã tham gia");

            String request;
            while ((request = in.readLine()) != null) {
                if (request.equalsIgnoreCase("USERS")) {
                    ArrayList<String> users = new ArrayList<>(CLIENTS.keySet());
                    users.sort(String::compareToIgnoreCase);
                    out.println("OK USERS " + String.join(",", users));
                } else if (request.regionMatches(true, 0, "MSG ", 0, 4)
                        && !request.substring(4).isBlank()) {
                    broadcast(nickname, "MSG " + nickname + ": " + request.substring(4));
                    out.println("OK SENT");
                } else if (request.equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                } else {
                    out.println("ERR UNKNOWN_COMMAND");
                }
            }
        } catch (IOException e) {
            System.err.println("Client ngắt kết nối: " + e.getMessage());
        } finally {
            if (nickname != null && CLIENTS.remove(nickname) != null) {
                broadcast(nickname, "INFO " + nickname + " đã rời phòng");
            }
        }
    }

    private static void broadcast(String sender, String message) {
        CLIENTS.forEach((name, writer) -> {
            if (!name.equals(sender)) writer.println(message);
        });
    }
}
