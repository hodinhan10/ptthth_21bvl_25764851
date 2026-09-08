package lab1_Upload_File;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Class_Images {

	// Ghi file anh
	public static void saveFile(File path, String tfile, byte[] bfile) {

		try {
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(bfile));
			ImageIO.write(img, tfile, path);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// Doc file anh
	public static byte[] readFile(File path) {
		// Kiem tra file co ton tai khong
		if (!path.exists()) {
			System.out.println("File khong ton tai");
			return null;
		}

		try {
			FileInputStream fis = new FileInputStream(path);
			byte[] buf = new byte[1024];
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			for (int readNum; (readNum = fis.read(buf)) != -1;) {
				bos.write(buf, 0, readNum);
			}
			fis.close();
			return bos.toByteArray();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		File source = new File("D:\\HocJava\\a.jpg");
		byte[] data = readFile(source);
		if (data != null) {
			File dest = new File("D:\\HocJava\\b.jpg");
			saveFile(dest, "jpg", data);
			System.out.println("Copy file anh thanh cong");
		}
	}
}