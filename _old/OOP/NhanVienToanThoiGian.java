package OOP;

class NhanVienToanThoiGian extends NhanVien {
	public double luongCoBan;
	public double heSoLuong;

	public NhanVienToanThoiGian(String hoTen, String cccd, double luongCoBan, double heSoLuong) {
		super(hoTen, cccd);
		this.luongCoBan = luongCoBan;
		this.heSoLuong = heSoLuong;
	}

	@Override
	public double tinhLuong() {
		return luongCoBan * heSoLuong;
	}
}