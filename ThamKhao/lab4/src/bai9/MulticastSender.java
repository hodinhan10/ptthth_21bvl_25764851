package ThamKhao.lab4.src.bai9;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;

public class MulticastSender {
    private static final String GROUP = "239.255.0.1";
    private static final int DEFAULT_PORT = 5014;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Cách dùng: java bai9.MulticastSender "
                    + "<tên-card-mạng> <thông-báo> [port]");
            return;
        }

        try {
            NetworkInterface networkInterface = MulticastSupport.findInterface(args[0]);
            int port = args.length > 2 ? Integer.parseInt(args[2]) : DEFAULT_PORT;
            byte[] data = args[1].getBytes(StandardCharsets.UTF_8);
            InetAddress group = InetAddress.getByName(GROUP);
            try (MulticastSocket socket = new MulticastSocket()) {
                socket.setNetworkInterface(networkInterface);
                socket.setTimeToLive(1);
                socket.send(new DatagramPacket(data, data.length, group, port));
                System.out.println("Đã gửi tới " + GROUP + ":" + port
                        + " qua " + networkInterface.getName());
            }
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
        } catch (Exception e) {
            System.err.println("Gửi multicast lỗi: " + e.getMessage());
        }
    }
}
