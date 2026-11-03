package lab6.bai7;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteServiceImpl extends UnicastRemoteObject implements RemoteService {

    public RemoteServiceImpl() throws RemoteException {
        super();
    }

    private void checkLength(double value) throws RemoteException {
        if (!Double.isFinite(value)) throw new RemoteException("Số tiền gửi phải hữu hạn");
        if (value <= 0) throw new RemoteException("Số tiền gửi phải lớn hơn 0");
    }

    @Override // Diện tích hình chữ nhật
    public double rectangleArea(double width, double height) throws RemoteException {
        checkLength(width);
        checkLength(height);
        return width * height;
    }

    @Override // Diện tích hình tròn
    public double circleArea(double radius) throws RemoteException {
        checkLength(radius);
        return Math.PI * radius * radius;
    }

    @Override // Diện tích tam giác
    public double triangleArea(double base, double height) throws RemoteException {
        checkLength(base);
        checkLength(height);
        return base * height / 2;
    }
}
