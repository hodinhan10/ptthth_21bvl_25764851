// Lớp này thuộc nhóm SocketTCP, tương ứng với thư mục SocketTCP.
package SocketTCP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPEchoServer {
    // Server mở cổng 7; client phải kết nối tới đúng cổng này.
    public final static int serverPort = 7;

    // Điểm bắt đầu chương trình server.
    public static void main(String[] args) {
        try {
            // ServerSocket là cửa chờ kết nối, chưa phải kết nối với một client cụ thể.
            ServerSocket ss = new ServerSocket(serverPort);
            System.out.println("Server da duoc tao");
            // Vòng ngoài: liên tục chờ và phục vụ từng client.
            while (true) {
                try {
                    // Đứng chờ client; khi có kết nối, trả về Socket để trao đổi dữ liệu.
                    Socket s = ss.accept();
                    System.out.println("Client da duoc ket noi");
                    // Luồng vào của server: nhận byte từ client.
                    InputStream is = s.getInputStream();
                    // Luồng ra của server: gửi byte về client.
                    OutputStream os = s.getOutputStream();
                    // Biến giữ mã byte vừa đọc; dùng int để có thể chứa cả -1.
                    int ch = 0;
                    // Vòng trong: nhận và trả lại dữ liệu của client hiện tại.
                    while(true){
                        // Chờ đọc một byte; trả về 0..255 hoặc -1 khi luồng kết thúc.
                        ch = is.read();
                        // Chỉ thoát vòng trong khi hết dữ liệu do phía client đóng luồng gửi.
                        if(ch == -1) break;
                        // In ký tự vừa nhận, ví dụ mã 48 được in thành 0.
                        System.out.println((char)ch);
                        // Echo (phản hồi nguyên dữ liệu): gửi lại đúng byte vừa nhận.
                        os.write(ch);
                    }
                    // Đóng kết nối này rồi quay lại vòng ngoài để chờ client tiếp theo.
                    s.close();
                } catch (IOException e) {
                    // Bắt lỗi chấp nhận kết nối hoặc trao đổi dữ liệu với client.
                    System.out.println("Error: Can Not create socket");  
                }
            }
        } catch (IOException ie) {
            // Bắt lỗi mở cổng server, ví dụ cổng đã bị chương trình khác sử dụng.
            System.out.println("Server Creation Error: " + ie);  

        } 
    }
}
