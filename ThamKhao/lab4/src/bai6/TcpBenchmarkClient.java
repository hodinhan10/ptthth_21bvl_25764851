package ThamKhao.lab4.src.bai6;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class TcpBenchmarkClient {
    private static final int MESSAGE_COUNT = 1000;
    private static final int MESSAGE_SIZE = 256;
    private static final int RUNS = 5;

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port;
        try {
            port = args.length > 1 ? Integer.parseInt(args[1]) : 5010;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        byte[] request = new byte[MESSAGE_SIZE];
        byte[] response = new byte[MESSAGE_SIZE];
        Arrays.fill(request, (byte) 'A');
        System.out.printf("TCP: %d thông điệp, %d byte/thông điệp%n",
                MESSAGE_COUNT, MESSAGE_SIZE);

        for (int run = 1; run <= RUNS; run++) {
            try (Socket socket = new Socket(host, port);
                    BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
                    BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())) {
                long start = System.nanoTime();
                int received = 0;
                for (int i = 0; i < MESSAGE_COUNT; i++) {
                    out.write(request);
                    out.flush();
                    readFully(in, response);
                    received++;
                }
                double milliseconds = (System.nanoTime() - start) / 1_000_000.0;
                System.out.printf("Lần %d: %.3f ms, nhận %d/%d%n",
                        run, milliseconds, received, MESSAGE_COUNT);
            } catch (IOException e) {
                System.err.println("Lần " + run + " lỗi: " + e.getMessage());
            }
        }
    }

    private static void readFully(BufferedInputStream in, byte[] data) throws IOException {
        int offset = 0;
        while (offset < data.length) {
            int count = in.read(data, offset, data.length - offset);
            if (count == -1) throw new EOFException("Server đóng kết nối sớm");
            offset += count;
        }
    }
}
