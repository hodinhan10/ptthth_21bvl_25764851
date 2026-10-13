package TKTK1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private static final int PORT = 5008;

    public static void main(String[] args) {
        // Tạo các phòng một lần. Mọi client đều sử dụng cùng danh sách này.
        ChatRoom[] rooms = {
                new ChatRoom("Study"),
                new ChatRoom("Game"),
                new ChatRoom("TraiNghiem")
        };

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server chat đang chạy tại cổng " + PORT);

            while (true) {
                // Chờ một client kết nối đến server.
                Socket socket = server.accept();

                // Giao client này cho một luồng riêng rồi tiếp tục chờ client khác.
                ClientHandler handler = new ClientHandler(socket, rooms);
                Thread thread = new Thread(handler);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Lỗi server: " + e.getMessage());
        }
    }
}
