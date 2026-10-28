package TKTK1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

// Mỗi ClientHandler phụ trách một client kết nối đến server.
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ChatRoom[] rooms;
    private PrintWriter out;
    private String name;
    // Chưa chọn phòng hoặc đã rời phòng thì room bằng null.
    private ChatRoom room;

    public ClientHandler(Socket socket, ChatRoom[] rooms) {
        this.socket = socket;
        this.rooms = rooms;
    }

    // Khi gọi thread.start(), luồng mới thực hiện phương thức run().
    @Override
    public void run() {
        try (Socket connection = socket;
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(
                        connection.getOutputStream(), StandardCharsets.UTF_8), true)) {
            this.out = out;
            // Bước 1: đọc tên client gửi đến.
            if (!onReadName(in)) {
                return;
            }

            // Bước 2: gửi danh sách phòng cho client lựa chọn.
            onSend("Xin chào " + name + "!");
            onShowRooms();

            // Bước 3: liên tục đọc lựa chọn, tin nhắn hoặc lệnh của client.
            String line;
            while ((line = in.readLine()) != null) {
                String command = line.trim();

                if (command.equalsIgnoreCase("/exit")) {
                    onLeaveRoom();
                    onSend("Đã ngắt kết nối. Tạm biệt " + name + "!");
                    break;
                } else if (command.equalsIgnoreCase("/quit")) {
                    onLeaveRoom();
                    onShowRooms();
                } else if (room == null) {
                    onJoinRoom(command);
                } else if (command.startsWith("/")) {
                    onSend("Lệnh không hợp lệ. Dùng /quit hoặc /exit.");
                } else if (!command.isEmpty()) {
                    room.onSendMessage(this, "[" + room.getName() + "] " + name + ": " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Client ngắt kết nối: " + e.getMessage());
        } finally {
            // Nếu client đóng cửa sổ đột ngột, vẫn xóa client khỏi phòng.
            onLeaveRoom();
        }
    }

    private boolean onReadName(BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            name = line.trim();
            if (name.equalsIgnoreCase("/exit")) {
                onSend("Đã ngắt kết nối.");
                return false;
            }
            if (!name.isEmpty() && !name.startsWith("/")) {
                return true;
            }
            onSend("Tên không được rỗng hoặc bắt đầu bằng /. Nhập lại tên:");
        }
        return false;
    }

    private void onShowRooms() {
        onSend("=== DANH SÁCH PHÒNG ===");
        for (int i = 0; i < rooms.length; i++) {
            onSend((i + 1) + ". " + rooms[i].getName());
        }
        onSend("Nhập số hoặc tên phòng để tham gia; /exit để thoát.");
    }

    private void onJoinRoom(String choice) {
        for (int i = 0; i < rooms.length; i++) {
            if (choice.equals(String.valueOf(i + 1)) || choice.equalsIgnoreCase(rooms[i].getName())) {
                room = rooms[i];
                room.onAddClient(this);
                return;
            }
        }
        onSend("Phòng không hợp lệ. Hãy chọn số hoặc tên phòng trong danh sách.");
    }

    private void onLeaveRoom() {
        if (room != null) {
            room.onRemoveClient(this);
            room = null;
        }
    }

    public String getClientName() {
        return name;
    }

    // Các phòng dùng phương thức này để gửi một dòng đến client.
    public void onSend(String message) {
        out.println(message);
    }
}
