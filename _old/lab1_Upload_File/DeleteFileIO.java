package lab1_Upload_File;

import java.io.File;

public class DeleteFileIO {

	private void deleteFile(String source) {

		// new file
		File file = new File(source);

		// check file exist
		// neu ton tai
		if (file.exists()) {

			System.out.println("File ton tai");

			file.delete();

			System.out.println("Xoa file thanh cong");

		} else {

			System.out.println("File khong ton tai");
		}
	}

	public static void main(String[] args) {

		DeleteFileIO deleteFileIO = new DeleteFileIO();

		deleteFileIO.deleteFile("D:\\HocJava\\demo.txt");
	}

}
