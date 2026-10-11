package bai7;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageLogServer {
    private static final int DEFAULT_PORT = 5012;
    private static final Path LOG_DIRECTORY = Path.of("data", "logs");
    private static final ConcurrentMap<String, Object> FILE_LOCKS = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int port;
        try {
            port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        ExecutorService pool = Executors.newFixedThreadPool(20);
        try {
            Files.createDirectories(LOG_DIRECTORY);
            try (ServerSocket server = new ServerSocket(port)) {
                System.out.println("Message log server tại port " + port);
                while (true) {
                    Socket socket = server.accept();
                    pool.submit(() -> handle(socket));
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi server: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void handle(Socket socket) {
        String remote = String.valueOf(socket.getRemoteSocketAddress());
        try (socket;
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            String hello = in.readLine();
            if (hello == null || !hello.matches("HELLO [A-Za-z0-9_-]+")) {
                out.println("ERR INVALID_HELLO");
                return;
            }

            String clientId = hello.substring(6);
            out.println("OK HELLO " + clientId);
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                }
                append(clientId, remote, message);
                out.println("OK SAVED");
            }
        } catch (IOException e) {
            System.err.println("Client " + remote + " lỗi: " + e.getMessage());
        }
    }

    private static void append(String clientId, String remote, String message)
            throws IOException {
        Path logFile = LOG_DIRECTORY.resolve(clientId + ".txt");
        Object lock = FILE_LOCKS.computeIfAbsent(clientId, key -> new Object());
        synchronized (lock) {
            try (BufferedWriter writer = Files.newBufferedWriter(logFile,
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND)) {
                writer.write(LocalDateTime.now() + " | " + remote + " | " + message);
                writer.newLine();
            }
        }
    }
}
