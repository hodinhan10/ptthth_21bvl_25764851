package Lab2;

public class SinhVien extends Nguoi {

	private String maSinhVien;
	private String nganhHoc;
	private double diemTrungBinh;

	public SinhVien(String hoTen, int namSinh, String diaChi, String maSinhVien, String nganhHoc,
			double diemTrungBinh) {
		super(hoTen, namSinh, diaChi);
		this.maSinhVien = maSinhVien;
		this.nganhHoc = nganhHoc;
		this.diemTrungBinh = diemTrungBinh;
	}

	@Override
	public void hienThiThongTin() {
		super.hienThiThongTin();
		System.out.println("Ma sinh vien: " + getMaSinhVien());
		System.out.println("Nganh Hoc: " + getNganhHoc());
		System.out.println("Diem trung binh: " + getDiemTrungBinh());
		System.out.println(" ");
	}

	public String xepLoai() {
		if (diemTrungBinh >= 8.5)
			return "Giỏi";
		if (diemTrungBinh >= 7)
			return "Khá";
		if (diemTrungBinh >= 5)
			return "Trung bình";
		return "Yếu";
	}

	public String getMaSinhVien() {
		return maSinhVien;
	}

	public void setMaSinhVien(String maSinhVien) {
		this.maSinhVien = maSinhVien;
	}

	public String getNganhHoc() {
		return nganhHoc;
	}

	public void setNganhHoc(String nganhHoc) {
		this.nganhHoc = nganhHoc;
	}

	public double getDiemTrungBinh() {
		return diemTrungBinh;
	}

	public void setDiemTrungBinh(double diemTrungBinh) {
		this.diemTrungBinh = diemTrungBinh;
	}

}
