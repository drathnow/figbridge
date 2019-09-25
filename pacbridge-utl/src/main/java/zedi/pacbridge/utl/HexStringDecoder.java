package zedi.pacbridge.utl;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class HexStringDecoder {
    
    public static byte[] hexStringAsBytes(String aHexString) {
        String hexString = aHexString.trim();
        if (hexString.charAt(0) == '|')
            return hexStringWithDelimiterAsBytes(hexString,"|");
        if (hexString.length() == 2 || hexString.indexOf(" ") != -1)
            return hexStringWithDelimiterAsBytes(hexString," ");
        return hexStringWithNoDelimiterAsBytes(aHexString);
    }

    public static byte[] hexStringWithDelimiterAsBytes(String aHexString, String aDelimiter) {
        byte[] bytes = new byte[(aHexString.length()+1)/3];
        int index = 0;
        char[] hexByte = new char[2]; 
        if (aHexString.substring(0,1).equals(aDelimiter))
            index++;
        int i = 0;
        String hexString = aHexString.trim(); 
        while (index < hexString.length()) {
            hexByte[0] = hexString.charAt(index++);
            hexByte[1] = hexString.charAt(index++);
            index++;
            bytes[i++] = (byte)Short.parseShort(new String(hexByte),16);
        }
        return bytes;
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
