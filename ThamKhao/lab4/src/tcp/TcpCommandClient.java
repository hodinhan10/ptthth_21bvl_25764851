// Thuộc package tcp; chạy bằng tên đầy đủ tcp.TcpCommandClient.
package ThamKhao.lab4.src.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpCommandClient {
    public static void main(String[] args) {
        // Có tham số thì dùng tham số; không có thì kết nối tới chính máy này, cổng
        // 5000.
        // Lưu ý: parseInt ở ngoài try nên catch NumberFormatException bên dưới chưa bắt
        // được lỗi tại đây.
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5000;

        // try (...) tự đóng các tài nguyên khi kết thúc khối, kể cả khi có lỗi.
        try (Socket socket = new Socket(host, port);
                // 1. console: nhận chữ từ BÀN PHÍM.
                //
                // InputStreamReader có 2 tham số:
                //   1) System.in: luồng byte nhận từ bàn phím.
                //   2) UTF_8: cách đổi các byte đó thành ký tự.
                //
                // BufferedReader có 1 tham số:
                //   1) InputStreamReader: nơi cung cấp các ký tự để đọc thành từng dòng.
                //
                // Dữ liệu đi như sau:
                //   bàn phím -> System.in (byte) -> InputStreamReader (ký tự)
                //             -> BufferedReader -> console.readLine() (một dòng)
                BufferedReader console = new BufferedReader(
                        new InputStreamReader(System.in, StandardCharsets.UTF_8));

                // 2. in: nhận chữ từ SERVER.
                //
                // InputStreamReader có 2 tham số:
                //   1) socket.getInputStream(): luồng byte đi từ server vào client.
                //   2) UTF_8: cách đổi các byte nhận được thành ký tự.
                //
                // BufferedReader có 1 tham số:
                //   1) InputStreamReader: nơi cung cấp các ký tự để đọc thành từng dòng.
                //
                // Dữ liệu đi như sau:
                //   server -> socket (byte) -> InputStreamReader (ký tự)
                //          -> BufferedReader -> in.readLine() (một dòng)
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

                // 3. out: gửi chữ từ CLIENT tới SERVER.
                //
                // OutputStreamWriter có 2 tham số:
                //   1) socket.getOutputStream(): luồng byte dùng để gửi tới server.
                //   2) UTF_8: cách đổi ký tự thành byte trước khi gửi.
                //
                // PrintWriter có 2 tham số:
                //   1) OutputStreamWriter: nơi nhận chữ và chuyển chữ thành byte.
                //   2) true: tự flush (đẩy dữ liệu đi) khi gọi println, printf hoặc format.
                //
                // Dữ liệu đi như sau:
                //   out.println() -> PrintWriter (chữ) -> OutputStreamWriter (byte)
                //                 -> socket -> server
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            System.out.println("Connected. Enter PING, TIME, UPPER text, QUIT");

            String request;
            // Chờ nhập một dòng từ bàn phím, gán vào request; null nghĩa là đầu vào đã kết
            // thúc.
            while ((request = console.readLine()) != null) {
                // CLIENT GỬI: gửi dòng vừa nhập kèm dấu xuống dòng tới server.
                out.println(request);
                // CLIENT NHẬN: chờ đọc một dòng phản hồi từ server.
                String response = in.readLine();
                // null nghĩa là luồng từ server đã kết thúc, không phải một dòng rỗng.
                if (response == null) {
                    System.out.println("Server closed the connection");
                    break;
                }
                System.out.println("Server: " + response);
                // Đã đọc và in phản hồi rồi mới thoát nếu lệnh vừa gửi là QUIT.
                if (request.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
        } catch (IOException e) {
            System.err.println("Lỗi kết nối: " + e.getMessage());
        }
    }
}
