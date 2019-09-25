package zedi.pacbridge.utl.crc;


public class CcittCheckSum implements CheckSum {

    public int calculatedChecksumForByteArray(byte[] bytes) {
        return calculatedChecksumForByteArray(bytes, 0, bytes.length);
    }

    public int calculatedChecksumForByteArray(byte[] bytes, int length) {
        return calculatedChecksumForByteArray(bytes, 0, length);
    }

    public int calculatedChecksumForByteArray(byte[] bytes, int offset, int length) {
        int checksum = 0;
        for (int i = offset; i < length; i++)
            checksum += bytes[i];
        return ~checksum & 0xff;
    }

}
