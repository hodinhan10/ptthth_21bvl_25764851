package OOP;

abstract class NhanVien {
	protected String hoTen;
	protected String cccd;

	// constructor
	public NhanVien(String hoTen, String cccd) {
		this.hoTen = hoTen;
		this.cccd = cccd;
	}

	// functions
	public abstract double tinhLuong();
}
