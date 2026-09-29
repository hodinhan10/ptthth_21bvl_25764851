package lab1_Upload_File;

import java.io.File;

public class DeleteFolderTH2 {
//	Delete thư mục chứa File
//	Test
//	├── a.txt
//	├── b.txt
//	└── c.txt
//	Xoa het file Test

	public boolean deleteListFileInfolder(String source) {
		File folder = new File(source);
		// folder ton tai
		if (folder.exists()) {
			// danh sach file
			File[] listFile = folder.listFiles();
			if (listFile != null && listFile.length != 0) {
				for (File f : listFile) {
					// file thi xoa
					if (f.isFile()) {
						f.delete();
					}
				}
			}
			if (folder.delete()) {
				System.out.println("Delete folder thanh cong!");
				return true;
			}
			System.out.println("Khong the delete folder!");
			return false;
		} else {
			System.out.println("Folder khong ton tai");
			return false;
		}
	}

	public static void main(String[] args) {
		DeleteFolderTH2 deleteFolder = new DeleteFolderTH2();
		deleteFolder.deleteListFileInfolder("D:\\HocJava\\Test");
	}
}