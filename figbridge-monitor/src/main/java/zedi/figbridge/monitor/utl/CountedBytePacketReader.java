package zedi.figbridge.monitor.utl;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class CountedBytePacketReader {

    private Socket socket;
    
    public CountedBytePacketReader(Socket socket) {
        this.socket = socket;
    }

    public int lengthOfNextPacket(byte[] buffer) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        int len = dataInputStream.readUnsignedShort();
        for (int i = 0; i < len; i++)
            buffer[i] = dataInputStream.readByte();
        return len;
    }
}
