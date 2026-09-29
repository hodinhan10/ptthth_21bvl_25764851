package JavaIOStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InventoryManager {
	private static final Path DATA_DIRECTORY = Path.of("JavaIOStream", "data");
	private static final Path INVENTORY_FILE = DATA_DIRECTORY.resolve("inventory.csv");
	private static final Path REPORT_FILE = DATA_DIRECTORY.resolve("inventory-report.txt");

	public static void main(String[] args) {
		try (BufferedReader console = new BufferedReader(
				new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
			List<Product> enteredProducts = readProductsFromConsole(console);
			writeInventory(enteredProducts);

			List<Product> products = readInventory();
			displaySummary(products);
			writeReport(products);
		} catch (IOException e) {
			System.err.println("Lỗi I/O với tệp " + INVENTORY_FILE + ": " + e.getMessage());
		} catch (IllegalArgumentException e) {
			System.err.println("Dữ liệu nhập không hợp lệ: " + e.getMessage());
		}
	}

	private static List<Product> readProductsFromConsole(BufferedReader console) throws IOException {
		List<Product> products = new ArrayList<>();
		int count = readNonNegativeInt(console, "Nhập số sản phẩm: ");

		for (int index = 1; index <= count; index++) {
			System.out.println("Sản phẩm " + index + ":");
			String code = readLine(console, "  Mã: ");
			String name = readLine(console, "  Tên: ");
			String unitPriceText = readLine(console, "  Đơn giá: ");
			String quantityText = readLine(console, "  Số lượng: ");

			try {
				double unitPrice = Double.parseDouble(unitPriceText);
				int quantity = Integer.parseInt(quantityText);
				products.add(new Product(code, name, unitPrice, quantity));
			} catch (IllegalArgumentException e) {
				System.err.println("Từ chối sản phẩm " + index + ": " + e.getMessage());
			}
		}
		return products;
	}

	private static List<Product> readInventory() throws IOException {
		List<Product> products = new ArrayList<>();
		if (!Files.exists(INVENTORY_FILE)) {
			throw new IOException("Không tìm thấy tệp " + INVENTORY_FILE);
		}

		try (BufferedReader reader = Files.newBufferedReader(INVENTORY_FILE, StandardCharsets.UTF_8)) {
			String header = reader.readLine();
			if (header == null) {
				return products;
			}
			String line;
			int lineNumber = 1;
			while ((line = reader.readLine()) != null) {
				lineNumber++;
				if (line.isBlank()) {
					continue;
				}

				String[] columns = line.split(",", -1);
				if (columns.length != 4) {
					System.err.println("Bỏ qua tệp " + INVENTORY_FILE + ", dòng "
							+ lineNumber + ": cần 4 cột nhưng có " + columns.length);
					continue;
				}
				try {
					products.add(new Product(
							columns[0].trim(),
							columns[1].trim(),
							Double.parseDouble(columns[2].trim()),
							Integer.parseInt(columns[3].trim())));
				} catch (IllegalArgumentException e) {
					System.err.println("Bỏ qua tệp " + INVENTORY_FILE + ", dòng "
							+ lineNumber + ": " + e.getMessage());
				}
			}
		}
		return products;
	}

	private static void writeInventory(List<Product> products) throws IOException {
		Files.createDirectories(DATA_DIRECTORY);
		try (BufferedWriter writer = Files.newBufferedWriter(INVENTORY_FILE, StandardCharsets.UTF_8)) {
			writer.write("ma,ten,donGia,soLuong");
			writer.newLine();
			for (Product product : products) {
				writer.write(product.toCsv());
				writer.newLine();
			}
		}
	}

	private static void displaySummary(List<Product> products) {
		double total = products.stream().mapToDouble(Product::inventoryValue).sum();
		System.out.println("\nDanh sách sản phẩm:");
		products.forEach(product -> System.out.printf(
				"%s | %s | giá %,.0f | SL %d | giá trị %,.0f VND%n",
				product.getCode(), product.getName(), product.getUnitPrice(),
				product.getQuantity(), product.inventoryValue()));
		System.out.printf("Tổng giá trị tồn kho: %,.0f VND%n", total);
		products.stream().max(Comparator.comparingDouble(Product::inventoryValue)).ifPresent(product ->
				System.out.printf("Sản phẩm có giá trị tồn kho cao nhất: %s (%s) - %,.0f VND%n",
						product.getCode(), product.getName(), product.inventoryValue()));
	}

	private static void writeReport(List<Product> products) throws IOException {
		double total = products.stream().mapToDouble(Product::inventoryValue).sum();
		Files.createDirectories(DATA_DIRECTORY);
		try (BufferedWriter writer = Files.newBufferedWriter(REPORT_FILE, StandardCharsets.UTF_8)) {
			writer.write("BÁO CÁO TỒN KHO");
			writer.newLine();
			writer.write("Số sản phẩm: " + products.size());
			writer.newLine();
			writer.write(String.format("Tổng giá trị tồn kho: %,.0f VND", total));
			writer.newLine();
			Product mostValuable = products.stream()
					.max(Comparator.comparingDouble(Product::inventoryValue))
					.orElse(null);
			if (mostValuable != null) {
				writer.write(String.format("Giá trị cao nhất: %s - %s (%,.0f VND)",
						mostValuable.getCode(), mostValuable.getName(), mostValuable.inventoryValue()));
				writer.newLine();
			}
		}
	}

	private static String readLine(BufferedReader console, String prompt) throws IOException {
		System.out.print(prompt);
		String line = console.readLine();
		return line == null ? "" : line.trim();
	}

	private static int readNonNegativeInt(BufferedReader console, String prompt) throws IOException {
		String value = readLine(console, prompt);
		try {
			int number = Integer.parseInt(value);
			if (number >= 0) {
				return number;
			}
		} catch (NumberFormatException ignored) {
		}
		throw new IllegalArgumentException("Số nguyên không âm không hợp lệ: " + value);
	}
}
