package ThamKhao.lab4.src.bai9;

import java.net.NetworkInterface;
import java.net.SocketException;

final class MulticastSupport {
    private MulticastSupport() {
    }

    static NetworkInterface findInterface(String name) throws SocketException {
        NetworkInterface networkInterface = NetworkInterface.getByName(name);
        if (networkInterface == null) {
            throw new IllegalArgumentException("Không tìm thấy card mạng: " + name);
        }
        if (!networkInterface.isUp()) {
            throw new IllegalArgumentException("Card mạng chưa hoạt động: " + name);
        }
        if (!networkInterface.supportsMulticast()) {
            throw new IllegalArgumentException("Card mạng không hỗ trợ multicast: " + name);
        }
        return networkInterface;
    }
}
