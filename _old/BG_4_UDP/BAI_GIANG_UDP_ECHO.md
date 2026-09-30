# Bài giảng ngắn: UDP Echo trong Java

Code thực hành:

- [`UdpEchoServer.java`](./UdpEchoServer.java): nhận dữ liệu và gửi lại.
- [`UdpEchoClient.java`](./UdpEchoClient.java): gửi 10 thông điệp và nhận phản hồi.

## 1. Bản chất của UDP

UDP gửi **từng gói tin độc lập** (datagram). Mỗi gói giống một bưu kiện, gồm:

```text
[địa chỉ IP] [cổng] [độ dài] [dữ liệu byte]
```

- **IP** xác định máy nhận.
- **Port** xác định chương trình nhận trên máy đó.
- UDP **không tạo kết nối** trước khi gửi.
- UDP không bảo đảm gói tin sẽ đến, đến đúng thứ tự hay chỉ đến một lần.
- Vì ít thủ tục, UDP nhanh và phù hợp với game, thoại/video trực tuyến, DNS...

Trong bài này, client gửi một gói và chờ server gửi lại chính nội dung đó. Hành
động gửi trả nguyên dữ liệu được gọi là **echo**.

```text
Client (cổng tự chọn)                 Server (cổng 6789)
        |------ "data | 0" ----------------->|
        |<----- "data | 0" ------------------|
        |------ "data | 1" ----------------->|
        |<----- "data | 1" ------------------|
```

> UDP không có kết nối, nhưng vẫn cần `DatagramSocket`: socket là “cửa gửi/nhận”,
> còn `DatagramPacket` là “gói hàng” đi qua cửa đó.

## 2. Hai lớp quan trọng

| Lớp | Bản chất | Vai trò trong bài |
|---|---|---|
| `DatagramSocket` | Điểm gửi/nhận UDP | Server mở cổng `6789`; client dùng một cổng trống |
| `DatagramPacket` | Một gói gồm dữ liệu và thông tin nơi gửi/nhận | Chứa thông điệp `data \| n` |

### Tạo socket

```java
// Server phải có cổng cố định để client biết nơi gửi đến.
DatagramSocket serverSocket = new DatagramSocket(6789);

// Client không truyền cổng: hệ điều hành tự chọn một cổng còn trống.
DatagramSocket clientSocket = new DatagramSocket();
```

Hai chương trình trên cùng máy có thể dùng socket khác nhau, nhưng không thể cùng
chiếm cổng `6789` tại cùng thời điểm.

### Tạo gói để gửi

```java
DatagramPacket sendPacket = new DatagramPacket(
        data,        // mảng byte cần gửi
        data.length, // số byte được gửi
        serverIp,    // IP máy nhận
        6789         // cổng chương trình nhận
);
socket.send(sendPacket);
```

Gói gửi phải có **dữ liệu + IP đích + cổng đích**.

### Tạo gói để nhận

```java
byte[] buffer = new byte[1024];
DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
socket.receive(receivePacket);
```

Gói nhận chỉ cần một vùng nhớ trống. Khi gói đến, Java điền dữ liệu, IP nguồn và
cổng nguồn vào `receivePacket`. `receive()` là lệnh **blocking**: chương trình
dừng tại đó cho đến khi nhận được gói.

## 3. Vì sao phải đổi `String` và `byte[]`?

Mạng truyền byte, không truyền trực tiếp đối tượng `String`.

```java
// String -> byte[] trước khi gửi
byte[] data = message.getBytes(StandardCharsets.UTF_8);

// byte[] -> String sau khi nhận
String message = new String(
        packet.getData(),
        packet.getOffset(),
        packet.getLength(),
        StandardCharsets.UTF_8
);
```

Ý nghĩa cú pháp:

- `getData()`: lấy toàn bộ mảng đệm.
- `getOffset()`: vị trí byte đầu tiên của dữ liệu trong mảng.
- `getLength()`: số byte thật sự đã nhận.
- `UTF_8`: quy tắc đổi giữa ký tự và byte; hai bên phải dùng cùng quy tắc.

Không nên viết `new String(packet.getData())`, vì buffer có 1024 byte nhưng dữ
liệu thật có thể chỉ dài 8 byte; phần còn lại là byte rỗng.

## 4. Luồng chạy của server

Phần cốt lõi trong `UdpEchoServer.java`:

```java
try (DatagramSocket socket = new DatagramSocket(PORT)) {
    for (int i = 0; i < MESSAGE_COUNT; i++) {
        // 1. Chuẩn bị gói rỗng và chờ client gửi đến.
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
        socket.receive(request);

        // 2. Đọc đúng phần dữ liệu thực tế.
        String message = new String(
                request.getData(), request.getOffset(), request.getLength(),
                StandardCharsets.UTF_8
        );

        // 3. Gửi lại cho chính client vừa gửi yêu cầu.
        byte[] responseData = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket response = new DatagramPacket(
                responseData,
                responseData.length,
                request.getAddress(), // IP nguồn trở thành IP đích
                request.getPort()     // cổng nguồn trở thành cổng đích
        );
        socket.send(response);
    }
}
```

Bản chất: server không biết trước client ở đâu. Sau `receive()`, server lấy IP và
cổng nguồn từ gói vừa nhận để biết nơi gửi phản hồi.

## 5. Luồng chạy của client

Phần cốt lõi trong `UdpEchoClient.java`:

```java
try (DatagramSocket socket = new DatagramSocket()) {
    InetAddress serverIp = InetAddress.getByName("localhost");

    for (int i = 0; i < MESSAGE_COUNT; i++) {
        // 1. Tạo dữ liệu và gửi đến server.
        byte[] data = ("data | " + i).getBytes(StandardCharsets.UTF_8);
        DatagramPacket request =
                new DatagramPacket(data, data.length, serverIp, SERVER_PORT);
        socket.send(request);

        // 2. Chờ server echo lại.
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        socket.receive(response);

        // 3. Đổi dữ liệu phản hồi thành String để hiển thị.
        String message = new String(
                response.getData(), response.getOffset(), response.getLength(),
                StandardCharsets.UTF_8
        );
        System.out.println("Server phản hồi: " + message);

        Thread.sleep(2000); // dừng 2 giây rồi mới gửi gói tiếp theo
    }
}
```

`localhost` là máy hiện tại, thường tương ứng với IP `127.0.0.1`. Nếu server ở
máy khác, thay `localhost` bằng IP của máy chạy server.

## 6. Cú pháp Java cần nhớ

```java
private static final int PORT = 6789;
```

- `private`: chỉ dùng trong lớp hiện tại.
- `static`: thuộc về lớp, không cần tạo đối tượng.
- `final`: chỉ gán một lần, dùng làm hằng số.
- `int`: kiểu số nguyên.

```java
try (DatagramSocket socket = new DatagramSocket(PORT)) {
    // sử dụng socket
} catch (IOException e) {
    System.err.println(e.getMessage());
}
```

Đây là `try-with-resources`. Java tự gọi `socket.close()` dù chạy thành công hay
xảy ra lỗi. `catch` nhận và xử lý lỗi vào/ra mạng.

```java
for (int i = 0; i < 10; i++) { ... }
```

- `int i = 0`: khởi tạo biến đếm.
- `i < 10`: còn đúng thì tiếp tục lặp.
- `i++`: tăng `i` thêm 1 sau mỗi vòng.

## 7. Toàn bộ quá trình

```text
SERVER                                  CLIENT
new DatagramSocket(6789)                new DatagramSocket()
        |                                       |
receive() <--- chờ                    send(request)
        |                                       |
đọc data + IP/cổng nguồn                       |
        |                                       |
send(response)                         receive() <--- chờ
        |                                       |
lặp 10 lần                              in phản hồi, nghỉ 2 giây
```

Điểm quan trọng nhất: `send()` chỉ đưa một gói đi; `receive()` chỉ lấy một gói
đến. Không có “kết nối UDP” tồn tại giữa hai lần gọi đó.

## 8. Biên dịch và chạy

Mở hai Terminal tại thư mục `_old/BG_4_UDP`.

```powershell
javac -encoding UTF-8 UdpEchoServer.java UdpEchoClient.java
```

Terminal 1 — chạy server trước:

```powershell
java UdpEchoServer
```

Terminal 2 — chạy client:

```powershell
java UdpEchoClient
```

Nếu chạy client khi chưa có server, UDP vẫn có thể gửi gói nhưng client sẽ đứng
ở `receive()` vì không có phản hồi. Trong ứng dụng thực tế, nên đặt giới hạn chờ:

```java
socket.setSoTimeout(3000); // tối đa 3 giây cho mỗi lần receive()
```

## 9. Tự kiểm tra

1. **Socket khác packet ở điểm nào?**

   - `DatagramSocket` là cửa giao tiếp, dùng để gửi và nhận dữ liệu UDP.
   - `DatagramPacket` là gói hàng, chứa dữ liệu cùng thông tin IP và cổng.

2. **Vì sao gói gửi cần IP/cổng, còn gói nhận chỉ cần buffer?**

   Khi gửi:

   - IP cho biết gửi đến máy nào.
   - Cổng cho biết gửi đến chương trình nào trên máy đó.

   Khi nhận:

   - Socket đã mở sẵn tại một cổng để chờ dữ liệu.
   - Vì vậy chỉ cần buffer làm chỗ chứa dữ liệu nhận được.

3. **Vì sao phải dùng `getLength()` khi đọc dữ liệu?**

   - Buffer có thể chứa `1024` byte nhưng dữ liệu thật chỉ có vài byte.
   - `getLength()` cho biết chính xác số byte đã nhận, tránh đọc phần trống.

4. **Vì sao UDP Echo vẫn có thể mất gói dù code không lỗi?**

   - UDP chỉ gửi đi, không kiểm tra gói đã đến hay chưa.
   - UDP không tự gửi lại khi gói bị mất.
   - Gói có thể mất do mạng yếu, nghẽn mạng hoặc firewall.
