package zedi.pacbridge.net;

public class TransmitProtocolPacketFactory {
    private int bodyOffset;
    private int bodyLength;
    
    
    public TransmitProtocolPacketFactory(int bodyOffset, int bodyLength) {
        this.bodyOffset = bodyOffset;
        this.bodyLength = bodyLength;
    }

    public TransmitProtocolPacket transmitProtocolPacketForByteArray(byte[] buffer) {
        return null;
    }
}
