# Hiểu UdpEchoClient và UdpEchoServer

## 1. Bài này làm gì?

- Client gửi một lời nhắn UDP tới server.
- Server nhận lời nhắn, đổi thành chữ hoa, thêm `ACK ` rồi gửi lại.
- Ví dụ client gửi `xin chào UDP`, server trả về `ACK XIN CHÀO UDP`.

```text
Client ── "xin chào UDP" ──> Server
Client <─ "ACK XIN CHÀO UDP" ─ Server
```

## 2. Socket và packet khác nhau thế nào?

- `DatagramSocket` là **cửa giao tiếp**, dùng để gửi và nhận dữ liệu UDP.
- `DatagramPacket` là **gói hàng**, chứa dữ liệu và thông tin nơi gửi hoặc nơi nhận.

Ví dụ gần gũi:

```text
DatagramSocket = bưu điện
DatagramPacket = bưu kiện
byte[]         = đồ nằm trong bưu kiện
IP             = địa chỉ ngôi nhà
port           = số phòng hoặc người nhận trong ngôi nhà
```

Một socket có thể gửi và nhận nhiều packet. Packet không phải là đường kết nối.

## 3. UDP khác TCP trong các bài trước

| TCP | UDP |
| --- | --- |
| Tạo kết nối bằng `Socket`. | Không tạo kết nối riêng với từng client. |
| Dữ liệu đi như một luồng liên tục. | Mỗi lần gửi là một gói riêng. |
| Dùng `InputStream`, `OutputStream`. | Dùng `DatagramPacket`. |
| Bảo đảm thứ tự và việc truyền khi kết nối hoạt động bình thường. | Gói có thể mất, trùng hoặc đến không đúng thứ tự. |

- Vì UDP không bảo đảm có phản hồi, client đặt thời gian chờ là 3 giây.
- Bài này gửi một gói rồi chờ một gói trả lời, nên luồng chạy vẫn dễ quan sát.

## Phần A. UdpEchoClient

### 4. Chọn địa chỉ, cổng và lời nhắn

```java
String host = args.length > 0 ? args[0] : "localhost";
int port = args.length > 1 ? Integer.parseInt(args[1]) : 5001;
String message = args.length > 2 ? args[2] : "xin chào UDP";
```

- Có tham số dòng lệnh thì chương trình dùng tham số đó.
- Không có thì dùng `localhost`, cổng `5001` và lời nhắn mặc định.
- `localhost` nghĩa là chính máy đang chạy client.

```java
byte[] data = message.getBytes(StandardCharsets.UTF_8);
```

- `message` là chuỗi ký tự nhưng UDP gửi byte.
- `getBytes(UTF_8)` đổi chuỗi thành mảng byte theo bảng mã UTF-8.

### 5. Tìm địa chỉ IP của server

```java
InetAddress server = InetAddress.getByName(host);
```

- Dòng này đổi tên máy trong `host` thành địa chỉ IP.
- Ví dụ `localhost` thường được đổi thành địa chỉ máy cục bộ như `127.0.0.1`.
- `server` chỉ giữ địa chỉ IP; nó không phải server và chưa gửi dữ liệu.

### 6. Tạo socket của client

```java
DatagramSocket socket = new DatagramSocket();
```

- Dòng này tạo cửa giao tiếp UDP cho client.
- Client không chỉ định cổng, nên hệ điều hành tự chọn một cổng còn trống.
- Cổng tự chọn này giúp server biết phải gửi phản hồi về đâu.

### 7. Tạo và gửi packet

```java
DatagramPacket request = new DatagramPacket(
        data, data.length, server, port);
```

Gói dùng để gửi có 4 tham số:

1. `data`: mảng byte cần gửi.
2. `data.length`: số byte cần lấy từ mảng.
3. `server`: địa chỉ IP của máy nhận.
4. `port`: cổng của chương trình nhận trên máy đó.

```java
socket.send(request);
```

- Dòng này mới thực sự gửi packet.
- Socket đọc địa chỉ IP và cổng đích được ghi trong `request`.

### 8. Chuẩn bị nhận phản hồi

```java
byte[] buffer = new byte[4096];
DatagramPacket response = new DatagramPacket(buffer, buffer.length);
```

- `buffer` là vùng nhớ trống, giống hộp có sức chứa tối đa 4096 byte.
- `response` là gói dùng để nhận, nên lúc tạo chỉ cần buffer và kích thước tối đa.
- Sau `receive`, Java điền dữ liệu, IP và cổng của bên gửi vào `response`.

Đây là điểm dễ nhầm:

| Gói gửi | Gói nhận |
| --- | --- |
| Cần dữ liệu. | Cần vùng nhớ trống. |
| Cần số byte gửi. | Cần sức chứa tối đa. |
| Cần IP và cổng đích. | Chưa cần IP và cổng; Java điền thông tin bên gửi sau khi nhận. |

### 9. Chờ phản hồi và xử lý hết giờ

```java
socket.setSoTimeout(3000);
socket.receive(response);
```

- `setSoTimeout(3000)` giới hạn thời gian chờ nhận là 3000 mili giây, tức 3 giây.
- `receive(response)` đứng chờ và đặt gói nhận được vào `response`.
- Nếu hết 3 giây, Java tạo `SocketTimeoutException` và chương trình in thông báo hết giờ.

### 10. Đổi byte phản hồi thành chuỗi

```java
String text = new String(
        response.getData(),
        response.getOffset(),
        response.getLength(),
        StandardCharsets.UTF_8);
```

`new String` có 4 tham số:

1. `getData()`: lấy mảng byte chứa dữ liệu.
2. `getOffset()`: vị trí bắt đầu dữ liệu thật trong mảng.
3. `getLength()`: số byte thật sự nhận được.
4. `UTF_8`: cách đổi các byte thành ký tự.

- Buffer có thể chứa 4096 byte, nhưng phản hồi có thể chỉ dài 20 byte.
- Vì vậy phải dùng `getLength()`, không đổi toàn bộ buffer thành chuỗi.

## Phần B. UdpEchoServer

### 11. Mở cổng server

```java
DatagramSocket socket = new DatagramSocket(PORT);
```

- Server mở cửa UDP tại cổng `5001`.
- Client phải gửi packet tới đúng cổng này.
- UDP không dùng `ServerSocket` và không gọi `accept()` như TCP.

### 12. Nhận request

```java
DatagramPacket request = new DatagramPacket(buffer, buffer.length);
socket.receive(request);
```

- Server tạo một packet trống để chứa gói sắp nhận.
- Nếu chưa có gói, chương trình đứng chờ tại `receive`.
- Khi nhận xong, `request` chứa dữ liệu cùng IP và cổng của client gửi gói.

### 13. Tạo nội dung phản hồi

```java
String message = new String(
        request.getData(),
        request.getOffset(),
        request.getLength(),
        StandardCharsets.UTF_8);

String text = "ACK " + message.toUpperCase(Locale.ROOT);
```

- Dòng đầu đổi đúng số byte nhận được thành chuỗi.
- `toUpperCase` đổi lời nhắn thành chữ hoa.
- `Locale.ROOT` dùng quy tắc đổi chữ ổn định, không phụ thuộc ngôn ngữ mặc định của máy.

### 14. Trả lời đúng client

```java
DatagramPacket response = new DatagramPacket(
        responseData,
        responseData.length,
        request.getAddress(),
        request.getPort());
```

Gói phản hồi có 4 tham số:

1. `responseData`: mảng byte phản hồi.
2. `responseData.length`: số byte cần gửi.
3. `request.getAddress()`: IP của client vừa gửi yêu cầu.
4. `request.getPort()`: cổng UDP của client vừa gửi yêu cầu.

- Server lấy địa chỉ và cổng từ packet nhận để trả đúng người gửi.
- Đây giống việc xem địa chỉ người gửi trên bưu kiện rồi gửi thư trả lời về địa chỉ đó.

```java
socket.send(response);
```

- Dòng này gửi phản hồi.
- Sau đó server quay lại đầu vòng `while` và chờ packet tiếp theo.

## 15. Toàn bộ thứ tự chạy

1. Server mở socket UDP tại cổng `5001` và chờ ở `receive`.
2. Client đổi lời nhắn thành byte và tạo packet có IP cùng cổng server.
3. Client gửi packet rồi chờ phản hồi tối đa 3 giây.
4. Server nhận packet; packet này chứa dữ liệu, IP và cổng client.
5. Server đổi dữ liệu thành chữ, viết hoa và thêm `ACK `.
6. Server tạo packet phản hồi với IP và cổng lấy từ packet yêu cầu.
7. Client nhận phản hồi, đổi đúng phần byte nhận được thành chuỗi rồi in ra.

```text
CLIENT                                          SERVER

message
  ↓ getBytes(UTF-8)
byte[] data
  ↓ tạo packet có IP/cổng server
socket.send(request) ─────────────────────────> socket.receive(request)
                                                    ↓
                                                xử lý chuỗi
                                                    ↓
socket.receive(response) <──────────────────── socket.send(response)
  ↓
new String(..., UTF-8)
  ↓
in kết quả
```

## 16. Cách chạy thử

Mở hai cửa sổ PowerShell tại thư mục:

```text
D:\IUH\FullSoureCodeJava\PT_HT_TH
```

Biên dịch:

```powershell
javac -encoding UTF-8 -d out ThamKhao/lab4/src/udp/UdpEchoServer.java ThamKhao/lab4/src/udp/UdpEchoClient.java
```

Chạy server trước:

```powershell
java -cp out ThamKhao.lab4.src.udp.UdpEchoServer
```

Chạy client ở cửa sổ thứ hai:

```powershell
java -cp out ThamKhao.lab4.src.udp.UdpEchoClient
```

Kết quả phía client:

```text
Server: ACK XIN CHÀO UDP
```

Có thể truyền địa chỉ, cổng và lời nhắn:

```powershell
java -cp out ThamKhao.lab4.src.udp.UdpEchoClient localhost 5001 "hoc UDP"
```

- Chỉ chạy client một lần rồi client kết thúc.
- Server tiếp tục chờ packet mới; dùng `Ctrl+C` tại cửa sổ server để dừng.
- Nếu chưa chạy server, client chờ 3 giây rồi báo chưa nhận được phản hồi.

## 17. Tự kiểm tra

1. `DatagramSocket` là gì? → Cửa dùng để gửi và nhận UDP.
2. `DatagramPacket` là gì? → Gói chứa dữ liệu và thông tin địa chỉ.
3. Vì sao packet gửi cần IP/cổng? → Để biết gửi tới máy và chương trình nào.
4. Vì sao packet nhận chỉ cần buffer? → Chưa biết ai gửi; Java điền IP/cổng sau khi nhận.
5. Vì sao dùng `getLength()`? → Chỉ đổi số byte thật sự nhận được thành chuỗi.
6. Server trả lời đúng client bằng cách nào? → Lấy IP và cổng từ packet yêu cầu.
7. Vì sao client cần timeout? → UDP có thể không nhận được phản hồi.
