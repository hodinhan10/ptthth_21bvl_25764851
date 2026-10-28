package TKTK1;

import java.util.ArrayList;

// Một đối tượng ChatRoom tương ứng với một phòng chat.
public class ChatRoom {
    private final String name;
    private final ArrayList<ClientHandler> clients = new ArrayList<>();

    public ChatRoom(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // synchronized: mỗi lần chỉ một luồng được thêm, xóa hoặc gửi trong phòng.
    // Nhờ vậy, danh sách không bị thay đổi giữa lúc đang duyệt để gửi tin.
    public synchronized void onAddClient(ClientHandler client) {
        clients.add(client);
        client.onSend("Đã vào phòng " + name + ". Nhập tin nhắn để chat.");
        client.onSend("/quit: rời phòng và chọn lại; /exit: ngắt kết nối.");
        onSendMessage(client, "[Thông báo] " + client.getClientName() + " đã vào phòng.");
    }

    public synchronized void onRemoveClient(ClientHandler client) {
        clients.remove(client);
        onSendMessage(client, "[Thông báo] " + client.getClientName() + " đã rời phòng.");
    }

    public synchronized void onSendMessage(ClientHandler sender, String message) {
        // Danh sách này chỉ chứa người trong phòng hiện tại.
        for (ClientHandler client : clients) {
            // Không gửi tin nhắn trở lại chính người gửi.
            if (client != sender) {
                client.onSend(message);
            }
        }
    }
}
