package ThamKhao.lab4.src.bai4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class CalculatorServer {
    private static final int DEFAULT_PORT = 5005;

    public static void main(String[] args) {
        int port = parsePort(args, DEFAULT_PORT);
        if (port < 0) return;

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Calculator server đang chạy tại port " + port);
            while (true) {
                try (Socket socket = server.accept()) {
                    serve(socket);
                } catch (IOException e) {
                    System.err.println("Lỗi phiên client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Không mở được server: " + e.getMessage());
        }
    }

    private static int parsePort(String[] args, int defaultPort) {
        try {
            return args.length > 0 ? Integer.parseInt(args[0]) : defaultPort;
        } catch (NumberFormatException e) {
            System.err.println("Port phải là số nguyên");
            return -1;
        }
    }

    private static void serve(Socket socket) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            String request;
            while ((request = in.readLine()) != null) {
                if (request.equalsIgnoreCase("QUIT")) {
                    out.println("OK BYE");
                    break;
                }
                out.println(process(request));
            }
        }
    }

    static String process(String request) {
        String[] parts = request.trim().split("\\s+");
        if (parts.length != 4 || !parts[0].equalsIgnoreCase("CALC")) {
            return "ERR INVALID_FORMAT";
        }

        if (!parts[1].matches("[+\\-*/]")) {
            return "ERR UNSUPPORTED_OPERATOR";
        }

        try {
            BigDecimal left = new BigDecimal(parts[2]);
            BigDecimal right = new BigDecimal(parts[3]);
            BigDecimal result;
            switch (parts[1]) {
                case "+" -> result = left.add(right);
                case "-" -> result = left.subtract(right);
                case "*" -> result = left.multiply(right);
                case "/" -> {
                    if (right.compareTo(BigDecimal.ZERO) == 0) {
                        return "ERR DIVIDE_BY_ZERO";
                    }
                    result = left.divide(right, MathContext.DECIMAL64);
                }
                default -> throw new IllegalStateException();
            }
            return "OK " + result.stripTrailingZeros().toPlainString();
        } catch (NumberFormatException e) {
            return "ERR INVALID_NUMBER";
        }
    }
}
