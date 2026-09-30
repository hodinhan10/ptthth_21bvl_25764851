package ThamKhao.lab4.src.bai6;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpBenchmarkServer {
    private static final int DEFAULT_PORT = 5010;

    public static void main(String[] args) {
        int port = parsePort(args, DEFAULT_PORT);
        if (port < 0) return;
        ExecutorService pool = Executors.newFixedThreadPool(10);
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("TCP benchmark server tại port " + port);
            while (true) {
                Socket socket = server.accept();
                pool.submit(() -> echo(socket));
            }
        } catch (IOException e) {
            System.err.println("Lỗi TCP server: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void echo(Socket socket) {
        try (socket;
                BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
                BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())) {
            byte[] buffer = new byte[4096];
            int count;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Lỗi client TCP: " + e.getMessage());
        }
    }

    private static int parsePort(String[] args, int defaultPort) {
        try {
            return args.length > 0 ? Integer.parseInt(args[0]) : defaultPort;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return -1;
        }
    }
}
