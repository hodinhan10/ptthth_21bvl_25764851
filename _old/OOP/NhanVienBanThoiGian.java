package OOP;

class NhanVienBanThoiGian extends NhanVien {
	public double luongNgay;
	public double soNgay;

	public NhanVienBanThoiGian(String hoTen, String cccd, double luongNgay, double soNgay) {
		super(hoTen, cccd);
		this.luongNgay = luongNgay;
		this.soNgay = soNgay;
	}

	@Override
	public double tinhLuong() {
		return luongNgay * soNgay;
	}
}