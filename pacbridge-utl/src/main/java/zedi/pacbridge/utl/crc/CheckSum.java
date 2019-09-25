package zedi.pacbridge.utl.crc;


public interface CheckSum {
    public abstract int calculatedChecksumForByteArray(byte[] bytes);
    public abstract int calculatedChecksumForByteArray(byte[] bytes, int length);
    public abstract int calculatedChecksumForByteArray(byte[] bytes, int offset, int length);
}