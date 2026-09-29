package SocketTCP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPEchoServer {
    public final static int serverPort = 7;

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(serverPort);
            System.out.println("Server da duoc tao");
            while (true) {
                try {
                    Socket s = ss.accept();
                    System.out.println("Client da duoc ket noi");
                    InputStream is = s.getInputStream();
                    OutputStream os = s.getOutputStream();
                    int ch = 0;
                    while(true){
                        ch = is.read();
                        if(ch == -1) break;
                        System.out.println((char)ch);
                        os.write(ch);
                    }
                    s.close();
                } catch (IOException e) {
                    System.out.println("Error: Can Not create socket");  
                }
            }
        } catch (IOException ie) {
            System.out.println("Server Creation Error: " + ie);  

        } 
    }
}
