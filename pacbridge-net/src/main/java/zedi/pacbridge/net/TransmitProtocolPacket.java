package zedi.pacbridge.net;


public class TransmitProtocolPacket extends ProtocolPacketBase {
    
    public TransmitProtocolPacket(byte[] bytes, Integer bodyOffset, Integer bodyLength) {
        super(bytes, bodyOffset, bodyLength);
    }
    
    public TransmitProtocolPacket(int maxPacketSize, Integer bodyOffset, Integer bodyLength) {
        super(maxPacketSize, bodyOffset, bodyLength);
    }

    public void merge() {
        body.setOffset(header.offset());
        body.setLength(header.length()+body.length()+trailer.length());
        header.setLength(0);
        trailer.setOffset(body.offset()+body.length());
        trailer.setLength(0);
    }
    
    public void addHeader(int count) {
        header.setOffset(header.offset()-count);
        header.setLength(header.length()+count);
    }

    public void addTrailer(int count) {
        trailer.setLength(trailer.length()+count);
    }
}
