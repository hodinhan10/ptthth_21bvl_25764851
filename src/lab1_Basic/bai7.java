package lab1_Basic;

import java.io.File;

public class bai7 {
	private void deleteFile(String source) {
		File file = new File(source);
		if (file.exists()) {
			System.out.println("file ton tai");
			file.delete();
		} else {
			System.out.println("file khong tai");
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		bai7 deleteFileIO = new bai7();
		deleteFileIO.deleteFile("D:/Hocjava/demo.txt");
	}

}
