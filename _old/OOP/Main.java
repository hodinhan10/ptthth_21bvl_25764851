package OOP;

import java.util.List;

public class Main {
	public static void main(String[] args) {
		NhanVien nv1 = new NhanVienToanThoiGian("nguyen an", "12345", 2100000, 1.5);
		NhanVien nv2 = new NhanVienBanThoiGian("tran binh", "56789", 500000, 3);

		List<NhanVien> ds = List.of(nv1, nv2);

		for (NhanVien nv : ds) {
			System.out.println(nv.tinhLuong());
		}
	}
}