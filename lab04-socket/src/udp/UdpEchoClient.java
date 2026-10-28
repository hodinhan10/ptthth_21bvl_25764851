package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class UdpEchoClient {
    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5001;
        String message = args.length > 2 ? args[2] : "xin chào UDP";
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        InetAddress server = InetAddress.getByName(host);

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(3000);
            socket.send(new DatagramPacket(data, data.length, server, port));

            byte[] buf = new byte[4096];
            DatagramPacket res = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(res);
                String text = new String(res.getData(), res.getOffset(), res.getLength(), StandardCharsets.UTF_8);
                System.out.println("Server: " + text);
            } catch (SocketTimeoutException e) {
                System.err.println("Hết 3 giây nhưng chưa nhận được phản hồi");
            }
        }
    }
}
