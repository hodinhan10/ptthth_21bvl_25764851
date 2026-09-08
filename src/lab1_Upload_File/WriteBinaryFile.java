package lab1_Upload_File;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class WriteBinaryFile {

	public static void saveSV(String src, ArrayList<SinhVien> listSV) throws IOException {

		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(src)));
		// Ghi so luong sinh vien
		dos.writeInt(listSV.size());

		for (SinhVien sv : listSV) {
			// Ghi MSSV
			dos.writeUTF(sv.getMssv());
			// Ghi ten
			dos.writeUTF(sv.getTen());
			// Ghi tuoi
			dos.writeInt(sv.getTuoi());
			// Ghi so luong mon hoc
			dos.writeInt(sv.getListMH().size());

			for (MonHoc mh : sv.getListMH()) {
				dos.writeUTF(mh.getTenMonHoc());
				dos.writeInt(mh.getTinChi());
				dos.writeDouble(mh.getDiem());
			}
		}

		dos.flush();
		dos.close();

		System.out.println("Ghi file thanh cong");
	}

	public static void main(String[] args) throws IOException {

		MonHoc mh = new MonHoc("ltcb", 3, 6.7);
		MonHoc mh1 = new MonHoc("ltw", 3, 6.7);
		MonHoc mh2 = new MonHoc("tkhdt", 3, 6.7);

		ArrayList<MonHoc> listMH = new ArrayList<>();

		listMH.add(mh2);
		listMH.add(mh1);
		listMH.add(mh);

		ArrayList<SinhVien> listSV = new ArrayList<>();
		SinhVien sv = new SinhVien("11329078", "Nguyen Van A", 23, listMH);
		SinhVien sv1 = new SinhVien("11329079", "Nguyen Van B", 23, listMH);

		listSV.add(sv);
		listSV.add(sv1);
		saveSV("D:\\HocJava\\a.txt", listSV);
	}
}