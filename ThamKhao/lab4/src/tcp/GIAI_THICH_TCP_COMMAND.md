# Hiểu TCP: server thông thường và server nhiều client

- **Phần A:** học TcpCommandClient và TcpCommandServer trước.
- **Phần B:** học MultiClientTcpServer sau, dùng lại cách gửi/nhận ở phần A.

## Phần A. Server thông thường — phục vụ từng client lần lượt

### 1. Hai file thông thường làm gì?

| File | Vai trò |
| --- | --- |
| `TcpCommandClient.java` | Đọc lệnh bạn nhập, gửi đến server và in câu trả lời. |
| `TcpCommandServer.java` | Phục vụ từng client lần lượt; chứa cả cách xử lý các lệnh. |


- **Client (máy khách)** là bên gửi yêu cầu; **server (máy phục vụ)** là bên nhận và trả lời.
- **TCP** truyền dữ liệu qua một kết nối; trong bài này, mỗi yêu cầu và phản hồi là một dòng chữ.
- Khác bài Echo chỉ trả lại dữ liệu, bài này hiểu các lệnh `PING`, `TIME`, `UPPER ...` và `QUIT`.

### 2. Đọc cấu trúc file Java trước

- `package tcp;`: lớp thuộc nhóm `tcp`, tương ứng với thư mục chứa các file này.
- `import ...;`: cho phép dùng tên ngắn của lớp có sẵn, như `Socket` hoặc `BufferedReader`.
- `public class ...`: khai báo lớp; tên lớp công khai trùng với tên file `.java`.
- `main(String[] args)`: điểm bắt đầu khi chạy một chương trình; `args` chứa tham số từ dòng lệnh.
- Dấu `{ }` bao quanh khối lệnh; dấu `;` kết thúc câu lệnh; `//` mở đầu chú thích.

```java
private static final int PORT = 5000;
```

- Dòng này khai báo hằng số cổng `5000`; server dùng cổng đó để chờ kết nối.
- `private`: chỉ dùng trực tiếp trong lớp; `static`: thuộc lớp; `final`: không được gán lại.
- `int` là kiểu số nguyên; `PORT` là tên hằng số.

### 3. Chỗ dễ nhầm nhất: nhận vào và gửi ra

**Luôn lấy chương trình đang đọc làm mốc: `in` nhận vào mình, `out` gửi ra khỏi mình.**

| Biến | Trong client | Trong server |
| --- | --- | --- |
| `console` | Nhận chữ từ bàn phím. | Không có biến này. |
| `in` | Nhận câu trả lời từ server. | Nhận yêu cầu từ client. |
| `out` | Gửi yêu cầu đến server. | Gửi câu trả lời về client. |

```text
Bàn phím → console → request của client

Client                                     Server
out.println(request) ─── "PING" ─────────→ in.readLine()
in.readLine()        ←── "OK PONG" ─────── out.println(response)
```

- `in` ở hai chương trình là hai biến riêng; `out` cũng vậy, chúng không phải một biến dùng chung.
- Socket là cửa giao tiếp; mỗi bên có đường nhận và đường gửi trên kết nối đó.
- `socket.getInputStream()` lấy đường nhận dữ liệu; `socket.getOutputStream()` lấy đường gửi dữ liệu.
- Lấy đường chưa có nghĩa là đã đọc hay gửi: phải gọi `readLine()` hoặc `println(...)` mới thực hiện việc đó.

### 4. Vì sao có nhiều lớp bọc quanh socket?

- Socket truyền **byte (đơn vị dữ liệu 8 bit)**, nhưng người dùng nhập chữ và chương trình muốn đọc từng dòng.
- Các lớp bọc giúp chuyển giữa byte, ký tự và dòng chữ.

Khi nhận:

```java
BufferedReader in = new BufferedReader(
    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
```

- Dòng này tạo công cụ đọc từng dòng từ đường nhận của socket; nó chưa đọc dữ liệu.
- `getInputStream()` cung cấp byte; `InputStreamReader` đổi byte thành ký tự theo UTF-8.
- **UTF-8 (cách mã hóa chữ thành byte)** giúp hai bên hiểu cùng một nội dung, kể cả chữ tiếng Việt.
- `BufferedReader` có vùng đệm và phương thức `readLine()` để đọc một dòng chữ, bỏ dấu kết thúc dòng.

Khi gửi:

```java
PrintWriter out = new PrintWriter(
    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
```

- Dòng này tạo công cụ gửi chữ qua đường gửi của socket; nó chưa gửi dữ liệu.
- `OutputStreamWriter` đổi ký tự thành byte UTF-8; `PrintWriter` giúp gửi bằng `println(...)`.
- `true` bật **auto-flush (tự đẩy dữ liệu đang đệm ra)** khi gọi `println`, để bên nhận có thể đọc ngay.
- `println` thêm dấu xuống dòng; bên nhận dùng dấu đó để biết một dòng đã hoàn tất.

#### Riêng biến console

- `System.in` là đầu vào bàn phím thông thường, nên `console.readLine()` chờ bạn nhập và nhấn Enter.
- `socket.getInputStream()` là đầu vào từ bên kia của kết nối, nên `in.readLine()` chờ chữ bên kia gửi.
- Cả hai đều là đầu vào, nhưng nguồn dữ liệu khác nhau.

#### Đọc cách bọc lớp từ trong ra ngoài

- Những dòng này là **khai báo biến và tạo đối tượng**, có gọi hàm tạo của các lớp; chúng chưa đọc hoặc gửi một dòng dữ liệu.
- **Hàm tạo (constructor)** là phần được gọi khi dùng `new` để tạo đối tượng; **phương thức (method)** như `readLine()` là việc đối tượng có thể làm.

| Thành phần | Công việc | Cách nhớ |
| --- | --- | --- |
| `System.in` | Cung cấp byte từ đầu vào bàn phím thông thường. | Nguồn chữ bạn nhập. |
| `socket.getInputStream()` | Cung cấp byte nhận từ server. | Đường nhận vào client. |
| `InputStreamReader` | Đổi byte thành ký tự. | Người phiên dịch byte thành chữ. |
| `BufferedReader` | Đệm ký tự và cho phép đọc từng dòng. | Công cụ đọc cả dòng. |
| `socket.getOutputStream()` | Cung cấp đường gửi byte đến server. | Đường gửi ra khỏi client. |
| `OutputStreamWriter` | Đổi ký tự thành byte. | Người phiên dịch chữ thành byte. |
| `PrintWriter` | Cho phép ghi chữ bằng `print`, `println`. | Công cụ viết chữ để gửi. |

**Với console:**

```text
System.in → InputStreamReader (UTF-8) → BufferedReader → console.readLine()
  byte             ký tự                   dòng             "PING"
```

- Java lấy `System.in`, tạo `InputStreamReader` với nguồn đó, rồi tạo `BufferedReader` bọc bên ngoài.
- `console` là tên biến giữ bộ đọc ngoài cùng; khi nhập `PING` rồi Enter, `console.readLine()` trả về `"PING"`.

**Với in của client:**

```text
Server → socket.getInputStream() → InputStreamReader → BufferedReader → in.readLine()
                byte                   ký tự              dòng          "OK PONG"
```

- Cách bọc giống `console`, chỉ thay nguồn bàn phím bằng nguồn dữ liệu nhận từ socket.
- Nếu server chưa gửi đủ một dòng và chưa kết thúc luồng, `in.readLine()` có thể tiếp tục chờ.

**Với out của client:**

```text
out.println("PING") → PrintWriter → OutputStreamWriter (UTF-8) → socket → Server
                         chữ              byte
```

- Khi tạo đối tượng, Java lấy đường gửi trước, tạo `OutputStreamWriter`, rồi tạo `PrintWriter` bên ngoài.
- Khi sử dụng, chữ đi từ `out.println(...)` qua các lớp bọc rồi thành byte gửi tới server.
- `true` thuộc hàm tạo `PrintWriter`, bật tự đẩy dữ liệu khi gọi `println`; gọi `print` thì không tự đẩy chỉ vì có `true`.
- Dấu xuống dòng giúp `readLine()` bên server nhận biết hết một dòng; flush giúp dữ liệu đang đệm được đẩy xuống đường gửi.

**Phân biệt UTF-8 với true:**

- `StandardCharsets.UTF_8` quy định cách đổi giữa chữ và byte; hai bên cần dùng cách mã hóa phù hợp với nhau.
- `true` quy định tự đẩy dữ liệu khi dùng các phương thức như `println`; nó không chọn cách mã hóa.
- Đọc ký tự `ế` có thể cần nhiều byte UTF-8, nên một ký tự không nhất thiết bằng một byte.

### 5. Luồng chạy trong TcpCommandClient

1. Chọn địa chỉ và cổng từ `args`; nếu không truyền tham số thì dùng `localhost` và `5000`.
2. `new Socket(host, port)` kết nối đến server; `localhost` thường là chính máy đang chạy client.
3. Tạo `console` để đọc bàn phím, `in` để nhận phản hồi, `out` để gửi lệnh.
4. Chờ bạn nhập một dòng rồi lưu vào `request`.
5. Gửi `request`, sau đó chờ một dòng trả lời và lưu vào `response`.
6. In câu trả lời; nếu vừa gửi `QUIT` thì thoát, nếu chưa thì quay lại chờ nhập.
7. Khi rời khối `try`, tự đóng các tài nguyên đã mở.

#### Cú pháp chọn giá trị mặc định

```java
String host = args.length > 0 ? args[0] : "localhost";
```

- Có ít nhất một tham số thì lấy tham số đầu tiên; không có thì lấy `"localhost"`.
- `điều_kiện ? giá_trị_khi_đúng : giá_trị_khi_sai` là cách viết ngắn của việc chọn một trong hai giá trị.
- `args[0]` là phần tử đầu tiên vì vị trí trong mảng Java bắt đầu từ `0`.

#### Vừa đọc, vừa gán, vừa kiểm tra

```java
while ((request = console.readLine()) != null)
```

- Đọc một dòng → gán vào `request` → kiểm tra kết quả có khác `null` không.
- Nếu có dòng thì chạy thân vòng lặp; nếu đầu vào kết thúc thì `readLine()` trả về `null` và vòng lặp dừng.
- Dòng rỗng `""` vẫn là một dòng; nó khác `null` (không còn dòng để đọc).
- Trong server cũng có cú pháp này, nhưng đọc từ client bằng `in.readLine()`.

### 6. Luồng chạy trong TcpCommandServer

1. `new ServerSocket(PORT)` mở cổng `5000` để chờ kết nối.
2. Vòng ngoài gọi `server.accept()`; chưa có client thì đứng chờ tại đây.
3. Khi có kết nối, `accept()` trả về một `Socket` dành cho client đó.
4. `serve(socket)` phục vụ client: đọc lệnh, xử lý rồi trả lời, lặp lại nhiều lần trên cùng kết nối.
5. Khi gặp `QUIT` hoặc luồng nhận kết thúc, `serve` kết thúc và socket được đóng.
6. Server quay lại `accept()` để nhận client tiếp theo.

- `ServerSocket` giống cửa tiếp khách; `Socket` là đường trao đổi với một khách cụ thể.
- Server này chưa phục vụ client tiếp theo khi còn đang chờ lệnh của client hiện tại.
- Client thứ hai có thể đã kết nối nhưng phải chờ đến lượt được xử lý.

#### Ba phương thức có nhiệm vụ khác nhau

| Phương thức | Nhiệm vụ |
| --- | --- |
| `main(...)` | Mở cổng, nhận kết nối rồi gọi `serve`. |
| `serve(Socket socket)` | Nhận các dòng yêu cầu và gửi các dòng phản hồi. |
| `process(String request)` | Đọc nội dung lệnh và tạo chuỗi trả lời; không gửi dữ liệu qua mạng. |

```java
String response = process(request);
out.println(response);
```

- Dòng đầu tạo câu trả lời và lưu vào `response`; dòng sau mới gửi câu trả lời đi.
- `static` cho phép gọi phương thức thông qua lớp mà không cần tạo đối tượng của lớp đó.
- `serve` không có `public` hoặc `private`, nên các lớp cùng package `tcp` có thể gọi nó.
- `throws IOException` cho phép lỗi vào/ra truyền về nơi gọi `serve` để xử lý.

### 7. Các lệnh trong process

| Nhập từ client | Server trả về | Ý nghĩa |
| --- | --- | --- |
| `PING` | `OK PONG` | Kiểm tra server có phản hồi. |
| `TIME` | `OK ...` kèm ngày giờ | Lấy ngày giờ trên máy server. |
| `UPPER hello` | `OK HELLO` | Đổi phần chữ phía sau lệnh thành chữ hoa. |
| `QUIT` | `OK BYE` | Kết thúc phiên của client hiện tại. |
| `ABC` | `ERR UNKNOWN_COMMAND` | Không nhận biết được lệnh. |

- `OK` biểu thị xử lý thành công; `ERR` biểu thị lỗi theo quy ước của bài này.
- `trim()` bỏ khoảng trắng ở đầu và cuối; `equalsIgnoreCase(...)` so sánh không phân biệt chữ hoa và chữ thường.
- Ví dụ ` ping ` vẫn được nhận biết là `PING`.
- `LocalDateTime.now()` lấy ngày giờ hiện tại theo máy chạy server, không kèm thông tin múi giờ trong chuỗi trả về.

#### Riêng lệnh UPPER

```java
trimmed.regionMatches(true, 0, "UPPER ", 0, 6)
```

- Kiểm tra sáu ký tự đầu có khớp `"UPPER "` hay không; ký tự thứ sáu là dấu cách.
- `true` nghĩa là bỏ qua khác biệt chữ hoa/thường; hai số `0` chỉ vị trí bắt đầu so sánh của hai chuỗi.
- `6` là số ký tự cần so sánh; `upper hello` cũng hợp lệ.

```java
trimmed.substring(6).toUpperCase(Locale.ROOT)
```

- `substring(6)` lấy phần từ vị trí `6` đến cuối, tức phần sau `"UPPER "`.
- `toUpperCase(Locale.ROOT)` đổi thành chữ hoa theo quy tắc không phụ thuộc thiết lập ngôn ngữ mặc định của máy.
- Ví dụ `UPPER hello` → lấy `hello` → đổi thành `HELLO` → thêm `OK ` để trả về.
- Nhập mỗi `UPPER` hoặc `UPPER ` sẽ bị coi là lệnh lạ, vì `trim()` xóa khoảng trắng cuối và không còn tiền tố `"UPPER "`.

#### Khi nhập QUIT

- Server tạo `OK BYE`, gửi phản hồi rồi mới thoát vòng nhận của client đó.
- Client đọc và in `OK BYE` rồi mới thoát vòng nhập; server vẫn tiếp tục chạy để chờ client khác.

### 8. Tự đóng tài nguyên và xử lý lỗi

```java
try (Socket socket = server.accept()) {
    serve(socket);
}
```

- Đây là **try-with-resources (try tự đóng tài nguyên)**: khi rời khối, Java tự gọi đóng socket, kể cả khi có lỗi.
- `catch (IOException e)` bắt lỗi vào/ra; `e.getMessage()` lấy thông báo của lỗi.
- `finally` chạy khi rời khối `try/catch`; cách dùng trong server nhiều client được giải thích ở phần B.
- `System.out` in thông báo thông thường; `System.err` in thông báo lỗi.

Một điểm cần lưu ý trong client hiện tại:

- `Integer.parseInt(args[1])` đổi chuỗi cổng thành số nguyên, nhưng nằm trước khối `try`.
- Nếu truyền cổng là `abc`, `catch (NumberFormatException e)` bên dưới chưa bắt được lỗi đó; chú thích đã chỉ rõ điểm này, chưa thay đổi cách hoạt động.

### 9. Chạy thử server thông thường

- Mở hai cửa sổ dòng lệnh tại `D:\IUH\FullSoureCodeJava\PT_HT_TH\lab04-socket`.
- Cần JDK (bộ công cụ phát triển Java); `javac` biên dịch mã nguồn thành file `.class` trong thư mục `out`.

Biên dịch hai file:

```powershell
javac -encoding UTF-8 -d out src/tcp/TcpCommandServer.java src/tcp/TcpCommandClient.java
```

Cửa sổ thứ nhất chạy server:

```powershell
java -cp out tcp.TcpCommandServer
```

Cửa sổ thứ hai chạy client:

```powershell
java -cp out tcp.TcpCommandClient
```

- `-cp out` chỉ nơi tìm các lớp đã biên dịch; `tcp.TcpCommandClient` gồm tên package và tên lớp.
- Có thể dùng `java -cp out tcp.TcpCommandClient localhost 5000` để chỉ định địa chỉ và cổng.
- Nhập lần lượt `PING`, `TIME`, `UPPER hello`, `QUIT`, mỗi lệnh xong nhấn Enter.
- Nếu mở client thứ hai khi client thứ nhất chưa kết thúc, client thứ hai phải chờ được phục vụ; nhập `QUIT` ở client thứ nhất để thấy nó đến lượt.
- `QUIT` kết thúc phiên client; `Ctrl+C` tại cửa sổ server dừng server.

### 10. Tự kiểm tra phần thông thường

1. `console` và `in` khác nhau ở đâu? → `console` nhận từ bàn phím, `in` nhận từ server.
2. Tạo `BufferedReader` đã đọc dữ liệu chưa? → Chưa đọc một dòng; gọi `readLine()` mới đọc dòng.
3. `process` có gửi dữ liệu không? → Không; nó chỉ tạo chuỗi phản hồi.
4. Server gửi phản hồi ở đâu? → `out.println(response)` trong `serve`.
5. Vì sao có `true` trong `PrintWriter`? → Tự đẩy dữ liệu đang đệm khi gọi `println`.

## Phần B. Server nhiều client — MultiClientTcpServer

### 1. Khác với server thông thường ở đâu?

- **Thread (luồng thực thi)** là phần thực hiện lệnh; nó khác **stream (luồng dữ liệu)** dùng để gửi và nhận.
- Server nhiều client có luồng `main` nhận kết nối và các luồng làm việc phục vụ client.
- **Thread pool (nhóm luồng làm việc)** quản lý và tái sử dụng các luồng này.

```java
ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTS);
```

- Dòng này tạo nhóm có tối đa `20` luồng làm việc, theo giá trị `MAX_CLIENTS`.
- Mỗi tác vụ trong bài phục vụ một client cho đến khi client kết thúc hoặc xảy ra lỗi.
- Nếu cả 20 luồng đang bận, tác vụ phục vụ client tiếp theo chờ trong hàng đợi; code không từ chối ngay kết nối thứ 21.

```java
pool.submit(() -> {
    // Công việc phục vụ một client.
});
```

- `submit` giao công việc cho nhóm luồng; luồng `main` có thể quay lại `accept()` để chờ kết nối mới.
- `() -> { ... }` là **lambda (cách viết ngắn để mô tả công việc)**: ở đây không nhận tham số, phần trong `{ }` là việc cần thực hiện.
- Socket vừa nhận được dùng trong công việc đó để phục vụ đúng client.

```text
Luồng main: nhận client A → giao việc → nhận client B → giao việc → ...
Luồng 1:    serve(socket của A): nhận lệnh A và trả lời A
Luồng 2:    serve(socket của B): nhận lệnh B và trả lời B
```

- `TcpCommandServer.serve(socket)` dùng lại phương thức xử lý kết nối, không chạy `main` của `TcpCommandServer` và không mở thêm cổng.
- `getRemoteSocketAddress()` lấy địa chỉ IP và cổng phía client để ghi thông báo; `String.valueOf(...)` đổi thông tin đó thành chuỗi.
- `pool.shutdown()` ngừng nhận tác vụ mới, nhưng không ép các tác vụ đang chạy dừng ngay.

### 2. Thứ tự thực hiện cho một client

1. Luồng `main` gọi `server.accept()` để nhận kết nối.
2. `pool.submit(...)` đưa công việc phục vụ socket đó vào nhóm luồng.
3. Luồng `main` quay lại chờ kết nối khác; một luồng làm việc nhận công việc khi có chỗ trống.
4. Luồng làm việc ghi `Connected`, gọi `TcpCommandServer.serve(socket)` để đọc lệnh và trả lời.
5. Khi phiên kết thúc hoặc có lỗi, `try (socket)` tự đóng socket và `finally` ghi `Disconnected`.

- `try (socket)` dùng biến socket đã có; không tạo thêm một kết nối.
- `Connected` được in khi tác vụ bắt đầu chạy, nên client đang chờ trong hàng đợi chưa có thông báo này ngay.
- Nếu việc mở server hoặc nhận kết nối gặp lỗi vào/ra, chương trình đi vào `catch` bên ngoài rồi `finally` gọi `pool.shutdown()`.

### 3. So sánh hai cách phục vụ

| Tình huống | Server thông thường | Server nhiều client |
| --- | --- | --- |
| Client A đang kết nối nhưng chưa nhập lệnh. | Luồng main chờ A trong `serve`. | Luồng làm việc của A chờ; main vẫn nhận kết nối. |
| Client B gửi `PING` trong lúc A vẫn kết nối. | B chờ A kết thúc phiên rồi mới được phục vụ. | B có thể nhận `OK PONG` khi có luồng làm việc trống. |
| Nơi gọi `serve(socket)`. | Trên luồng main. | Trên luồng làm việc của pool. |
| Cách nhận lệnh và trả lời. | Dùng `serve` và `process`. | Dùng lại chính `serve` và `process` đó. |

- Giống quầy phục vụ: server thông thường có một người phục vụ, còn server nhiều client có tối đa 20 người làm việc đồng thời.
- Client đang được phục vụ nhưng không gửi lệnh vẫn chiếm một luồng đang chờ đọc trong bài này.

### 4. Chạy thử server nhiều client

- Dừng server thông thường bằng `Ctrl+C` trước, vì hai server đều dùng cổng `5000`.
- Vẫn dùng `TcpCommandClient` như phần A; không cần một client riêng cho server nhiều client.

Biên dịch cả ba file tại thư mục `lab04-socket`:

```powershell
javac -encoding UTF-8 -d out src/tcp/TcpCommandServer.java src/tcp/MultiClientTcpServer.java src/tcp/TcpCommandClient.java
```

Chạy server:

```powershell
java -cp out tcp.MultiClientTcpServer
```

Mở hai cửa sổ khác nhau, mỗi cửa sổ chạy:

```powershell
java -cp out tcp.TcpCommandClient
```

1. Giữ client A đang kết nối, chưa nhập lệnh.
2. Nhập `PING` tại client B; B vẫn nhận `OK PONG` khi A chưa kết thúc.
3. Nhập `UPPER hello` tại A; A nhận `OK HELLO`.
4. Nhập `QUIT` tại từng client để kết thúc các phiên; dùng `Ctrl+C` để dừng server.

### 5. Tự kiểm tra phần nhiều client

1. Luồng main làm gì? → Chờ và nhận kết nối, rồi giao việc cho pool.
2. Luồng làm việc làm gì? → Gọi `serve(socket)` để nhận lệnh và trả lời một client.
3. Có phải chạy cả hai chương trình server cùng lúc không? → Không; chạy `MultiClientTcpServer` là đủ, nó gọi phương thức của lớp kia.
4. Có phải từ chối ngay client thứ 21 không? → Không; khi 20 luồng bận, tác vụ tiếp theo chờ trong hàng đợi.
5. `shutdown()` có dừng ngay mọi client không? → Không; tác vụ đã nhận vẫn được phép chạy tiếp.
