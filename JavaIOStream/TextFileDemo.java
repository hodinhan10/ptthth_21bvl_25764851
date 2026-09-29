package JavaIOStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class TextFileDemo {
	public static void main(String[] args) {
		Path file = Path.of("JavaIOStream", "data", "ghi_chu.txt");
		try {
			Files.createDirectories(file.getParent());
			System.out.println("Tệp: " + file.toAbsolutePath());
			try (BufferedWriter writer = Files.newBufferedWriter(
					file,
					StandardCharsets.UTF_8,
					StandardOpenOption.CREATE,
					StandardOpenOption.APPEND)) {
				writer.write("Java I/O làm việc với các luồng dữ liệu.");
				writer.newLine();
				writer.write("BufferedWriter giúp ghi văn bản hiệu quả.");
				writer.newLine();
				writer.write("UTF-8 hỗ trợ tiếng Việt ổn định.");
			}
			try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
				String line;
				int number = 1;
				while ((line = reader.readLine()) != null) {
					System.out.printf("%d. %s%n", number++, line);
				}
			}
				System.out.println("\nĐọc thử bằng ISO-8859-1:");
				try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.ISO_8859_1)) {
					String line;
					while ((line = reader.readLine()) != null) {
						System.out.println(line);
					}
				}
				System.out.println("Nhận xét: kết quả bị sai ký tự vì tệp được ghi bằng UTF-8 nhưng đọc bằng ISO-8859-1.");
		} catch (IOException e) {
			System.err.println("Lỗi xử lý tệp " + file + ": " + e.getMessage());
		}
	}
}
