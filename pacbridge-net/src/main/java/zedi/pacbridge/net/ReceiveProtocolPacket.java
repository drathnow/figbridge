package zedi.pacbridge.net;


public class ReceiveProtocolPacket extends ProtocolPacketBase {
    
    public ReceiveProtocolPacket(byte[] bytes, int offset, int length) {
        super(bytes, offset, length);
    }
        
    public ReceiveProtocolPacket(byte[] bytes) {
        super(bytes, 0, bytes.length);
    }
    
    public ReceiveProtocolPacket(int maxPacketSize) {
        this(new byte[maxPacketSize]);
    }

    public void extractTrailer(int count) {
        body.setLength(body.length()-count);
        trailer.setOffset(body.offset()+body.length());
        trailer.setLength(count);
    }
    
    public void extractHeader(int count) {
        body.setOffset(header.offset()+count);
        body.setLength(body.length()-count);
        header.setLength(count);
    }

    public void trim() {
        header.setOffset(body.offset());
        header.setLength(0);
        trailer.setLength(0);
        trailer.setOffset(body.offset()+body.length());
    }
}
