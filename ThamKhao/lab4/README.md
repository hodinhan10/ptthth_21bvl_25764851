# Lab 04 - Socket TCP và UDP

## Biên dịch

Chạy tại thư mục `ThamKhao/lab4`:

```powershell
javac -encoding UTF-8 -d ../../bin (Get-ChildItem -Recurse src -Filter *.java).FullName
```

Lệnh trên biên dịch toàn bộ mục 4.1–4.4 và Bài 1–10 vào thư mục `PT_HT_TH/bin`.
Chỉ cần biên dịch lại sau khi sửa code.

## 4.1 - Khảo sát địa chỉ mạng

```powershell
java -cp ../../bin ThamKhao.lab4.src.network.HostInspector localhost
java -cp ../../bin ThamKhao.lab4.src.network.HostInspector example.com
java -cp ../../bin ThamKhao.lab4.src.network.HostInspector host-khong-ton-tai.invalid
```

## 4.2 - TCP client/server theo giao thức dòng

Mở server trước:

```powershell
java -cp ../../bin ThamKhao.lab4.src.tcp.TcpCommandServer
```

Mở client trong terminal khác:

```powershell
java -cp ../../bin ThamKhao.lab4.src.tcp.TcpCommandClient localhost 5000
```

## 4.3 - TCP server phục vụ nhiều client

Dừng `TcpCommandServer`, sau đó chạy server nhiều client:

```powershell
java -cp ../../bin ThamKhao.lab4.src.tcp.MultiClientTcpServer
```

Mở nhiều terminal và chạy cùng một lệnh client:

```powershell
java -cp ../../bin ThamKhao.lab4.src.tcp.TcpCommandClient localhost 5000
```

## 4.4 - UDP request response

Mục tiêu: gửi một datagram, đọc đúng số byte thực nhận và xử lý trường hợp không có phản hồi.

Terminal 1 chạy server:

```powershell
java -cp ../../bin ThamKhao.lab4.src.udp.UdpEchoServer
```

Terminal 2 chạy client:

```powershell
java -cp ../../bin ThamKhao.lab4.src.udp.UdpEchoClient localhost 5001 "xin chào UDP"
```

Các ca kiểm thử:

- Tiếng Việt: nhận `ACK XIN CHÀO UDP` đúng dấu.
- Server chưa chạy: client báo timeout sau khoảng 3 giây.
- Sai cổng: đổi `5001` thành `5999`, client phải timeout.
- Nhiều client: chạy lệnh client ở ba terminal gần đồng thời.
- Thông điệp dài: thử dữ liệu gần 4096 byte để quan sát giới hạn buffer.

## Cách chạy Bài 1–10

Các lệnh bên dưới đều chạy tại thư mục `ThamKhao/lab4` sau khi đã biên dịch.

## Bài 1 - Host và URI Inspector

```powershell
java -cp ../../bin ThamKhao.lab4.src.bai1.HostUriInspector example.com "https://example.com:443/docs?q=java#socket"
```

## Bài 2 - TCP đổi chữ số thành chữ

Terminal 1 chạy server:

```powershell
java -cp ../../bin ThamKhao.lab4.src.bai2.DigitServer 5002
```

Terminal 2 chạy client:

```powershell
java -cp ../../bin ThamKhao.lab4.src.bai2.DigitClient localhost 5002
```

Các ca biên cần thử: `0`, `9`, dòng rỗng, `10`, `a`, ` 1 ` và `QUIT`.

## Bài 3 - Dịch vụ ngày giờ TCP và UDP

TCP:

Terminal 1 chạy server, Terminal 2 chạy client:

```powershell
java -cp ../../bin ThamKhao.lab4.src.bai3.TcpDateTimeServer 5003
java -cp ../../bin ThamKhao.lab4.src.bai3.TcpDateTimeClient localhost 5003
```

UDP:

Terminal 1 chạy server, Terminal 2 chạy client:

```powershell
java -cp ../../bin ThamKhao.lab4.src.bai3.UdpDateTimeServer 5004
java -cp ../../bin ThamKhao.lab4.src.bai3.UdpDateTimeClient localhost 5004
```

Lệnh hỗ trợ: `DATE`, `TIME`, `DATETIME`. TCP dùng thêm `QUIT` để đóng phiên.

Khi server dừng giữa lúc client hoạt động:

- TCP: kết nối đang có bị mất; lần đọc hoặc ghi tiếp theo sẽ báo lỗi hay nhận EOF.
- UDP: không có kết nối cố định; client không biết server đã dừng và chỉ báo timeout sau 3 giây.

## Bài 4 - Máy tính từ xa

Terminal 1 chạy server, Terminal 2 chạy client:

```powershell
java -cp ../../bin ThamKhao.lab4.src.bai4.CalculatorServer 5005
java -cp ../../bin ThamKhao.lab4.src.bai4.CalculatorClient localhost 5005
```

Ví dụ: `CALC + 100 200`, `CALC / 10 0`, `CALC % 10 3` và `QUIT`.

## Bài 5 - Chat TCP nhiều client

Terminal 1 chạy server. Mở ít nhất hai terminal khác để chạy client:

```powershell
java -cp ../../bin ThamKhao.lab4.src.bai5.ChatServer 5006
java -cp ../../bin ThamKhao.lab4.src.bai5.ChatClient localhost 5006
```

Mở nhiều client, nhập nickname duy nhất rồi dùng `USERS`, `MSG nội_dung` và `QUIT`.

## Bài 6 - So sánh TCP và UDP

TCP:

Terminal 1 chạy server, Terminal 2 chạy client:

```powershell
java -cp ../../bin ThamKhao.lab4.src.bai6.TcpBenchmarkServer 5010
java -cp ../../bin ThamKhao.lab4.src.bai6.TcpBenchmarkClient localhost 5010
```

UDP:

Dừng TCP server nếu không dùng nữa. Terminal 1 chạy UDP server, Terminal 2 chạy UDP client:

```powershell
java -cp ../../bin ThamKhao.lab4.src.bai6.UdpBenchmarkServer 5011
java -cp ../../bin ThamKhao.lab4.src.bai6.UdpBenchmarkClient localhost 5011
```

Mỗi client tự chạy 5 lần, mỗi lần gửi 1000 thông điệp 256 byte. UDP dùng timeout 100 ms.

## Bài 7 - Server lưu nhật ký tin nhắn

Terminal 1 chạy server, Terminal 2 chạy client:

```powershell
java -cp ../../bin ThamKhao.lab4.src.bai7.MessageLogServer 5012
java -cp ../../bin ThamKhao.lab4.src.bai7.MessageLogClient sinhvien01 localhost 5012
```

Nhật ký được lưu tại `data/logs/<clientId>.txt`. `clientId` chỉ được chứa chữ, số, `-` và `_`.

## Bài 8 - Truyền file TCP an toàn

Terminal 1 chạy server, Terminal 2 gửi file:

```powershell
java -cp ../../bin ThamKhao.lab4.src.bai8.FileTransferServer 5013
java -cp ../../bin ThamKhao.lab4.src.bai8.FileTransferClient ".\du-lieu.txt" localhost 5013
```

File hợp lệ được lưu vào `upload`. Server kiểm tra tên file, kích thước và SHA-256 trước khi lưu chính thức.

## Bài 9 - Multicast thông báo

Xem tên card mạng đang hoạt động:

```powershell
Get-NetAdapter | Where-Object Status -eq Up
```

Chạy ít nhất hai receiver trước, sau đó chạy sender:

```powershell
java -cp ../../bin ThamKhao.lab4.src.bai9.MulticastReceiver "Wi-Fi" 5014
java -cp ../../bin ThamKhao.lab4.src.bai9.MulticastSender "Wi-Fi" "Xin chào multicast" 5014
```

Gửi thông điệp `QUIT` để receiver rời group trước khi đóng socket. Nếu không nhận được gói, kiểm tra Wi-Fi, VPN và firewall.

## Bài 10 - UDP discovery kết hợp TCP service

Terminal 1 chạy server. Terminal 2 chạy client khám phá dịch vụ:

```powershell
java -cp ../../bin ThamKhao.lab4.src.bai10.DiscoveryServiceServer 5015 5016
java -cp ../../bin ThamKhao.lab4.src.bai10.DiscoveryClient 127.0.0.1 5015
```

Để tìm server trong LAN, thay `127.0.0.1` bằng địa chỉ broadcast, ví dụ `192.168.1.255`. Client loại phản hồi trùng, hiển thị mọi server rồi dùng địa chỉ nguồn UDP để kết nối TCP.
