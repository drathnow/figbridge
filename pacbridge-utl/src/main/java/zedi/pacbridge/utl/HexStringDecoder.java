package zedi.pacbridge.utl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HexStringDecoder {
    
    public static byte[] hexStringAsBytes(String aHexString) {
        String hexString = aHexString.trim();
        if (hexString.charAt(0) == '|')
            return hexStringWithDelimiterAsBytes(hexString,"\\|");
        if (hexString.length() == 2 || hexString.indexOf(" ") != -1)
            return hexStringWithDelimiterAsBytes(hexString," ");
        return hexStringWithNoDelimiterAsBytes(aHexString);
    }

    public static byte[] hexStringWithDelimiterAsBytes(String aHexString, String aDelimiter) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        String ss[] = aHexString.split(aDelimiter);
        for (int i = 0; i < ss.length; i++)
        {
            String hexString = ss[i].trim();
            if (hexString.length() != 2)
                continue;
            byteArrayOutputStream.write((byte)Short.parseShort(hexString,16));
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] hexStringWithNoDelimiterAsBytes(String aHexString) {
        String hexString = aHexString.trim();
        if (hexString.length() % 2 != 0)
            throw new IllegalArgumentException("Hex string must contain hex pairs");
        byte[] bytes = new byte[(aHexString.length())/2];
        int numberOfReads = hexString.length()/2;
        byte[] buffer = new byte[2];
        int i = 0;
        ByteArrayInputStream arrayReader = new ByteArrayInputStream(hexString.getBytes());
        while (numberOfReads-- > 0) {
            try {
                arrayReader.read(buffer);
            } catch (IOException e) {}
            bytes[i++] = (byte)Short.parseShort(new String(buffer),16);
        }
        return bytes;
    }
}
