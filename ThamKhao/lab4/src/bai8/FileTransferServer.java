package ThamKhao.lab4.src.bai8;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileTransferServer {
    private static final int DEFAULT_PORT = 5013;
    private static final long MAX_FILE_SIZE = 100L * 1024 * 1024;
    private static final Path UPLOAD_DIRECTORY = Path.of("upload");

    public static void main(String[] args) {
        int port;
        try {
            port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return;
        }

        ExecutorService pool = Executors.newFixedThreadPool(10);
        try {
            Files.createDirectories(UPLOAD_DIRECTORY);
            try (ServerSocket server = new ServerSocket(port)) {
                System.out.println("File server tại port " + port);
                while (true) {
                    Socket socket = server.accept();
                    pool.submit(() -> receive(socket));
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi file server: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void receive(Socket socket) {
        Path temporaryFile = null;
        try (socket;
                DataInputStream in = new DataInputStream(
                        new BufferedInputStream(socket.getInputStream()));
                DataOutputStream out = new DataOutputStream(
                        new BufferedOutputStream(socket.getOutputStream()))) {
            String fileName = in.readUTF();
            long size = in.readLong();
            String expectedHash = in.readUTF();

            if (!isSafeFileName(fileName)) {
                send(out, "ERR INVALID_FILE_NAME");
                return;
            }
            if (size < 0 || size > MAX_FILE_SIZE) {
                send(out, "ERR INVALID_FILE_SIZE");
                return;
            }
            if (!expectedHash.matches("[0-9a-fA-F]{64}")) {
                send(out, "ERR INVALID_HASH");
                return;
            }

            temporaryFile = Files.createTempFile(UPLOAD_DIRECTORY, "receiving-", ".tmp");
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (OutputStream fileOut = Files.newOutputStream(temporaryFile)) {
                byte[] buffer = new byte[8192];
                long remaining = size;
                while (remaining > 0) {
                    int count = in.read(buffer, 0,
                            (int) Math.min(buffer.length, remaining));
                    if (count == -1) throw new EOFException("Thiếu dữ liệu file");
                    fileOut.write(buffer, 0, count);
                    digest.update(buffer, 0, count);
                    remaining -= count;
                }
            }

            String actualHash = HexFormat.of().formatHex(digest.digest());
            if (!actualHash.equalsIgnoreCase(expectedHash)) {
                send(out, "ERR HASH_MISMATCH");
                return;
            }

            Path destination = UPLOAD_DIRECTORY.resolve(fileName);
            Files.move(temporaryFile, destination, StandardCopyOption.REPLACE_EXISTING);
            temporaryFile = null;
            send(out, "OK " + actualHash);
        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("Nhận file lỗi: " + e.getMessage());
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {
                    // File tạm sẽ được dọn thủ công nếu hệ thống không cho phép xóa.
                }
            }
        }
    }

    private static boolean isSafeFileName(String fileName) {
        if (fileName.isBlank() || fileName.equals(".") || fileName.equals("..")) return false;
        try {
            return Path.of(fileName).getFileName().toString().equals(fileName)
                    && !fileName.contains("/") && !fileName.contains("\\");
        } catch (InvalidPathException e) {
            return false;
        }
    }

    private static void send(DataOutputStream out, String response) throws IOException {
        out.writeUTF(response);
        out.flush();
    }
}
