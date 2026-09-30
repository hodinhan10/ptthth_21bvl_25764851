package ThamKhao.lab4.src.bai9;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;

public class MulticastReceiver {
    private static final String GROUP = "239.255.0.1";
    private static final int DEFAULT_PORT = 5014;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Cách dùng: java bai9.MulticastReceiver "
                    + "<tên-card-mạng> [port]");
            return;
        }

        InetSocketAddress groupAddress = null;
        NetworkInterface networkInterface = null;
        try {
            networkInterface = MulticastSupport.findInterface(args[0]);
            int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;
            groupAddress = new InetSocketAddress(InetAddress.getByName(GROUP), port);
            try (MulticastSocket socket = new MulticastSocket(port)) {
                socket.joinGroup(groupAddress, networkInterface);
                try {
                    System.out.println("Đang nghe " + GROUP + ":" + port
                            + " qua " + networkInterface.getName());
                    byte[] buffer = new byte[4096];
                    while (true) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        String message = new String(packet.getData(), packet.getOffset(),
                                packet.getLength(), StandardCharsets.UTF_8);
                        System.out.println(packet.getAddress().getHostAddress()
                                + ": " + message);
                        if (message.equalsIgnoreCase("QUIT")) break;
                    }
                } finally {
                    socket.leaveGroup(groupAddress, networkInterface);
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
        } catch (Exception e) {
            System.err.println("Nhận multicast lỗi: " + e.getMessage());
        }
    }
}
