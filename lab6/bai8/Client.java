package lab6.bai8;

import java.rmi.Naming;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        var sc = new Scanner(System.in);
        try {
            var service = (RemoteService) Naming.lookup("rmi://localhost:1108/RemoteService");
            System.out.println("Các chuyến bay: VN101 (50 ghế), VN202 (80 ghế), VN303 (100 ghế).");
            while (true) {
                System.out.println("\n1. Xem ghế trống | 2. Đặt vé | 3. Hủy vé | 0. Thoát");
                var choice = sc.nextInt();
                sc.nextLine();
                if (choice == 0) break;
                System.out.print("Mã chuyến bay: ");
                String flight = sc.nextLine();

                switch (choice) {
                    case 1:
                        var available = service.availableSeats(flight);
                        if (available > 0) System.out.println("Ghế trống: " + available);
                        else System.out.println("Không có chuyến bay.");
                        break;
                    case 2:
                        System.out.print("Số ghế đặt: ");
                        var seats = sc.nextInt();
                        var isBookTicket = service.bookTicket(flight, seats);

                        if (isBookTicket) System.out.println("Đặt vé thành công");
                        else System.out.println("Đặt thất bại.");
                        break;
                    case 3:
                        System.out.print("Số ghế hủy: ");
                        int cancel = sc.nextInt();
                        var cancelBooking = service.cancelBooking(flight, cancel);

                        if (cancelBooking) System.out.println("Hủy vé thành công.");
                        else System.out.println("Hủy vé thất bại..");
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Loi: " + e.getMessage());
        }
        sc.close();
    }
}
