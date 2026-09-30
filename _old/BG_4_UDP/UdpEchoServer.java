import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

/**
 * UDP Echo Server lắng nghe tại cổng 6789.
 * Server nhận 10 thông điệp và gửi nguyên nội dung về cho client.
 */
public class UdpEchoServer {
    private static final int PORT = 6789;
    private static final int MESSAGE_COUNT = 10;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("UDP Server đã được tạo tại cổng " + PORT);

            for (int i = 0; i < MESSAGE_COUNT; i++) {
                byte[] receiveBuffer = new byte[BUFFER_SIZE];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                // Chờ nhận một gói tin từ client.
                socket.receive(receivePacket);

                String receiveData = new String(
                        receivePacket.getData(),
                        receivePacket.getOffset(),
                        receivePacket.getLength(),
                        StandardCharsets.UTF_8);

                System.out.println("Server nhận: " + receiveData
                        + " | IP: " + receivePacket.getAddress()
                        + " | Port: " + receivePacket.getPort()
                        + " | Số byte: " + receivePacket.getLength());

                // Gửi phản hồi về đúng địa chỉ IP và cổng của client.
                byte[] sendBuffer = receiveData.getBytes(StandardCharsets.UTF_8);
                DatagramPacket sendPacket = new DatagramPacket(
                        sendBuffer,
                        sendBuffer.length,
                        receivePacket.getAddress(),
                        receivePacket.getPort());
                socket.send(sendPacket);
            }

            System.out.println("Server đã phản hồi đủ 10 gói tin.");
        } catch (IOException e) {
            System.err.println("Lỗi UDP Server: " + e.getMessage());
        }
    }
}
