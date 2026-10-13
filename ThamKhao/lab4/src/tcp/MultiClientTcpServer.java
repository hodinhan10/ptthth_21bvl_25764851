// Cùng package tcp nên gọi được TcpCommandServer.serve(socket).
package ThamKhao.lab4.src.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiClientTcpServer {
    private static final int PORT = 5000;
    // Tối đa 20 tác vụ phục vụ client chạy cùng lúc; tác vụ dư sẽ chờ trong hàng
    // đợi.
    private static final int MAX_CLIENTS = 20;

    public static void main(String[] args) {
        // Pool là nhóm luồng làm việc, tái sử dụng luồng cho các tác vụ phục vụ client.
        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTS);
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Multi-client server on port " + PORT);
            while (true) {
                // Luồng main chờ và nhận kết nối mới.
                Socket socket = server.accept();
                // Giao công việc cho pool rồi main quay lại accept, không chờ công việc hoàn
                // tất.
                // () -> { ... } là đoạn công việc chạy trên một luồng của pool.
                pool.submit(() -> {
                    // Lấy địa chỉ IP và cổng của client để ghi thông báo.
                    String client = String.valueOf(
                            socket.getRemoteSocketAddress());
                    System.out.println("Connected: " + client);
                    // Tự đóng socket khi tác vụ kết thúc, kể cả khi xảy ra lỗi.
                    try (socket) {
                        // Dùng lại cách nhận lệnh và trả lời của server đơn client.
                        TcpCommandServer.serve(socket);
                    } catch (IOException e) {
                        System.err.println("Client " + client
                                + " failed: " + e.getMessage());
                    } finally {
                        // Thông báo khi rời phiên làm việc, dù thành công hay có lỗi.
                        System.out.println("Disconnected: " + client);
                    }
                });
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            // Ngừng nhận tác vụ mới; các tác vụ đã nhận vẫn được phép chạy tiếp.
            pool.shutdown();
        }
    }
}
