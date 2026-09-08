package Lab2;

import java.text.NumberFormat;
import java.util.Locale;

public class GiangVien extends Nguoi {
	private String maGiangVien;
	private String chuyenMon;
	private double luongCoBan;
	private double heSoLuong;

	public GiangVien(String hoTen, int namSinh, String diaChi, String maGiangVien, String chuyenMon, double luongCoBan,
			double heSoLuong) {
		super(hoTen, namSinh, diaChi);
		this.maGiangVien = maGiangVien;
		this.chuyenMon = chuyenMon;
		this.luongCoBan = luongCoBan;
		this.heSoLuong = heSoLuong;
	}

	public String tinhLuong() {
		return NumberFormat.getInstance(Locale.US).format(luongCoBan * heSoLuong);
	}

	public void hienThiThongTin() {
		super.hienThiThongTin();
		System.out.println("Ma Giang Vien: " + getMaGiangVien());
		System.out.println("Chuyen mon: " + getChuyenMon());
		System.out.println("Luong co ban: " + getLuongCoBan());
		System.out.println("He so luong: " + getHeSoLuong());
		System.out.println("Tinh luong: " + tinhLuong());
		System.out.println("");
	}

	public String getMaGiangVien() {
		return maGiangVien;
	}

	public void setMaGiangVien(String maGiangVien) {
		this.maGiangVien = maGiangVien;
	}

	public String getChuyenMon() {
		return chuyenMon;
	}

	public void setChuyenMon(String chuyenMon) {
		this.chuyenMon = chuyenMon;
	}

	public double getLuongCoBan() {
		return luongCoBan;
	}

	public void setLuongCoBan(double luongCoBan) {
		this.luongCoBan = luongCoBan;
	}

	public double getHeSoLuong() {
		return heSoLuong;
	}

	public void setHeSoLuong(double heSoLuong) {
		this.heSoLuong = heSoLuong;
	}

	public static void main(String[] args) {
		GiangVien gv1 = new GiangVien("Nguyen Van A", 1980, "123 Le Loi, Q1, TP.HCM", "GV001", "Cong Nghe Thong Tin",
				8000000, 2.5);
		gv1.hienThiThongTin();

		GiangVien gv2 = new GiangVien("Tran Thi B", 1985, "456 Nguyen Trai, Q5, TP.HCM", "GV002", "Ky Thuat Phan Mem",
				7500000, 3.0);
		gv2.hienThiThongTin();

		SinhVien sv1 = new SinhVien("Le Van C", 2003, "789 Tran Hung Dao, Q1, TP.HCM", "SV001", "Cong Nghe Thong Tin",
				8.5);
		sv1.xepLoai();
		sv1.hienThiThongTin();

		SinhVien sv2 = new SinhVien("Pham Thi D", 2004, "321 Vo Van Tan, Q3, TP.HCM", "SV002", "Ky Thuat Phan Mem",
				7.8);
		sv2.xepLoai();
		sv2.hienThiThongTin();
	}
}
