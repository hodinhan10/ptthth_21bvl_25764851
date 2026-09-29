package lab1_Upload_File;

import java.io.File;

public class DeleteEmptyFolder {
//	Delete thư mục rỗng
	public boolean deleteEmptyFolder(String source) {

		File folder = new File(source);

		// kiem tra neu folder ton tai thi xoa
		if (folder.exists()) {

			if (folder.delete()) {

				System.out.println("Folder ton tai");
				System.out.println("Xoa folder thanh cong");

				return true;
			}

			System.out.println("Khong the xoa folder");
		} else {

			System.out.println("Folder khong ton tai");
		}

		return false;
	}

	public static void main(String[] args) {

		DeleteEmptyFolder deleteFolder = new DeleteEmptyFolder();

		deleteFolder.deleteEmptyFolder("D:\\HocJava\\Test");
	}
}
