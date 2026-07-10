import java.io.InputStream;
import java.net.Socket;

public class SocketProbe {
    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("172.16.14.9", 13306)) {
            socket.setSoTimeout(10000);
            InputStream in = socket.getInputStream();
            byte[] header = new byte[4];
            int n = in.read(header);
            System.out.println("header=" + n + " " + toHex(header, n));
            int length = (header[0] & 0xff) | ((header[1] & 0xff) << 8) | ((header[2] & 0xff) << 16);
            byte[] payload = new byte[length];
            int off = 0;
            while (off < length) {
                int m = in.read(payload, off, length - off);
                if (m < 0) {
                    break;
                }
                off += m;
            }
            System.out.println("payload=" + off + " version=" + version(payload));
        }
    }

    private static String version(byte[] payload) throws Exception {
        int end = 1;
        while (end < payload.length && payload[end] != 0) {
            end++;
        }
        return new String(payload, 1, end - 1, "UTF-8");
    }

    private static String toHex(byte[] bytes, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(String.format("%02x", bytes[i]));
        }
        return sb.toString();
    }
}
