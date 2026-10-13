package ThamKhao.lab4.src.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class UdpEchoClient {
    public static void main(String[] args) throws Exception {
        // Có tham số dòng lệnh thì dùng; nếu không có thì dùng giá trị mặc định.
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5001;
        String message = args.length > 2 ? args[2] : "xin chào UDP";

        // UDP gửi byte, nên đổi chuỗi message thành mảng byte theo UTF-8.
        byte[] data = message.getBytes(StandardCharsets.UTF_8);

        // Đổi tên máy, ví dụ localhost, thành địa chỉ IP của server.
        InetAddress server = InetAddress.getByName(host);

        // DatagramSocket là cửa giao tiếp UDP của client.
        // Client không chỉ định cổng nên hệ điều hành tự chọn một cổng còn trống.
        try (DatagramSocket socket = new DatagramSocket()) {
            // receive() chỉ chờ tối đa 3000 mili giây = 3 giây.
            socket.setSoTimeout(3000);

            // DatagramPacket gửi có 4 tham số:
            //   1) data: mảng byte cần gửi.
            //   2) data.length: số byte cần gửi trong mảng.
            //   3) server: địa chỉ IP của máy nhận.
            //   4) port: cổng của chương trình nhận trên máy đó.
            DatagramPacket request = new DatagramPacket(
                    data, data.length, server, port);

            // Gửi gói request tới địa chỉ và cổng được ghi trong gói.
            socket.send(request);

            // Tạo vùng nhớ trống, chứa tối đa 4096 byte phản hồi.
            byte[] buffer = new byte[4096];

            // DatagramPacket nhận có 2 tham số:
            //   1) buffer: mảng byte để chứa dữ liệu sắp nhận.
            //   2) buffer.length: số byte tối đa có thể nhận vào mảng.
            // Không cần IP/cổng vì đây là gói dùng để nhận; sau khi nhận, Java sẽ
            // điền địa chỉ và cổng của bên gửi vào chính response.
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            try {
                // Đứng chờ phản hồi; hết 3 giây sẽ phát sinh SocketTimeoutException.
                socket.receive(response);

                // new String có 4 tham số:
                //   1) response.getData(): mảng byte đang chứa phản hồi.
                //   2) response.getOffset(): vị trí bắt đầu dữ liệu trong mảng.
                //   3) response.getLength(): số byte thật sự nhận được.
                //   4) UTF_8: cách đổi các byte đó thành chữ.
                String text = new String(response.getData(),
                        response.getOffset(), response.getLength(),
                        StandardCharsets.UTF_8);
                System.out.println("Server: " + text);
            } catch (SocketTimeoutException e) {
                // UDP không bảo đảm gói tin đến nơi, nên client cần xử lý khi không có phản hồi.
                System.err.println("Hết 3 giây nhưng chưa nhận được phản hồi");
            }
        }
    }
}
