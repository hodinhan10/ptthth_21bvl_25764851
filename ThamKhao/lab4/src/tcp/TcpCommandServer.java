// Thuộc package tcp; cùng nhóm với client và server phục vụ nhiều client.
package ThamKhao.lab4.src.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Locale;

public class TcpCommandServer {
    // Cổng chờ kết nối; client cần dùng cùng giá trị.
    private static final int PORT = 5000;

    public static void main(String[] args) {
        // Mở cửa chờ kết nối; try (...) tự đóng server khi rời khối.
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("TCP server listening on port " + PORT);
            while (true) {
                // Chờ client và tự đóng socket khi kết thúc phiên làm việc của client đó.
                try (Socket socket = server.accept()) {
                    // Phục vụ client ngay trên luồng main; xong mới chờ client tiếp theo.
                    serve(socket);
                } catch (IOException e) {
                    System.err.println("Lỗi phiên client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Không mở được server: " + e.getMessage());
        }
    }

    // Phục vụ một kết nối: nhận lệnh, xử lý và gửi phản hồi cho đến khi kết thúc
    // phiên.
    // Không ghi public/private: các lớp cùng package tcp có thể gọi phương thức
    // này.
    static void serve(Socket socket) throws IOException {
        try (
                // in: nhận và đọc từng dòng yêu cầu do client gửi đến.
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                // out: gửi từng dòng phản hồi từ server về client.
                // true tự đẩy dữ liệu đi khi gọi println; UTF-8 dùng để mã hóa chữ.
                PrintWriter out = new PrintWriter(new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            String request;
            // SERVER NHẬN: chờ một dòng client gửi; null nghĩa là luồng đã kết thúc.
            while ((request = in.readLine()) != null) {
                // Tạo câu trả lời từ lệnh vừa nhận; process không gửi dữ liệu qua mạng.
                String response = process(request);
                // SERVER GỬI: gửi câu trả lời kèm dấu xuống dòng về client.
                out.println(response);
                // Gửi OK BYE trước, rồi mới thoát vòng nhận khi gặp QUIT.
                if (request.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
        }
    }

    // Chỉ xử lý chuỗi: nhận request và trả về chuỗi response.
    static String process(String request) {
        // Bỏ khoảng trắng ở đầu và cuối để việc so sánh lệnh dễ hơn.
        String trimmed = request.trim();
        // equalsIgnoreCase so sánh không phân biệt chữ hoa/chữ thường.
        if (trimmed.equalsIgnoreCase("PING")) {
            return "OK PONG";
        }
        if (trimmed.equalsIgnoreCase("TIME")) {
            // Lấy ngày giờ hiện tại của máy chạy server.
            return "OK " + LocalDateTime.now();
        }
        if (trimmed.equalsIgnoreCase("QUIT")) {
            return "OK BYE";
        }
        // So sánh 6 ký tự đầu với "UPPER ", không phân biệt hoa/thường (true).
        if (trimmed.regionMatches(true, 0, "UPPER ", 0, 6)) {
            // Bỏ phần "UPPER " rồi đổi phần chữ còn lại thành chữ hoa.
            return "OK " + trimmed.substring(6).toUpperCase(Locale.ROOT);
        }
        // Không khớp lệnh nào thì trả thông báo lệnh không được nhận biết.
        return "ERR UNKNOWN_COMMAND";
    }
}
