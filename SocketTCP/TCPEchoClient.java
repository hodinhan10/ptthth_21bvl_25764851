// Lớp này thuộc nhóm SocketTCP, tương ứng với thư mục SocketTCP.
package SocketTCP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCPEchoClient {
    // Địa chỉ máy chạy server: 127.0.0.1 là chính máy đang chạy client.
    public final static String serverTP = "127.0.0.1";
    // Cổng đích phải trùng với cổng mà server mở để chờ kết nối.
    public final static int serverPort = 7;

    // Điểm bắt đầu chương trình. throws cho phép lỗi chưa được xử lý truyền ra ngoài main.
    public static void main(String[] args) throws InterruptedException, IOException {
        // Chưa kết nối nên s chưa trỏ tới socket nào.
        Socket s = null;
        try {
            // Tạo cửa giao tiếp TCP và kết nối tới server.
            s = new Socket(serverTP, serverPort);
            System.out.println("Client da duoc tao");
            // Luồng vào: nhận các byte do server gửi về.
            InputStream is = s.getInputStream();
            // Luồng ra: gửi các byte tới server.
            OutputStream os = s.getOutputStream();
            // '0' và '9' là ký tự có mã 48 và 57, không phải số 0 và 9.
            for (int i = '0'; i <= '9'; i++) {
                // Gửi một byte chứa mã của ký tự hiện tại.
                os.write(i);
                // Chờ đọc một byte trả về; -1 nghĩa là luồng đã kết thúc.
                int ch = is.read();
                // Đổi mã nhận được thành ký tự để in, ví dụ 48 thành '0'.
                System.out.println((char)ch);
                // Tạm dừng luồng thực thi hiện tại 2000 mili giây = 2 giây.
                Thread.sleep(2000);
            }
        } catch (IOException e) {
            // Bắt lỗi kết nối hoặc gửi/nhận; thông báo hiện tại dùng chung cho các lỗi này.
            System.out.println("Error: Can Not create socket");  
        } finally {
            // Luôn đi qua đây khi rời try/catch; chỉ đóng nếu đã tạo được socket.
            if(s != null){
                // Đóng kết nối và cả hai luồng dữ liệu gắn với socket.
                s.close();
            }
        }
    }
}
