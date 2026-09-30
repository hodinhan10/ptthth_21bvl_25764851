package ThamKhao.lab4.src.bai6;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class UdpBenchmarkClient {
    private static final int MESSAGE_COUNT = 1000;
    private static final int MESSAGE_SIZE = 256;
    private static final int RUNS = 5;
    private static final int TIMEOUT_MS = 100;

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        int port;
        try {
            port = args.length > 1 ? Integer.parseInt(args[1]) : 5011;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        InetAddress server = InetAddress.getByName(host);
        byte[] data = new byte[MESSAGE_SIZE];
        Arrays.fill(data, (byte) 'A');
        System.out.printf("UDP: %d thông điệp, %d byte/thông điệp, timeout %d ms%n",
                MESSAGE_COUNT, MESSAGE_SIZE, TIMEOUT_MS);

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(TIMEOUT_MS);
            for (int run = 1; run <= RUNS; run++) {
                int received = 0;
                long start = System.nanoTime();
                for (int sequence = 0; sequence < MESSAGE_COUNT; sequence++) {
                    ByteBuffer.wrap(data).putInt(0, run).putInt(4, sequence);
                    socket.send(new DatagramPacket(data, data.length, server, port));
                    byte[] responseData = new byte[MESSAGE_SIZE];
                    DatagramPacket response = new DatagramPacket(responseData,
                            responseData.length);
                    try {
                        socket.receive(response);
                        ByteBuffer header = ByteBuffer.wrap(responseData);
                        if (header.getInt() == run && header.getInt() == sequence) received++;
                    } catch (SocketTimeoutException ignored) {
                        // Gói tin này được tính là mất.
                    }
                }
                double milliseconds = (System.nanoTime() - start) / 1_000_000.0;
                System.out.printf("Lần %d: %.3f ms, nhận %d/%d%n",
                        run, milliseconds, received, MESSAGE_COUNT);
            }
        }
    }
}
