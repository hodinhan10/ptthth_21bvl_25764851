package lab6.bai7;

import java.rmi.Naming;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        var sc = new Scanner(System.in);
        try {
            var service = (RemoteService) Naming.lookup("rmi://localhost:1107/RemoteService");
            while (true) {
                System.out.println("\n1. Hình chữ nhật | 2. Hình tròn | 3. Tam giác | 0. Thoát");
                var choice = sc.nextInt();
                if (choice == 0) break;

                switch (choice) {
                    case 1:
                        System.out.print("Chiều rộng: ");
                        var width = sc.nextDouble();
                        System.out.print("Chiều dài: ");
                        var height = sc.nextDouble();

                        var rectangleArea = service.rectangleArea(width, height);
                        System.out.println("Diện tích hình chữ nhật: " + rectangleArea);
                        break;
                    case 2:
                        System.out.print("Bán kính: ");
                        double radius = sc.nextDouble();

                        var circleArea = service.circleArea(radius);
                        System.out.println("Diện tích hình tròn: " + circleArea);
                        break;
                    case 3:
                        System.out.print("Độ dài dáy: ");
                        var base = sc.nextDouble();
                        System.out.print("Chiều cao: ");
                        var triangleHeight = sc.nextDouble();

                        var triangleArea = service.triangleArea(base, triangleHeight);
                        System.out.println("Diện tích tam giác: " + triangleArea);
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
