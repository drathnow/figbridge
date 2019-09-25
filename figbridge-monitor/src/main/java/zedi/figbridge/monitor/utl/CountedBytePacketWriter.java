package zedi.figbridge.monitor.utl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class CountedBytePacketWriter {
    private Socket socket;

    public CountedBytePacketWriter(Socket socket) {
        this.socket = socket;
    }
    
    public void sendBytes(byte[] buffer, int length) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.writeShort(length);
        outputStream.write(buffer, 0, length);
    }
}
