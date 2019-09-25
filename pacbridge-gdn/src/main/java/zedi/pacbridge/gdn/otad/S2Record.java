package zedi.pacbridge.gdn.otad;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import zedi.pacbridge.utl.HexStringDecoder;


/**
 * Typical S2 record: S2243800003B3BCC001616CFC76C80260FCC99213BEC8416DFC21B8287C7201ECC000416CFB8
 * 
 * Fields are separated as:
 * 
 * S2 24 380000 3B3BCC001616CFC76C80260FCC99213BEC8416DFC21B8287C7201ECC000416CF B8
 * 
 * Record Length : 24 (length byte + address bytes + data bytes)
 * Address       : 380000
 * Data          : 3B3BCC001616CFC76C80260FCC99213BEC8416DFC21B8287C7201ECC000416CF
 * CheckSum      : B8 - Calculated from above bytes (exclude "S0" and checksum byte)
 */
public class S2Record extends SRecord {

    
    public S2Record(String record) throws IOException {
        String interestString = record.substring(2, record.length() - 2);
        byte[] bytes = HexStringDecoder.hexStringWithNoDelimiterAsBytes(interestString);
        int cheksum = Integer.valueOf(record.substring(record.length()-2, record.length()), 16).intValue();
        verifyCheckSum(bytes, cheksum);
        
        
        byte[] addressBytes = new byte[4];
        for (int i = 1; i < addressBytes.length; i++) 
            addressBytes[i] = bytes[i];
        
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(addressBytes);
        DataInputStream dataInputStream = new DataInputStream(arrayInputStream);
        address = dataInputStream.readInt();
        
        int length = bytes[0] - 4;
        data = new byte[length];
        System.arraycopy(bytes, 4, data, 0, length);
    }
}
