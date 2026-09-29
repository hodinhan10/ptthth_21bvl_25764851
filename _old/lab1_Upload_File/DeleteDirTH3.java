package lab1_Upload_File;

import java.io.File;
import java.io.IOException;

public class DeleteDirTH3 {

	public boolean deleteListFileInfolder(String source) throws IOException {

		File folder = new File(source);
		// folder ton tai
		if (folder.exists()) {
			// danh sach file
			File[] listFile = folder.listFiles();
			if (listFile != null && listFile.length != 0) {
				for (File f : listFile) {
					// neu la file thi delete
					if (f.isFile()) {
						f.delete();
					}
					// neu la thu muc thi goi de quy lai
					if (f.isDirectory()) {
						deleteListFileInfolder(f.getAbsolutePath());
					}
				}
			}
			if (folder.delete()) {
				System.out.println("Delete folder thanh cong: " + folder.getAbsolutePath());
				return true;
			}
			return false;
		} else {
			System.out.println("Folder khong ton tai");
			return false;
		}
	}

	public static void main(String[] args) throws IOException {
		DeleteDirTH3 deleteDirTH3 = new DeleteDirTH3();
		deleteDirTH3.deleteListFileInfolder("D:\\HocJava\\TestDeleteDir");
	}
}
