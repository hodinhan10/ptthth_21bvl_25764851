package ThamKhao.lab4.src.bai8;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class FileTransferClient {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Cách dùng: java bai8.FileTransferClient <file> [host] [port]");
            return;
        }

        Path file = Path.of(args[0]);
        String host = args.length > 1 ? args[1] : "localhost";
        int port;
        try {
            port = args.length > 2 ? Integer.parseInt(args[2]) : 5013;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        if (!Files.isRegularFile(file)) {
            System.err.println("Không tìm thấy file: " + file);
            return;
        }

        try {
            String hash = sha256(file);
            try (Socket socket = new Socket(host, port);
                    DataOutputStream out = new DataOutputStream(
                            new BufferedOutputStream(socket.getOutputStream()));
                    DataInputStream in = new DataInputStream(
                            new BufferedInputStream(socket.getInputStream()));
                    InputStream fileIn = Files.newInputStream(file)) {
                out.writeUTF(file.getFileName().toString());
                out.writeLong(Files.size(file));
                out.writeUTF(hash);
                fileIn.transferTo(out);
                out.flush();
                System.out.println("Server: " + in.readUTF());
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("Gửi file lỗi: " + e.getMessage());
        }
    }

    private static String sha256(Path file)
            throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream in = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = in.read(buffer)) != -1) {
                digest.update(buffer, 0, count);
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }
}
