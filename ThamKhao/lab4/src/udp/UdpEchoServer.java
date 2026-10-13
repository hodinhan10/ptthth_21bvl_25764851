package ThamKhao.lab4.src.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class UdpEchoServer {
    // Client phải gửi đến đúng cổng này.
    private static final int PORT = 5001;

    public static void main(String[] args) {
        // Vùng nhớ dùng để chứa dữ liệu nhận; tối đa 4096 byte cho mỗi gói.
        byte[] buffer = new byte[4096];

        // DatagramSocket là cửa giao tiếp UDP; server mở cửa tại cổng 5001.
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("UDP server listening on port " + PORT);

            // Server chạy liên tục để nhận hết gói này đến gói khác.
            while (true) {
                // Gói nhận có 2 tham số:
                //   1) buffer: mảng byte để chứa dữ liệu sắp nhận.
                //   2) buffer.length: số byte tối đa có thể nhận.
                // Chưa biết client nào gửi nên lúc tạo chưa cần địa chỉ và cổng.
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);

                // Đứng chờ một gói UDP rồi đặt dữ liệu, IP và cổng bên gửi vào request.
                socket.receive(request);

                // Đổi đúng phần byte vừa nhận thành chuỗi UTF-8.
                // Không dùng toàn bộ buffer vì phần dữ liệu thật có thể ngắn hơn 4096 byte.
                String message = new String(request.getData(),
                        request.getOffset(), request.getLength(),
                        StandardCharsets.UTF_8);

                // Tạo nội dung phản hồi: thêm ACK và đổi lời nhắn thành chữ hoa.
                String text = "ACK " + message.toUpperCase(Locale.ROOT);

                // UDP gửi byte, nên đổi chuỗi phản hồi thành mảng byte UTF-8.
                byte[] responseData = text.getBytes(StandardCharsets.UTF_8);

                // Gói gửi có 4 thông tin:
                //   1) responseData: mảng byte cần gửi.
                //   2) responseData.length: số byte cần gửi.
                //   3) request.getAddress(): IP của client vừa gửi yêu cầu.
                //   4) request.getPort(): cổng UDP của client vừa gửi yêu cầu.
                // Server lấy IP/cổng từ request để trả đúng về client đó.
                DatagramPacket response = new DatagramPacket(
                        responseData, responseData.length,
                        request.getAddress(), request.getPort());

                // Gửi gói phản hồi; xong thì quay lại đầu vòng lặp để chờ gói mới.
                socket.send(response);
            }
        } catch (IOException e) {
            System.err.println("UDP server error: " + e.getMessage());
        }
    }
}
