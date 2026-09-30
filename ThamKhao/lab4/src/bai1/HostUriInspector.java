package ThamKhao.lab4.src.bai1;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class HostUriInspector {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Cách dùng: java bai1.HostUriInspector <hostname> <URI>");
            return;
        }

        inspectHost(args[0]);
        inspectUri(args[1]);
    }

    private static void inspectHost(String hostname) {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(hostname);
            System.out.println("Host: " + hostname);
            for (InetAddress address : addresses) {
                String type;
                if (address instanceof Inet4Address) {
                    type = "IPv4";
                } else if (address instanceof Inet6Address) {
                    type = "IPv6";
                } else {
                    type = "Không xác định";
                }

                System.out.println("- IP: " + address.getHostAddress());
                System.out.println("  Loại: " + type);
                System.out.println("  Loopback: " + address.isLoopbackAddress());
                System.out.println("  Site local: " + address.isSiteLocalAddress());
            }
        } catch (UnknownHostException e) {
            System.err.println("Không phân giải được hostname: " + hostname);
        }
    }

    private static void inspectUri(String value) {
        try {
            URI uri = new URI(value);
            if (uri.getScheme() == null || uri.getHost() == null) {
                System.err.println("URI không hợp lệ: phải có scheme và host");
                return;
            }

            System.out.println("URI: " + value);
            System.out.println("- Scheme: " + uri.getScheme());
            System.out.println("- Host: " + uri.getHost());
            System.out.println("- Port: " + uri.getPort());
            System.out.println("- Path: " + uri.getPath());
            System.out.println("- Query: " + uri.getQuery());
            System.out.println("- Fragment: " + uri.getFragment());
        } catch (URISyntaxException e) {
            System.err.println("URI không hợp lệ: " + e.getMessage());
        }
    }
}
