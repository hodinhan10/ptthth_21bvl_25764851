package ThamKhao.lab4.src.bai10;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiscoveryServiceServer {
    private static final int DEFAULT_DISCOVERY_PORT = 5015;
    private static final int DEFAULT_TCP_PORT = 5016;
    private static final String SERVICE_NAME = "DATE_TIME";
    private static final String VERSION = "1.0";

    public static void main(String[] args) {
        int discoveryPort;
        int tcpPort;
        try {
            discoveryPort = args.length > 0
                    ? Integer.parseInt(args[0])
                    : DEFAULT_DISCOVERY_PORT;
            tcpPort = args.length > 1
                    ? Integer.parseInt(args[1])
                    : DEFAULT_TCP_PORT;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        ExecutorService pool = Executors.newFixedThreadPool(20);
        int finalTcpPort = tcpPort;
        Thread discoveryThread = new Thread(
                () -> discover(discoveryPort, finalTcpPort), "udp-discovery");
        discoveryThread.setDaemon(true);
        discoveryThread.start();

        try (ServerSocket server = new ServerSocket(tcpPort)) {
            System.out.println("TCP service tại port " + tcpPort);
            while (true) {
                Socket socket = server.accept();
                pool.submit(() -> serve(socket));
            }
        } catch (IOException e) {
            System.err.println("Lỗi TCP service: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void discover(int discoveryPort, int tcpPort) {
        byte[] buffer = new byte[1024];
        try (DatagramSocket socket = new DatagramSocket(discoveryPort)) {
            socket.setBroadcast(true);
            System.out.println("UDP discovery tại port " + discoveryPort);
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);
                String message = new String(request.getData(), request.getOffset(),
                        request.getLength(), StandardCharsets.UTF_8);
                if (!message.equals("DISCOVER_SERVICE")) continue;

                String text = "SERVICE " + SERVICE_NAME + " " + tcpPort + " " + VERSION;
                byte[] responseData = text.getBytes(StandardCharsets.UTF_8);
                socket.send(new DatagramPacket(responseData, responseData.length,
                        request.getAddress(), request.getPort()));
            }
        } catch (IOException e) {
            System.err.println("Lỗi UDP discovery: " + e.getMessage());
        }
    }

    private static void serve(Socket socket) {
        try (socket;
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            String request;
            while ((request = in.readLine()) != null) {
                String command = request.trim().toUpperCase();
                if (command.equals("PING")) {
                    out.println("OK PONG");
                } else if (command.equals("TIME")) {
                    out.println("OK " + LocalDateTime.now());
                } else if (command.equals("QUIT")) {
                    out.println("OK BYE");
                    break;
                } else {
                    out.println("ERR UNKNOWN_COMMAND");
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi client TCP: " + e.getMessage());
        }
    }
}
