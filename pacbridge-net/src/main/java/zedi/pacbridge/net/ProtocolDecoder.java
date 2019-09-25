package zedi.pacbridge.net;

public interface ProtocolDecoder {
    public void addBytes(byte[] bytes) throws Exception;
    public byte[] nextMessage() throws Exception;
}
