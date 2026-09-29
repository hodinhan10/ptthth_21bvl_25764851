package lab1_Upload_File;

import java.io.File;

public class FindFile {

	public void finFile(String source, String key) {

		File file = new File(source);
		if (file.exists()) {
			// Neu la file
			if (file.isFile()) {
				// Kiem tra ten file ket thuc bang key
				if (file.getName().endsWith(key)) {
					System.out.println(file.getAbsolutePath());
				}
			}
			File[] listFile = file.listFiles();
			if (listFile != null) {
				for (File f : listFile) {
					finFile(f.getAbsolutePath(), key);
				}
			}

		} else {
			System.out.println("Source khong ton tai");
		}
	}

	public static void main(String[] args) {
		FindFile findFile = new FindFile();
		findFile.finFile("D:\\HocJava", ".txt");
	}
}