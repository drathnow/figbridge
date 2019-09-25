package zedi.pacbridge.stp.apl;

import zedi.pacbridge.utl.crc.CheckSum;


public class AplCheckSum implements CheckSum {
    
    @Override
    public int calculatedChecksumForByteArray(byte[] bytes) {
        return calculatedChecksumForByteArray(bytes, bytes.length);
    }
    
    @Override
    public int calculatedChecksumForByteArray(byte[] bytes, int length) {
        return calculatedChecksumForByteArray(bytes, 0, length);
    }

    @Override
    public int calculatedChecksumForByteArray(byte[] bytes, int offset, int length) {
        byte checksum = 0;
        for (int i = offset; i < length; i++)
            checksum = (byte)((checksum + bytes[i]) & 0xff);
        return ((checksum ^ 0xff) + 1) & 0xff;
    }
}
