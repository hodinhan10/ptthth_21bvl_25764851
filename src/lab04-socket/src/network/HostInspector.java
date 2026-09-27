package network;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class HostInspector {
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            printUsage();
            return;
        }

        inspectHost(args[0]);
        if (args.length == 2) {
            inspectUri(args[1]);
        }
    }

    private static void inspectHost(String hostname) {
        System.out.println("=== THÔNG TIN HOST ===");
        System.out.println("Host: " + hostname);

        try {
            InetAddress[] addresses = InetAddress.getAllByName(hostname);

            for (InetAddress address : addresses) {
                System.out.println("- IP: " + address.getHostAddress());
                System.out.println("  Loại: " + getAddressType(address));
                System.out.println("  Canonical: " + address.getCanonicalHostName());
                System.out.println("  Loopback: " + address.isLoopbackAddress());
                System.out.println("  Site local: " + address.isSiteLocalAddress());
            }
        } catch (UnknownHostException e) {
            System.err.println("Không phân giải được host: " + hostname);
        }
    }

    private static String getAddressType(InetAddress address) {
        if (address instanceof Inet4Address) {
            return "IPv4";
        }
        if (address instanceof Inet6Address) {
            return "IPv6";
        }
        return "Không xác định";
    }

    private static void inspectUri(String uriText) {
        System.out.println("=== THÔNG TIN URI ===");
        System.out.println("URI: " + uriText);

        try {
            URI uri = new URI(uriText);
            if (uri.getScheme() == null || uri.getHost() == null) {
                System.err.println("URI không hợp lệ: phải có scheme và host");
                return;
            }

            System.out.println("- Scheme: " + valueOrNone(uri.getScheme()));
            System.out.println("- Host: " + valueOrNone(uri.getHost()));
            System.out.println("- Port: " + (uri.getPort() == -1 ? "mặc định" : uri.getPort()));
            System.out.println("- Path: " + valueOrNone(uri.getPath()));
            System.out.println("- Query: " + valueOrNone(uri.getQuery()));
            System.out.println("- Fragment: " + valueOrNone(uri.getFragment()));
        } catch (URISyntaxException e) {
            System.err.println("URI không hợp lệ: " + e.getMessage());
        }
    }

    private static String valueOrNone(String value) {
        return value == null || value.isEmpty() ? "(không có)" : value;
    }

    private static void printUsage() {
        System.out.println("Cách dùng: java network.HostInspector <hostname> [URI]");
        System.out.println("Ví dụ: java network.HostInspector example.com https://example.com:443/docs?q=java#socket");
    }
}
