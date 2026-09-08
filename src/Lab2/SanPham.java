package Lab2;

import java.text.NumberFormat;
import java.util.Locale;

public class SanPham {
	private int ma;
	private String ten;
	private int donGia;
	private int soLuong;

	public SanPham(int ma, String ten, int donGia, int soLuong) {
		this.ma = ma;
		this.ten = ten;
		this.donGia = donGia;
		this.soLuong = soLuong;
	}

	public double tinhThanhTien() {
		return this.donGia * this.soLuong;
	}

	public void nhapHang(int soLuongNhap) {
		this.soLuong = this.soLuong + soLuongNhap;
	}

	public boolean banHang(int soLuongBan) {
		if (soLuongBan >= 0 || this.soLuong > 0 || this.soLuong - soLuongBan >= 0)
			return true;
		return false;
	}

	public void hienThiThongTin() {
		System.out.println("mã : " + this.ma);
		System.out.println("tên : " + this.ten);
		System.out.println("đơn giá : " + NumberFormat.getInstance(Locale.US).format(this.donGia));
		System.out.println("số lượng : " + this.soLuong);
		System.out.println("số thành tiền : " + this.tinhThanhTien());
		System.out.println("");
	}

	public static void main(String[] args) {
		SanPham sp1 = new SanPham(1, "iphone 17 pro max", 34_000_000, 10);
		SanPham sp2 = new SanPham(2, "iphone 15 pro max", 24_000_000, 30);
		sp1.hienThiThongTin();
		sp2.hienThiThongTin();

		sp1.nhapHang(5);
		sp1.hienThiThongTin();

		sp1.banHang(9);
		sp1.hienThiThongTin();

		sp1.banHang(10);
		sp1.hienThiThongTin();
	}
}
