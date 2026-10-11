# Lab 04 - Socket TCP và UDP

Code được hoàn thiện dựa trên `ThamKhao/lab4`, gồm mục 4.1–4.4 và Bài 1–10.
Các package (nhóm lớp Java) dùng tên `network`, `tcp`, `udp`, `bai1` đến `bai10`.

## Chuẩn bị

- Dùng JDK 17 trở lên; dự án hiện cấu hình JDK 21.
- Tất cả lệnh chạy bằng PowerShell tại thư mục gốc `PT_HT_TH`.
- Chạy server trước, giữ terminal đó mở rồi chạy client ở terminal khác.
- Dùng `Ctrl+C` để dừng server. `QUIT` kết thúc phiên client; riêng multicast, `QUIT` làm các chương trình nhận thoát.
- `localhost` là máy đang chạy chương trình; khi dùng hai máy, thay bằng IP của máy chạy server.

## Danh sách chương trình

| Mục | Chức năng | Cổng mặc định |
| --- | --- | --- |
| 4.1 | Khảo sát địa chỉ mạng | Không dùng |
| 4.2 | TCP gửi và nhận từng dòng | TCP 5000 |
| 4.3 | TCP nhiều client | TCP 5000 |
| 4.4 | UDP gửi yêu cầu, nhận phản hồi | UDP 5001 |
| Bài 1 | Khảo sát host và URI | Không dùng |
| Bài 2 | Đổi chữ số thành chữ | TCP 5002 |
| Bài 3 | Dịch vụ ngày giờ | TCP 5003, UDP 5004 |
| Bài 4 | Máy tính từ xa | TCP 5005 |
| Bài 5 | Chat nhiều client | TCP 5006 |
| Bài 6 | Đo thời gian TCP và UDP | TCP 5010, UDP 5011 |
| Bài 7 | Lưu nhật ký tin nhắn | TCP 5012 |
| Bài 8 | Truyền file | TCP 5013 |
| Bài 9 | Thông báo multicast | UDP 5014 |
| Bài 10 | Tìm dịch vụ qua UDP, kết nối TCP | UDP 5015, TCP 5016 |

## Biên dịch

Chạy bằng PowerShell tại thư mục gốc `PT_HT_TH`:

```powershell
javac -encoding UTF-8 -d bin (Get-ChildItem -Path .\lab04-socket\src -Recurse -Filter *.java).FullName
```

Lệnh trên biên dịch toàn bộ mục 4.1–4.4 và Bài 1–10 vào thư mục `PT_HT_TH/bin`.
Chỉ cần biên dịch lại sau khi sửa code.

File `.class` được lưu trong `bin`; `-encoding UTF-8` giúp đọc đúng tiếng Việt trong nguồn.

## 4.1 - Khảo sát địa chỉ mạng

```powershell
java -cp bin network.HostInspector localhost
java -cp bin network.HostInspector example.com
java -cp bin network.HostInspector host-khong-ton-tai.invalid
```

Chạy riêng từng lệnh. Chương trình in IP, tên host chuẩn, địa chỉ loopback (trỏ về máy mình) và địa chỉ nội bộ; host không tồn tại sẽ báo lỗi.

## 4.2 - TCP client/server theo giao thức dòng

Mở server trước:

```powershell
java -cp bin tcp.TcpCommandServer
```

Mở client trong terminal khác:

```powershell
java -cp bin tcp.TcpCommandClient localhost 5000
```

Nhập từng dòng trong client:

| Yêu cầu | Phản hồi |
| --- | --- |
| `PING` | `OK PONG` |
| `UPPER xin chào` | `OK XIN CHÀO` |
| `TIME` | `OK` kèm ngày giờ của server |
| `ABC` | `ERR UNKNOWN_COMMAND` |
| `QUIT` | `OK BYE`, đóng kết nối |

Server xử lý lần lượt từng client; client hiện tại phải ngắt kết nối thì server mới xử lý client tiếp theo.

## 4.3 - TCP server phục vụ nhiều client

Dừng `TcpCommandServer`, sau đó chạy server nhiều client:

```powershell
java -cp bin tcp.MultiClientTcpServer
```

Mở nhiều terminal và chạy cùng một lệnh client:

```powershell
java -cp bin tcp.TcpCommandClient localhost 5000
```

Các lệnh giống mục 4.2. Server dùng nhóm 20 luồng xử lý; thử để một client chưa nhập gì rồi gửi `PING` từ client khác.

## 4.4 - UDP gửi yêu cầu và nhận phản hồi

Mục tiêu: gửi một datagram (gói UDP), đọc đúng số byte thực nhận và xử lý trường hợp không có phản hồi.

Terminal 1 chạy server:

```powershell
java -cp bin udp.UdpEchoServer
```

Terminal 2 chạy client:

```powershell
java -cp bin udp.UdpEchoClient localhost 5001 "xin chào UDP"
```

Các ca kiểm thử:

- Tiếng Việt: nhận `ACK XIN CHÀO UDP` đúng dấu.
- Server chưa chạy: client báo hết thời gian chờ sau khoảng 3 giây.
- Sai cổng: đổi `5001` thành `5999`, client phải báo hết thời gian chờ.
- Nhiều client: chạy lệnh client ở ba terminal gần đồng thời.
- Bộ đệm nhận có 4096 byte; gói lớn hơn có thể bị cắt, kể cả phản hồi dài hơn do thêm `ACK `. Số byte UTF-8 có thể lớn hơn số ký tự.

## Cách chạy Bài 1–10

Các lệnh bên dưới đều chạy tại thư mục gốc `PT_HT_TH` sau khi đã biên dịch.

## Bài 1 - Host và URI Inspector

```powershell
java -cp bin bai1.HostUriInspector example.com "https://example.com:443/docs?q=java#socket"
```

Tham số đầu là hostname (tên máy) cần tìm IP; tham số sau là URI (chuỗi định danh tài nguyên) cần phân tích. Kết quả gồm IPv4/IPv6 và giao thức, host, cổng, đường dẫn, truy vấn, phần sau `#` của URI.

## Bài 2 - TCP đổi chữ số thành chữ

Terminal 1 chạy server:

```powershell
java -cp bin bai2.DigitServer 5002
```

Terminal 2 chạy client:

```powershell
java -cp bin bai2.DigitClient localhost 5002
```

Các ca biên cần thử: `0` → `OK không`, `9` → `OK chín`; dòng rỗng, `10`, `a`, ` 1 ` → `ERR INVALID_DIGIT`. Chỉ nhận đúng một ký tự từ `0` đến `9`; `QUIT` trả `OK BYE`.

## Bài 3 - Dịch vụ ngày giờ TCP và UDP

TCP:

Terminal 1 chạy server, Terminal 2 chạy client:

```powershell
java -cp bin bai3.TcpDateTimeServer 5003
```

Terminal 2:

```powershell
java -cp bin bai3.TcpDateTimeClient localhost 5003
```

UDP:

Terminal 1 chạy server, Terminal 2 chạy client:

```powershell
java -cp bin bai3.UdpDateTimeServer 5004
```

Terminal 2:

```powershell
java -cp bin bai3.UdpDateTimeClient localhost 5004
```

Lệnh hỗ trợ: `DATE` trả `OK dd MM yyyy`, `TIME` trả `OK HH mm ss`, `DATETIME` trả cả hai theo thời gian của server. Lệnh sai trả `ERR UNKNOWN_COMMAND`. TCP dùng `QUIT` để đóng phiên; UDP client dùng `QUIT` để tự thoát.

Khi server dừng giữa lúc client hoạt động:

- TCP: kết nối đang có bị mất; lần đọc hoặc ghi tiếp theo sẽ báo lỗi hay nhận EOF (kết thúc luồng dữ liệu).
- UDP: không có kết nối cố định; client không biết server đã dừng và chỉ báo timeout sau 3 giây.

## Bài 4 - Máy tính từ xa

Terminal 1 chạy server, Terminal 2 chạy client:

```powershell
java -cp bin bai4.CalculatorServer 5005
```

Terminal 2:

```powershell
java -cp bin bai4.CalculatorClient localhost 5005
```

Chỉ hỗ trợ `+`, `-`, `*`, `/`; phép chia dùng độ chính xác `DECIMAL64`. Nhập `QUIT` để kết thúc.

| Yêu cầu | Phản hồi |
| --- | --- |
| `CALC + 100 200` | `OK 300` |
| `CALC - 10 3` | `OK 7` |
| `CALC * 2.5 4` | `OK 10` |
| `CALC / 10 4` | `OK 2.5` |
| `CALC / 10 0` | `ERR DIVIDE_BY_ZERO` |
| `CALC % 10 3` | `ERR UNSUPPORTED_OPERATOR` |
| `CALC + a 2` | `ERR INVALID_NUMBER` |
| `CALC + 1` | `ERR INVALID_FORMAT` |

## Bài 5 - Chat TCP nhiều client

Terminal 1 chạy server. Mở ít nhất hai terminal khác để chạy client:

```powershell
java -cp bin bai5.ChatServer 5006
```

Terminal 2:

```powershell
java -cp bin bai5.ChatClient localhost 5006
```

Mở nhiều client, nhập nickname (tên trong phòng chat) riêng cho mỗi client, ví dụ `an` và `binh`.

- Tên dài 1–20 ký tự, chỉ gồm chữ không dấu, số, `-`, `_`; tên trùng trả `ERR NICKNAME_EXISTS`.
- `USERS` xem danh sách người dùng; `MSG xin chào` gửi cho các client khác, người gửi nhận `OK SENT`.
- `QUIT` rời phòng; các client khác nhận thông báo tham gia và rời phòng.

## Bài 6 - So sánh TCP và UDP

TCP:

Terminal 1 chạy server, Terminal 2 chạy client:

```powershell
java -cp bin bai6.TcpBenchmarkServer 5010
```

Terminal 2:

```powershell
java -cp bin bai6.TcpBenchmarkClient localhost 5010
```

UDP:

Dừng TCP server nếu không dùng nữa. Terminal 1 chạy UDP server, Terminal 2 chạy UDP client:

```powershell
java -cp bin bai6.UdpBenchmarkServer 5011
```

Terminal 2:

```powershell
java -cp bin bai6.UdpBenchmarkClient localhost 5011
```

Mỗi client tự chạy 5 lần, mỗi lần gửi 1000 thông điệp 256 byte. UDP dùng timeout (thời gian chờ tối đa) 100 ms cho mỗi lần nhận.

- Client gửi một thông điệp rồi chờ phản hồi; kết quả gồm thời gian và số phản hồi nhận được.
- Chạy UDP server trước khi đo; nếu không có phản hồi, cả phép đo có thể mất khoảng 500 giây.
- Kết quả phụ thuộc máy và mạng; dùng số đo thực tế để nhận xét, không kết luận UDP luôn nhanh hơn TCP.

## Bài 7 - Server lưu nhật ký tin nhắn

Terminal 1 chạy server, Terminal 2 chạy client:

```powershell
java -cp bin bai7.MessageLogServer 5012
```

Terminal 2:

```powershell
java -cp bin bai7.MessageLogClient sinhvien01 localhost 5012
```

Nhật ký được lưu tại `data/logs/<clientId>.txt`, tính từ thư mục chạy server. `clientId` (mã client) chỉ được chứa chữ không dấu, số, `-` và `_`.

- Nhập tin nhắn để nhận `OK SAVED`; nhập `QUIT` để thoát.
- Nhật ký gồm thời gian, địa chỉ client và nội dung, được ghi thêm vào file.
- Với cách chạy tại thư mục gốc, file nằm ở `PT_HT_TH\data\logs`; nhiều client cùng mã dùng chung file.

## Bài 8 - Truyền file TCP an toàn

Terminal 1 chạy server, Terminal 2 gửi file:

```powershell
java -cp bin bai8.FileTransferServer 5013
```

Terminal 2:

```powershell
java -cp bin bai8.FileTransferClient ".\lab04-socket\README.md" localhost 5013
```

File hợp lệ được lưu vào `upload`. Server kiểm tra tên file, kích thước và SHA-256 (mã kiểm tra nội dung) trước khi lưu chính thức.

- Ví dụ trên gửi README có sẵn; kết quả nằm ở `PT_HT_TH\upload\README.md` khi server chạy tại thư mục gốc.
- Thành công trả `OK <mã SHA-256>`; kích thước tối đa là 100 MiB.
- Tên chứa đường dẫn, kích thước sai hoặc mã kiểm tra sai bị từ chối. File trùng tên trong `upload` sẽ được thay thế.

## Bài 9 - Multicast thông báo

Multicast là gửi một thông báo tới nhiều máy cùng tham gia nhóm; nhóm ở bài này là `239.255.0.1`.

### Chạy trên máy hiện tại

Các lệnh dưới đây chạy tại thư mục gốc `PT_HT_TH`, dùng code trong `lab04-socket`.

- Card Wi-Fi trên máy hiện tại có tên Java là `wireless_32768`.
- `IUH-lab-1` là tên mạng Wi-Fi đang kết nối, không phải tên card mạng truyền vào chương trình.

**Bước 1 - Biên dịch Bài 9:**

```powershell
javac -encoding UTF-8 -d bin lab04-socket\src\bai9\*.java
```

**Bước 2 - Terminal 1 chạy chương trình nhận:**

```powershell
java -cp bin bai9.MulticastReceiver wireless_32768 5014
```

Mở Terminal 2 và chạy cùng lệnh trên để thử hai chương trình nhận. Giữ cả hai terminal mở; chương trình sẽ đứng chờ tin nhắn.

**Bước 3 - Terminal 3 gửi tin nhắn:**

```powershell
java -cp bin bai9.MulticastSender wireless_32768 "Xin chào multicast" 5014
```

Các terminal nhận sẽ hiển thị IP người gửi và nội dung `Xin chào multicast`.

**Bước 4 - Gửi lệnh kết thúc:**

```powershell
java -cp bin bai9.MulticastSender wireless_32768 "QUIT" 5014
```

Các chương trình nhận rời nhóm và kết thúc khi nhận được `QUIT`.

### Tìm tên card mạng khi chạy trên máy khác

Tìm tên card mạng mà Java nhận diện:

```powershell
jshell
```

Nhập trong JShell:

```java
import java.net.*;
NetworkInterface.networkInterfaces().filter(n -> { try { return n.isUp() && n.supportsMulticast(); } catch (SocketException e) { return false; } }).forEach(n -> System.out.println(n.getName() + " : " + n.getDisplayName()));
/exit
```

Lấy tên bên trái dấu `:` của card mạng đang dùng và hỗ trợ multicast, rồi thay `wireless_32768` trong các lệnh trên bằng tên đó. Tên này có thể khác `Wi-Fi` hiển thị trong Windows.

- Khi chạy giữa nhiều máy, mỗi máy dùng tên card riêng và cùng mạng LAN.
- Nếu không nhận được gói, kiểm tra card hỗ trợ multicast, VPN và tường lửa; mạng trường có thể chặn multicast.

## Bài 10 - UDP discovery kết hợp TCP service

Terminal 1 chạy server. Terminal 2 chạy client khám phá dịch vụ:

```powershell
java -cp bin bai10.DiscoveryServiceServer 5015 5016
```

Terminal 2:

```powershell
java -cp bin bai10.DiscoveryClient 127.0.0.1 5015
```

Để tìm server trong LAN, thay `127.0.0.1` bằng địa chỉ broadcast, ví dụ `192.168.1.255`. Client loại phản hồi trùng, hiển thị mọi server rồi dùng địa chỉ nguồn UDP để kết nối TCP.

- Broadcast là địa chỉ gửi tới các máy trong mạng; chọn địa chỉ phù hợp với cấu hình mạng thực tế.
- Client gửi `DISCOVER_SERVICE`, nhận `SERVICE DATE_TIME 5016 1.0`, thu thập tới khi không có gói mới trong khoảng 2 giây.
- Client tự chọn dịch vụ đầu tiên để kết nối TCP, gửi `PING` và `QUIT`; kết quả mong đợi là `OK PONG`.

## Lỗi thường gặp

- `javac` không được nhận diện: kiểm tra đã cài JDK và thêm thư mục `bin` của JDK vào `PATH`.
- `Could not find or load main class`: biên dịch lại và chạy tại `PT_HT_TH` với đúng tên lớp trong README.
- `Address already in use`: cổng đang được dùng; dừng server cũ, đặc biệt giữa mục 4.2 và 4.3.
- `Connection refused`: kiểm tra server đã chạy, IP và cổng client có đúng không.
- UDP hết thời gian chờ: kiểm tra server, cổng và tường lửa.
- Tiếng Việt hiển thị sai: dùng terminal UTF-8; có thể đặt `$OutputEncoding = [Console]::OutputEncoding = [System.Text.UTF8Encoding]::new()` trước khi chạy.
