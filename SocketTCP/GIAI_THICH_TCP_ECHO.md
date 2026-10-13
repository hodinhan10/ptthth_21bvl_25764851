# Giải thích TCPEchoClient và TCPEchoServer cho người mới học

## 1. Hai file này làm gì?

- **Client (máy khách)** kết nối đến server rồi lần lượt gửi các ký tự `0` đến `9`.
- **Server (máy phục vụ)** nhận ký tự nào thì gửi trả lại ký tự đó; đây là **echo (phản hồi nguyên dữ liệu)**.
- **TCP** là cách truyền dữ liệu qua một kết nối, bảo đảm dữ liệu đến đúng thứ tự khi kết nối hoạt động bình thường.
- Hai chương trình có thể chạy trên cùng một máy, trong hai cửa sổ dòng lệnh riêng.

## 2. “Luồng” ở đây có hai nghĩa

- **Stream (luồng dữ liệu)**: dòng byte đi vào hoặc đi ra chương trình, giống nước chảy trong đường ống.
- **Thread (luồng thực thi)**: phần thực hiện các lệnh của chương trình, giống người lần lượt làm từng công việc.
- Hai file dùng `InputStream` và `OutputStream` để trao đổi dữ liệu; chúng không tạo thêm thread bằng `new Thread(...)`.
- `Thread.sleep(2000)` chỉ cho luồng thực thi hiện tại nghỉ 2 giây, không tạo luồng mới và không đóng kết nối.

## 3. Cấu trúc một file Java

| Thành phần | Ý nghĩa trong hai file |
| --- | --- |
| `package SocketTCP;` | Cho biết lớp thuộc nhóm `SocketTCP`. |
| `import ...;` | Cho phép dùng tên ngắn của những lớp có sẵn, như `Socket`. |
| `public class TCPEchoClient` | Khai báo lớp client; tên lớp công khai phải trùng tên file. |
| `public class TCPEchoServer` | Khai báo lớp server. |
| `serverTP`, `serverPort` | Lưu địa chỉ server và cổng kết nối. |
| `main(...)` | Nơi Java bắt đầu chạy chương trình. |
| `try / catch / finally` | Thử chạy, xử lý lỗi và dọn tài nguyên. |

- Dấu `{ }` bao quanh một khối lệnh, chẳng hạn phần thân lớp, phương thức hoặc vòng lặp.
- Dấu `;` kết thúc một câu lệnh; `//` mở đầu chú thích và không được thực thi.
- Khi chạy client, Java vào `main` của client; khi chạy server, Java vào `main` của server.

### Các từ trong khai báo

```java
public final static int serverPort = 7;
```

- Dòng này khai báo cổng server có giá trị `7`; hai chương trình dùng giá trị này khi mở hoặc kết nối đến cổng.
- `public`: có thể được truy cập từ lớp khác; `static`: thuộc về lớp, không cần tạo đối tượng để dùng.
- `final`: không được gán lại sau khi khởi tạo; `int`: kiểu số nguyên.
- `serverPort` là tên biến, `7` là giá trị ban đầu.

```java
public static void main(String[] args)
```

- Đây là phương thức bắt đầu chương trình; các lệnh bên trong chạy theo thứ tự từ trên xuống.
- `public static` cho phép Java gọi phương thức này mà không cần tạo đối tượng của lớp.
- `void` nghĩa là không trả về giá trị; `String[] args` chứa các tham số khi chạy từ dòng lệnh, nhưng hai file chưa dùng chúng.
- Client còn có `throws InterruptedException, IOException`: cho phép lỗi bị ngắt khi ngủ hoặc lỗi vào/ra chưa được xử lý truyền ra ngoài `main`.

## 4. Socket và hai luồng dữ liệu

- **Socket (cửa giao tiếp)** là một đầu của kết nối giữa client và server.
- `ServerSocket` là cửa chờ kết nối của server; `Socket` do `accept()` trả về phục vụ một client cụ thể.
- **Byte** là một đơn vị dữ liệu 8 bit; mỗi lần `read()` hoặc `write(...)` trong bài này nhận hoặc gửi một byte.

Khi gửi:

- Client dùng `os.write(i)` để gửi tới server.
- Server dùng `os.write(ch)` để gửi trả về client.

Khi nhận:

- Server dùng `is.read()` để đọc dữ liệu client gửi.
- Client dùng `is.read()` để đọc dữ liệu server trả về.

```text
Client                                  Server
OutputStream  ─── ký tự '0' ──────────> InputStream
InputStream   <── ký tự '0' ─────────── OutputStream
```

- `is` là tên viết tắt của `InputStream` (luồng vào); `os` là tên viết tắt của `OutputStream` (luồng ra).
- “Vào” và “ra” được tính từ chương trình đang chạy: dữ liệu ra khỏi client sẽ đi vào server.
- `getInputStream()` và `getOutputStream()` lấy hai luồng gắn với socket; chúng chưa đọc hoặc gửi dữ liệu.

## 5. Thứ tự chạy toàn bộ bài

1. Chạy server: mở cổng `7`, in `Server da duoc tao`, rồi đứng chờ tại `ss.accept()`.
2. Chạy client: kết nối đến `127.0.0.1:7`, rồi in `Client da duoc tao` nếu kết nối thành công.
3. Server nhận kết nối, in `Client da duoc ket noi`, rồi lấy hai luồng dữ liệu.
4. Client gửi ký tự `0`; server đọc, in `0` và gửi lại `0`.
5. Client đọc phản hồi, in `0`, nghỉ 2 giây rồi gửi ký tự tiếp theo.
6. Các bước gửi và nhận lặp lại đến ký tự `9`; client nghỉ thêm 2 giây rồi đóng socket.
7. Server đọc được `-1`, thoát vòng trong, đóng socket của client và tiếp tục chờ kết nối mới.

- Mỗi chương trình chạy tuần tự trong chính nó; thứ tự thông báo giữa hai cửa sổ có thể khác nhau tùy lúc máy xử lý.

## 6. Các dòng chính trong client

### Kết nối tới server

```java
s = new Socket(serverTP, serverPort);
```

- Dòng này tạo socket và kết nối tới server; `serverTP` là địa chỉ, `serverPort` là cổng đích.
- Trước đó `s` là `null` (chưa tham chiếu đến đối tượng); sau khi thành công, chương trình có thể lấy luồng để gửi và nhận.
- `127.0.0.1` nghĩa là chính máy đang chạy client; cổng giống số cửa để tìm đúng chương trình trên máy đó.

### Gửi các ký tự từ 0 đến 9

```java
for (int i = '0'; i <= '9'; i++)
```

- Vòng lặp chạy 10 lần; `i` giữ mã của ký tự hiện tại.
- `'0'` là ký tự có mã `48`, còn số `0` có giá trị bằng không; `'9'` có mã `57`.
- Ban đầu `i = 48`, sau mỗi vòng `i++` tăng thêm 1; khi `i = 58`, vòng lặp kết thúc.

```java
os.write(i);
int ch = is.read();
System.out.println((char)ch);
```

- `os.write(i)` gửi một byte; ví dụ `i = 48` thì gửi byte biểu diễn ký tự `0`.
- Sau khi gửi, `is.read()` chờ đọc một byte phản hồi và lưu kết quả vào biến số nguyên `ch`.
- `(char)ch` là **ép kiểu (đổi cách biểu diễn giá trị)** sang ký tự để in; nhận `48` thì in `0`, không in `48`.

### Nghỉ giữa các lần gửi

```java
Thread.sleep(2000);
```

- Dòng này cho luồng thực thi của client nghỉ khoảng 2 giây sau khi in phản hồi.
- `2000` tính bằng mili giây; hết thời gian nghỉ, chương trình đi tới bước tăng `i` của vòng lặp.
- Server vẫn chạy riêng và thường đứng chờ byte tiếp theo trong thời gian client nghỉ.

## 7. Các dòng chính trong server

### Mở cửa chờ kết nối

```java
ServerSocket ss = new ServerSocket(serverPort);
Socket s = ss.accept();
```

- Dòng đầu mở cổng `7` để chờ client; `ss` giữ cửa chờ kết nối.
- Dòng thứ hai chờ một client kết nối rồi trả về socket `s` dành cho client đó.
- Trước `accept()`, chưa có socket trao đổi với client; sau khi nó trả về, server lấy luồng vào và ra từ `s`.

### Nhận và trả lại một byte

```java
ch = is.read();
if(ch == -1) break;
System.out.println((char)ch);
os.write(ch);
```

- `is.read()` đọc một byte và trả về số `0..255`; nếu luồng kết thúc thì trả về `-1`.
- Chưa có dữ liệu mà luồng vẫn mở thì `read()` đứng chờ, không trả về `-1` chỉ vì đang thiếu dữ liệu.
- `if` kiểm tra điều kiện; `break` thoát vòng lặp gần nhất, tức vòng nhận dữ liệu bên trong.
- Nếu nhận được byte, server in ký tự rồi gửi lại byte đó; ví dụ nhận `48` thì in `0` và trả về `48`.

### Vì sao có hai vòng while?

| Vòng lặp | Nhiệm vụ | Khi nào rời vòng? |
| --- | --- | --- |
| `while (true)` bên ngoài | Chờ và phục vụ từng client. | Không có lệnh thoát trong code hiện tại; thường dừng bằng cách kết thúc chương trình. |
| `while (true)` bên trong | Nhận và trả dữ liệu của client đang kết nối. | `read()` trả về `-1` thì `break`; lỗi vào/ra cũng khiến chương trình rời khối để vào `catch`. |

- `true` là giá trị “đúng”, nên vòng lặp tiếp tục cho đến khi có lệnh thoát hoặc xảy ra lỗi làm rời khối.
- Server hiện tại xử lý từng client lần lượt; nó chưa quay lại `accept()` khi vẫn đang phục vụ client trước.

## 8. Bắt lỗi và đóng kết nối

- `try` bao quanh các lệnh có thể xảy ra lỗi; `catch (IOException e)` nhận **ngoại lệ (thông báo lỗi khi chạy)** liên quan đến vào/ra.
- Trong client, `finally` chạy khi rời `try/catch`, kể cả khi có lỗi; `if (s != null)` kiểm tra đã có socket trước khi gọi `close()`.
- `s.close()` đóng kết nối và hai luồng dữ liệu gắn với socket đó.
- Trong server, `s.close()` nằm sau vòng nhận; `ss` vẫn mở để tiếp tục nhận client khác.

Một vài giới hạn của code hiện tại:

- Client chưa kiểm tra `ch == -1` trước khi ép thành ký tự; nếu server đóng sớm, client có thể in ký tự không mong muốn.
- Nếu server lỗi trong lúc nhận hoặc gửi, lệnh `s.close()` phía sau có thể bị bỏ qua.
- Thông báo `Error: Can Not create socket` dùng chung cho cả lỗi kết nối và lỗi gửi/nhận, nên chưa chỉ rõ nguyên nhân.

## 9. Cách chạy và kết quả

- Mở hai cửa sổ dòng lệnh tại thư mục gốc `D:\IUH\FullSoureCodeJava\PT_HT_TH`.
- Cần bộ công cụ Java JDK để có lệnh `javac` (biên dịch mã nguồn thành file `.class`).

Biên dịch trong cửa sổ thứ nhất:

```powershell
javac -encoding UTF-8 SocketTCP/TCPEchoServer.java SocketTCP/TCPEchoClient.java
```

Chạy server trong cửa sổ thứ nhất:

```powershell
java SocketTCP.TCPEchoServer
```

Chạy client trong cửa sổ thứ hai:

```powershell
java SocketTCP.TCPEchoClient
```

- `SocketTCP.TCPEchoServer` gồm tên package và tên lớp; khi chạy không thêm đuôi `.java` hoặc `.class`.
- Client in `Client da duoc tao`, sau đó in `0` đến `9`, mỗi ký tự một dòng.
- Server in `Server da duoc tao`, rồi `Client da duoc ket noi`, sau đó cũng in `0` đến `9`.
- Client kết thúc sau khoảng 20 giây cộng thời gian kết nối và trao đổi; server tiếp tục chờ và có thể dừng bằng `Ctrl+C`.
- Nếu chạy client trước khi server mở cổng, client thường báo lỗi kết nối; nếu cổng `7` đã bị chiếm, server báo lỗi mở cổng.

## 10. Tự kiểm tra đã hiểu bài chưa

1. Client gửi byte bằng dòng nào? → `os.write(i)`.
2. Server gửi phản hồi bằng dòng nào? → `os.write(ch)`.
3. Dòng nào chờ một kết nối mới? → `ss.accept()`.
4. Dòng nào chờ dữ liệu trên kết nối đã có? → `is.read()`.
5. Vì sao `ch` là `int`? → Để chứa mã byte `0..255` và giá trị đặc biệt `-1`.
6. `break` có làm server kết thúc không? → Không; ở đây nó chỉ thoát vòng nhận dữ liệu của client hiện tại.
