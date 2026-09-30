# Lab 04 - Socket TCP và UDP

## 4.1 - Khảo sát địa chỉ mạng

Các lệnh dưới đây được chạy tại thư mục gốc `PT_HT_TH`.

### Biên dịch

```powershell
javac -encoding UTF-8 -d bin lab04-socket\src\network\HostInspector.java
```

File `.class` sau khi biên dịch được lưu trong `bin\network`.

### Chạy với localhost

```powershell
java -cp bin network.HostInspector localhost
```

### Chạy với hostname hợp lệ

```powershell
java -cp bin network.HostInspector example.com
```

### Chạy với hostname không tồn tại

```powershell
java -cp bin network.HostInspector host-khong-ton-tai.invalid
```

Lưu ý: mỗi lệnh phải được chạy riêng. Tham số `-encoding UTF-8` giúp `javac` đọc đúng nội dung tiếng Việt trong file nguồn.

## 4.2 - TCP client/server theo giao thức dòng

Các lệnh dưới đây được chạy tại thư mục gốc `PT_HT_TH`.

### Biên dịch server và client

```powershell
javac -encoding UTF-8 -d bin lab04-socket\src\tcp\TcpCommandServer.java lab04-socket\src\tcp\TcpCommandClient.java
```

### Chạy server

Mở Terminal 1 và chạy:

```powershell
java -cp bin tcp.TcpCommandServer
```

Server phải được chạy trước để mở cổng `5000` và chờ client kết nối.

### Chạy client

Giữ Terminal 1 đang chạy server. Mở Terminal 2 tại thư mục gốc `PT_HT_TH` rồi chạy:

```powershell
java -cp bin tcp.TcpCommandClient localhost 5000
```

Trong đó:

- `localhost` là máy đang chạy server.
- `5000` là cổng mà server đang lắng nghe.
- Có thể nhập lần lượt `PING`, `UPPER xin chào`, `TIME`, `ABC` và `QUIT` để kiểm tra.

## Bài 1 - Host và URI Inspector

Các lệnh dưới đây được chạy tại thư mục gốc `PT_HT_TH`.

### Biên dịch

```powershell
javac -encoding UTF-8 -d bin lab04-socket\src\bai1\HostUriInspector.java
```

### Chạy

```powershell
java -cp bin bai1.HostUriInspector example.com "https://example.com:443/docs?q=java#socket"
```

Chương trình nhận hai tham số:

- `example.com` là hostname cần tìm địa chỉ IP.
- Chuỗi phía sau là URI cần phân tích.

## Bài 2 - TCP đổi chữ số thành chữ

### Biên dịch

```powershell
javac -encoding UTF-8 -d bin lab04-socket\src\bai2\DigitServer.java lab04-socket\src\bai2\DigitClient.java
```

### Chạy server

Mở Terminal 1:

```powershell
java -cp bin bai2.DigitServer 5002
```

### Chạy client

Giữ server hoạt động và mở Terminal 2:

```powershell
java -cp bin bai2.DigitClient localhost 5002
```

Nhập lần lượt `0`, `9`, dòng rỗng, `10`, `a`, ` 1 ` và `QUIT` để kiểm tra.

## Bài 3 - Dịch vụ ngày giờ TCP và UDP

### Biên dịch

```powershell
javac -encoding UTF-8 -d bin lab04-socket\src\bai3\DateTimeService.java lab04-socket\src\bai3\TcpDateTimeServer.java lab04-socket\src\bai3\TcpDateTimeClient.java lab04-socket\src\bai3\UdpDateTimeServer.java lab04-socket\src\bai3\UdpDateTimeClient.java
```

### Chạy bằng TCP

Terminal 1 chạy TCP server:

```powershell
java -cp bin bai3.TcpDateTimeServer 5003
```

Terminal 2 chạy TCP client:

```powershell
java -cp bin bai3.TcpDateTimeClient localhost 5003
```

### Chạy bằng UDP

Dừng TCP server nếu không dùng nữa. Terminal 1 chạy UDP server:

```powershell
java -cp bin bai3.UdpDateTimeServer 5004
```

Terminal 2 chạy UDP client:

```powershell
java -cp bin bai3.UdpDateTimeClient localhost 5004
```

Các lệnh ngày giờ gồm `DATE`, `TIME` và `DATETIME`. TCP hỗ trợ thêm `QUIT`; UDP client dùng `QUIT` để tự kết thúc.
