package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class UdpEchoClient {
    private static final int MESSAGE_COUNT = 1_000;
    private static final int MESSAGE_SIZE = 256;
    private static final int TEST_ROUNDS = 5;
    private static final int TIMEOUT_MS = 1_000;

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        int port;
        try {
            port = args.length > 1 ? Integer.parseInt(args[1]) : 5001;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        InetAddress server = InetAddress.getByName(host);
        printEnvironment(host, port);
        for (int round = 1; round <= TEST_ROUNDS; round++) {
            runRound(server, port, round);
        }
    }

    private static void runRound(InetAddress server, int port, int round) {
        int responses = 0;
        long start = System.nanoTime();

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(TIMEOUT_MS);
            byte[] receiveBuffer = new byte[4096];

            for (int i = 0; i < MESSAGE_COUNT; i++) {
                byte[] data = createMessage(i);
                socket.send(new DatagramPacket(data, data.length, server, port));

                DatagramPacket response = new DatagramPacket(
                        receiveBuffer, receiveBuffer.length);
                try {
                    socket.receive(response);
                    byte[] received = Arrays.copyOfRange(response.getData(),
                            response.getOffset(), response.getOffset() + response.getLength());
                    if (Arrays.equals(data, received)) responses++;
                } catch (SocketTimeoutException e) {
                    // Gói tin được tính là mất; tiếp tục gửi thông điệp kế tiếp.
                }
            }
        } catch (Exception e) {
            System.err.println("UDP round " + round + " lỗi: " + e.getMessage());
        }

        double elapsedMs = (System.nanoTime() - start) / 1_000_000.0;
        System.out.printf("UDP round %d: %.3f ms, responses: %d/%d, lost: %d%n",
                round, elapsedMs, responses, MESSAGE_COUNT, MESSAGE_COUNT - responses);
    }

    private static byte[] createMessage(int sequence) {
        String prefix = String.format("%06d|", sequence);
        String message = prefix + "X".repeat(MESSAGE_SIZE - prefix.length());
        return message.getBytes(StandardCharsets.UTF_8);
    }

    private static void printEnvironment(String host, int port) {
        System.out.println("=== UDP BENCHMARK ===");
        System.out.println("OS: " + System.getProperty("os.name") + " "
                + System.getProperty("os.version"));
        System.out.println("Java: " + System.getProperty("java.version"));
        System.out.println("Server: " + host + ":" + port);
        System.out.println("Payload: " + MESSAGE_SIZE + " bytes UTF-8 ASCII");
        System.out.println("Messages per round: " + MESSAGE_COUNT);
        System.out.println("Rounds: " + TEST_ROUNDS);
        System.out.println("Timeout: " + TIMEOUT_MS + " ms/message");
        System.out.println("Method: request-response tuần tự, đo bằng System.nanoTime()");
    }
}
