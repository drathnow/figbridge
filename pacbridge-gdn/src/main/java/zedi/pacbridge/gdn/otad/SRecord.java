package zedi.pacbridge.gdn.otad;

import java.io.IOException;
import java.util.Formatter;

import zedi.pacbridge.utl.crc.CcittCheckSum;

abstract class SRecord {

    static CcittCheckSum checkSum = new CcittCheckSum();
    
    byte[] data;
    int address;
    
    public int getAddress() {
        return address;
    }

    public byte[] getData() {
        return data;
    }
    
    protected void verifyCheckSum(byte[] bytes, int expectedChecksum) throws IOException {
        long result = checkSum.calculatedChecksumForByteArray(bytes);
        if (result != expectedChecksum) {
            StringBuilder stringBuilder = new StringBuilder();
            Formatter formatter = new Formatter(stringBuilder);
            formatter.format("Invalid checksum found in S2 record. Expecting 0x%2X but was 0x%2X", expectedChecksum, result);
            formatter.close();
            throw new IOException(stringBuilder.toString());
        }
    }
}
