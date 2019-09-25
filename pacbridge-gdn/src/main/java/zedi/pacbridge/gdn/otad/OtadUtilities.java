package zedi.pacbridge.gdn.otad;

import java.io.IOException;

import zedi.pacbridge.utl.HexStringDecoder;
import zedi.pacbridge.utl.crc.CheckSum;

public class OtadUtilities {

    public static long checksumForHexString(CheckSum checksum, String hexString, boolean whiteSpaceSeparator) throws IOException {
        byte[] bytes = HexStringDecoder.hexStringAsBytes(hexString);
        return checksum.calculatedChecksumForByteArray(bytes);
    }
}
