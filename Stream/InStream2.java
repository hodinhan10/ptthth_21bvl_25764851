package Stream;

import static java.lang.Thread.sleep;
import java.io.IOException;
import java.io.InputStream;

public class InStream2 {
    public static void main(String[] args) throws InterruptedException, IOException {
        InputStream is = System.in;
        while (true) {
            try {
                if (is.available() > 0) {
                    byte[] buffer = new byte[is.available()];
                    int bytesRead = is.read(buffer);
                    if (bytesRead == -1) break;
                    String str = new String(buffer, 0, bytesRead);
                    System.out.print(str);
                } else {
                    System.out.print("-");
                    sleep(1000);
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
