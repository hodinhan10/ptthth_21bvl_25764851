# Code tham khảo

Thư mục này dùng package bắt đầu bằng `ThamKhao` để không trùng với code bài chính.

## Biên dịch

Chạy tại thư mục gốc `PT_HT_TH`:

```powershell
$sources = Get-ChildItem -LiteralPath ".\ThamKhao" -Recurse -Filter "*.java"
javac -encoding UTF-8 -cp ".\bin" -d ".\bin" $sources.FullName
```

File `.class` được tạo trong `bin/ThamKhao`.

## Ví dụ chạy

```powershell
java -cp bin ThamKhao.OOP.Main
java -cp bin ThamKhao.Lab2.SanPham
java -cp bin ThamKhao.lab1_Basic.Bai1
java -cp bin ThamKhao.BG_4_UDP.UdpEchoServer
java -cp bin ThamKhao.BG_4_UDP.UdpEchoClient
```

Lab 4 có hướng dẫn riêng trong `ThamKhao/lab4/README.md`.

Các ví dụ đọc, ghi hoặc xóa file trong `lab1_Upload_File` đang dùng đường dẫn mẫu `D:\HocJava`. Cần tạo dữ liệu mẫu hoặc đổi đường dẫn trước khi chạy.
