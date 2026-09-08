package lab1_Upload_File;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CopyFile {

	public boolean copyFile(String source, String dest) throws FileNotFoundException, IOException {
		// file nguon
		File sourceFile = new File(source);
		// file dich
		File destFile = new File(dest);
		// kiem tra file nguon co ton tai khong
		if (sourceFile.exists()) {
			// luong doc file
			FileInputStream fis = new FileInputStream(sourceFile);
			// luong ghi file
			FileOutputStream fos = new FileOutputStream(destFile);

			byte[] arr = new byte[1024];
			int len;

			while ((len = fis.read(arr)) != -1) {
				fos.write(arr, 0, len);
				fos.flush();
			}
			fis.close();
			fos.close();
			System.out.println("Copy thanh cong");
			return true;
		} else {
			System.out.println("File nguon khong ton tai");
			return false;
		}
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		CopyFile copy = new CopyFile();
		copy.copyFile("D:\\HocJava\\a.txt", "D:\\HocJava\\b.txt");
	}
}
