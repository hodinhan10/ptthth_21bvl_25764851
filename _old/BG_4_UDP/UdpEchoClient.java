import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * UDP Echo Client gửi 10 thông điệp đến server tại localhost:6789.
 */
public class UdpEchoClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 6789;
    private static final int MESSAGE_COUNT = 10;
    private static final int BUFFER_SIZE = 1024;
    private static final int DELAY_MILLISECONDS = 2000;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverIp = InetAddress.getByName(SERVER_HOST);
            System.out.println("UDP Client đã được tạo." + serverIp);

            for (int i = 0; i < MESSAGE_COUNT; i++) {
                String sendData = "data | " + i;
                byte[] sendBuffer = sendData.getBytes(StandardCharsets.UTF_8);

                DatagramPacket sendPacket = new DatagramPacket(
                        sendBuffer,
                        sendBuffer.length,
                        serverIp,
                        SERVER_PORT);
                System.out.println("Client gửi: " + sendData);
                socket.send(sendPacket);

                byte[] receiveBuffer = new byte[BUFFER_SIZE];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                String responseData = new String(
                        receivePacket.getData(),
                        receivePacket.getOffset(),
                        receivePacket.getLength(),
                        StandardCharsets.UTF_8);
                System.out.println("Server phản hồi: " + responseData);

                Thread.sleep(DELAY_MILLISECONDS);
            }

            System.out.println("Client đã gửi đủ 0-9 và nhận đủ phản hồi.");
        } catch (IOException e) {
            System.err.println("Lỗi UDP Client: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Client bị ngắt khi đang chờ.");
        }
    }
}
