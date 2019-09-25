package zedi.figbridge.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class ConnectWithoutAuthenticating {

    public static void main(String[] args) {
        Socket socket = null;
        long now = 0;
        try {
            System.out.println("Connecting...");
            socket = new Socket(InetAddress.getLocalHost(), 3100);
            now = System.currentTimeMillis();
            System.out.println("Connected, reading....");
            int size = socket.getInputStream().available();
            socket.getInputStream().read(new byte[size], 0, size);
            System.out.println("Got challenge...");
            while (socket.getInputStream().read(new byte[size], 0, size) == 0) {
                Thread.sleep(1000);
                System.out.print(".");
            }
        } catch (Exception e) {
            System.out.println();
            long diff = System.currentTimeMillis() - now;
            System.out.println("Exception tossed after " + TimeUnit.MILLISECONDS.toSeconds(diff) + " seconds " + e.toString());
        }
        try {
            if (socket != null)
                socket.close();
        } catch (IOException e) {
        }
    }
}
