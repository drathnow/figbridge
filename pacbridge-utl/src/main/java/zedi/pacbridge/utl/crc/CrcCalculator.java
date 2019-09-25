package zedi.pacbridge.utl.crc;


import java.nio.ByteBuffer;

public interface CrcCalculator {
    public int calculate(int crcSeed, byte[] src);
    public int calculate(int crcSeed, byte[] src, int length);
    public int calculate(int crcSeed, ByteBuffer byteBuffer);
}
