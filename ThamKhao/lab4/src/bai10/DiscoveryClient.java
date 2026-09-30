package ThamKhao.lab4.src.bai10;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class DiscoveryClient {
    private static final int DEFAULT_DISCOVERY_PORT = 5015;
    private static final int TIMEOUT_MS = 2000;

    public static void main(String[] args) {
        String target = args.length > 0 ? args[0] : "255.255.255.255";
        int discoveryPort;
        try {
            discoveryPort = args.length > 1
                    ? Integer.parseInt(args[1])
                    : DEFAULT_DISCOVERY_PORT;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        Map<String, ServiceInfo> services = discover(target, discoveryPort);
        if (services.isEmpty()) {
            System.out.println("Không tìm thấy dịch vụ trong " + TIMEOUT_MS + " ms");
            return;
        }

        System.out.println("Các dịch vụ tìm thấy:");
        services.values().forEach(System.out::println);
        connectAndPing(services.values().iterator().next());
    }

    private static Map<String, ServiceInfo> discover(String target, int port) {
        Map<String, ServiceInfo> services = new LinkedHashMap<>();
        byte[] data = "DISCOVER_SERVICE".getBytes(StandardCharsets.UTF_8);
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            socket.setSoTimeout(TIMEOUT_MS);
            InetAddress address = InetAddress.getByName(target);
            socket.send(new DatagramPacket(data, data.length, address, port));

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(response);
                } catch (SocketTimeoutException e) {
                    break;
                }

                String text = new String(response.getData(), response.getOffset(),
                        response.getLength(), StandardCharsets.UTF_8);
                String[] parts = text.split("\\s+");
                if (parts.length != 4 || !parts[0].equals("SERVICE")) continue;
                try {
                    ServiceInfo info = new ServiceInfo(response.getAddress(), parts[1],
                            Integer.parseInt(parts[2]), parts[3]);
                    services.putIfAbsent(info.key(), info);
                } catch (NumberFormatException ignored) {
                    // Bỏ qua phản hồi không đúng định dạng.
                }
            }
        } catch (IOException e) {
            System.err.println("Khám phá dịch vụ lỗi: " + e.getMessage());
        }
        return services;
    }

    private static void connectAndPing(ServiceInfo service) {
        try (Socket socket = new Socket(service.address(), service.port());
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            out.println("PING");
            System.out.println("TCP " + service.address().getHostAddress()
                    + ": " + in.readLine());
            out.println("QUIT");
            in.readLine();
        } catch (IOException e) {
            System.err.println("Không kết nối được TCP service: " + e.getMessage());
        }
    }

    private record ServiceInfo(InetAddress address, String name, int port, String version) {
        String key() {
            return address.getHostAddress() + ":" + port + ":" + name + ":" + version;
        }

        @Override
        public String toString() {
            return name + " " + version + " tại "
                    + address.getHostAddress() + ":" + port;
        }
    }
}
