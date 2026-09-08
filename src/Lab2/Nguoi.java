package Lab2;

import java.util.Calendar;

public class Nguoi {
	private String hoTen;
	private int namSinh;
	private String diaChi;

	public Nguoi(String hoTen, int namSinh, String diaChi) {
		this.hoTen = hoTen;
		this.namSinh = namSinh;
		this.diaChi = diaChi;
	}

	public void hienThiThongTin() {
		System.out.println("Ho ten: " + getHoTen());
		System.out.println("Nam sinh: " + getNamSinh());
		System.out.println("Dia chi : " + getDiaChi());
		System.out.println("Tuoi hien tai: " + tinhTuoi());
	}

	public int tinhTuoi() {
		int namHienTai = Calendar.getInstance().get(Calendar.YEAR);
		return namHienTai - namSinh;
	}

	public String getHoTen() {
		return hoTen;
	}

	public void setHoTen(String hoTen) {
		this.hoTen = hoTen;
	}

	public int getNamSinh() {
		return namSinh;
	}

	public void setNamSinh(int namSinh) {
		this.namSinh = namSinh;
	}

	public String getDiaChi() {
		return diaChi;
	}

	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}

}
