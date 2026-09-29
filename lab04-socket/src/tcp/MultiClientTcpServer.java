package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiClientTcpServer {
    private static final int PORT = 5000;
    private static final int MAX_CLIENTS = 20;
    private static final Map<String, ClientHandler> CLIENTS = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTS);
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Chat server listening on port " + PORT);
            while (true) {
                pool.submit(new ClientHandler(server.accept()));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : CLIENTS.values()) {
            if (client != sender) client.send(message);
        }
    }

    private static String onlineUsers() {
        List<String> names = new ArrayList<>();
        for (ClientHandler client : CLIENTS.values()) names.add(client.nickname);
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        return String.join(", ", names);
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private String nickname;
        private String nicknameKey;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String remote = String.valueOf(socket.getRemoteSocketAddress());
            System.out.println("Connected: " + remote);
            try (socket;
                 BufferedReader in = new BufferedReader(new InputStreamReader(
                         socket.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                         socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
                out = writer;
                if (!registerNickname(in)) return;

                broadcast("SYSTEM " + nickname + " joined the chat", this);
                String request;
                while ((request = in.readLine()) != null && process(request)) {
                    // Mỗi yêu cầu được xử lý theo giao thức dòng UTF-8.
                }
            } catch (IOException e) {
                System.err.println("Client " + remote + " failed: " + e.getMessage());
            } finally {
                removeClient();
                System.out.println("Disconnected: " + remote);
            }
        }

        private boolean registerNickname(BufferedReader in) throws IOException {
            String request = in.readLine();
            if (request == null || !request.startsWith("NICK ")) {
                send("ERR EXPECTED_NICK");
                return false;
            }

            String requestedName = request.substring(5).trim();
            if (!requestedName.matches("[A-Za-z0-9_-]{1,20}")) {
                send("ERR INVALID_NICKNAME");
                return false;
            }

            String key = requestedName.toLowerCase(Locale.ROOT);
            nickname = requestedName;
            nicknameKey = key;
            if (CLIENTS.putIfAbsent(key, this) != null) {
                nickname = null;
                nicknameKey = null;
                send("ERR NICKNAME_TAKEN");
                return false;
            }

            send("OK WELCOME " + nickname);
            return true;
        }

        private boolean process(String request) {
            if (request.equalsIgnoreCase("USERS")) {
                send("OK USERS " + onlineUsers());
                return true;
            }
            if (request.regionMatches(true, 0, "MSG ", 0, 4)) {
                String content = request.substring(4).trim();
                if (content.isEmpty()) {
                    send("ERR EMPTY_MESSAGE");
                } else {
                    broadcast("MSG " + nickname + ": " + content, this);
                    send("OK SENT");
                }
                return true;
            }
            if (request.equalsIgnoreCase("QUIT")) {
                send("OK BYE");
                return false;
            }
            send("ERR UNKNOWN_COMMAND");
            return true;
        }

        private synchronized void send(String message) {
            if (out != null) out.println(message);
        }

        private void removeClient() {
            if (nicknameKey != null && CLIENTS.remove(nicknameKey, this)) {
                broadcast("SYSTEM " + nickname + " left the chat", this);
            }
        }
    }
}
