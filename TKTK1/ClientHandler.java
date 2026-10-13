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
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                        connection.getOutputStream(), StandardCharsets.UTF_8), true)) {
            out = writer;

            // Bước 1: đọc tên client gửi đến.
            if (!readName(in)) {
                return;
            }

            // Bước 2: gửi danh sách phòng cho client lựa chọn.
            send("Xin chào " + name + "!");
            showRooms();

            // Bước 3: liên tục đọc lựa chọn, tin nhắn hoặc lệnh của client.
            String line;
            while ((line = in.readLine()) != null) {
                String command = line.trim();

                if (command.equalsIgnoreCase("/exit")) {
                    leaveRoom();
                    send("Đã ngắt kết nối. Tạm biệt " + name + "!");
                    break;
                } else if (command.equalsIgnoreCase("/quit")) {
                    leaveRoom();
                    showRooms();
                } else if (room == null) {
                    joinRoom(command);
                } else if (command.startsWith("/")) {
                    send("Lệnh không hợp lệ. Dùng /quit hoặc /exit.");
                } else if (!command.isEmpty()) {
                    room.sendMessage(this, "[" + room.getName() + "] " + name + ": " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Client ngắt kết nối: " + e.getMessage());
        } finally {
            // Nếu client đóng cửa sổ đột ngột, vẫn xóa client khỏi phòng.
            leaveRoom();
        }
    }

    private boolean readName(BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            name = line.trim();
            if (name.equalsIgnoreCase("/exit")) {
                send("Đã ngắt kết nối.");
                return false;
            }
            if (!name.isEmpty() && !name.startsWith("/")) {
                return true;
            }
            send("Tên không được rỗng hoặc bắt đầu bằng /. Nhập lại tên:");
        }
        return false;
    }

    private void showRooms() {
        send("=== DANH SÁCH PHÒNG ===");
        for (int i = 0; i < rooms.length; i++) {
            send((i + 1) + ". " + rooms[i].getName());
        }
        send("Nhập số hoặc tên phòng để tham gia; /exit để thoát.");
    }

    private void joinRoom(String choice) {
        for (int i = 0; i < rooms.length; i++) {
            String number = String.valueOf(i + 1);
            String roomName = rooms[i].getName();

            if (choice.equals(number) || choice.equalsIgnoreCase(roomName)) {
                room = rooms[i];
                room.addClient(this);
                return;
            }
        }
        send("Phòng không hợp lệ. Hãy chọn số hoặc tên phòng trong danh sách.");
    }

    private void leaveRoom() {
        if (room != null) {
            room.removeClient(this);
            room = null;
        }
    }

    public String getClientName() {
        return name;
    }

    // Các phòng dùng phương thức này để gửi một dòng đến client.
    public void send(String message) {
        out.println(message);
    }
}
