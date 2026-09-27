package tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpCommandClient {
    private static final int MESSAGE_COUNT = 1_000;
    private static final int MESSAGE_SIZE = 256;
    private static final int TEST_ROUNDS = 5;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port;
        try {
            port = args.length > 1 ? Integer.parseInt(args[1]) : 5000;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        printEnvironment(host, port);
        for (int round = 1; round <= TEST_ROUNDS; round++) {
            runRound(host, port, round);
        }
    }

    private static void runRound(String host, int port, int round) {
        int responses = 0;
        long start = System.nanoTime();

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(
                     socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(
                     socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            for (int i = 0; i < MESSAGE_COUNT; i++) {
                String message = createMessage(i);
                out.println(message);
                String response = in.readLine();
                if (message.equals(response)) responses++;
            }
        } catch (IOException e) {
            System.err.println("TCP round " + round + " lỗi: " + e.getMessage());
        }

        double elapsedMs = (System.nanoTime() - start) / 1_000_000.0;
        System.out.printf("TCP round %d: %.3f ms, responses: %d/%d%n",
                round, elapsedMs, responses, MESSAGE_COUNT);
    }

    private static String createMessage(int sequence) {
        String prefix = String.format("%06d|", sequence);
        return prefix + "X".repeat(MESSAGE_SIZE - prefix.length());
    }

    private static void printEnvironment(String host, int port) {
        System.out.println("=== TCP BENCHMARK ===");
        System.out.println("OS: " + System.getProperty("os.name") + " "
                + System.getProperty("os.version"));
        System.out.println("Java: " + System.getProperty("java.version"));
        System.out.println("Server: " + host + ":" + port);
        System.out.println("Payload: " + MESSAGE_SIZE + " bytes UTF-8 ASCII");
        System.out.println("Messages per round: " + MESSAGE_COUNT);
        System.out.println("Rounds: " + TEST_ROUNDS);
        System.out.println("Method: request-response tuần tự, đo bằng System.nanoTime()");
    }
}
