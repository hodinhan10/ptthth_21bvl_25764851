package TKTK1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatClient {
    private static final int PORT = 5008;

    public static void main(String[] args) {
        // Không truyền địa chỉ thì kết nối tới server trên cùng máy.
        String host = args.length > 0 ? args[0] : "localhost";
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        try {
            String name;
            while (true) {
                System.out.print("Nhập tên của bạn: ");
                name = console.readLine();
                if (name == null || name.trim().equalsIgnoreCase("/exit")) {
                    return;
                }
                // Tên hợp lệ thì thoát vòng lặp; chưa hợp lệ thì nhập lại.
                if (!name.trim().isEmpty() && !name.trim().startsWith("/")) {
                    break;
                }
            }

            try (Socket socket = new Socket(host, PORT);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                    PrintWriter out = new PrintWriter(
                            new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
                // Gửi tên trước; sau đó server gửi danh sách phòng về.
                out.println(name.trim());

                // Một luồng đọc bàn phím để gửi, luồng main nhận tin từ server.
                Thread sender = new Thread(() -> sendFromKeyboard(console, out), "gui-tin");
                // Cho phép chương trình kết thúc khi server đóng kết nối,
                // kể cả khi luồng bàn phím còn đang chờ người dùng nhập.
                sender.setDaemon(true);
                sender.start();

                String response;
                while ((response = in.readLine()) != null) {
                    System.out.println(response);
                }
                System.out.println("Kết nối đã đóng.");
            }
        } catch (IOException e) {
            System.err.println("Lỗi kết nối: " + e.getMessage());
        }
    }

    private static void sendFromKeyboard(BufferedReader console, PrintWriter out) {
        try {
            String line;
            while ((line = console.readLine()) != null) {
                // println thêm dấu xuống dòng; true trong PrintWriter đẩy dữ liệu đi ngay.
                out.println(line);
                if (line.trim().equalsIgnoreCase("/exit") || out.checkError()) {
                    return;
                }
            }
            // Nếu đầu vào bàn phím kết thúc, yêu cầu server đóng kết nối.
            out.println("/exit");
        } catch (IOException e) {
            System.err.println("Lỗi đọc bàn phím: " + e.getMessage());
            out.println("/exit");
        }
    }
}
