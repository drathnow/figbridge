package zedi.pacbridge.gdn.otad;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zedi.pacbridge.utl.HexStringDecoder;

/**
 * Parse an S0 record that contains the version number of a firmware image
 * 
 * Typical S0 Record: 
 * 
 *      S00D00005645523236304231303694
 *
 * Fields are separated as:
 * 
 * S0 0D 0000 56455232363042313036 94
 *  
 * Record Length : 0D (length byte + address bytes + data bytes) does not include "S0" bytes and checksum byte"
 * Address       : 0000
 * Version String: 56455232363042313036 (VER260B106)
 * CheckSum      : 94 - Calculated from above bytes (exclude "S0" and checksum byte)
 * 
 * There are two possible formats for the version number.  The format version 1 fromat above, and the
 * version 2 format of "2VER0260B0106" where the version number and build number are 4 digits. 
 */
class S0Record extends SRecord {

    public static final String UNKNOWN_VERSION_FORMAT_ERROR = "Unknown version format: ";
    private static String VERSION_FORMAT = "V%d.%d Build %05d";
    private static Pattern version1CodingPattern = Pattern.compile("VER(\\d{3}?)B(\\d{3})?");
    private static Pattern version2CodingPattern = Pattern.compile("2VER(\\d{4}?)B(\\d{5}?)");
    
    private String rawVersionString;
    private int versionNumber;
    private int buildNumber;
    
    public S0Record(String record) throws IOException {
        
        String interestString = record.substring(2, record.length() - 2);
        byte[] bytes = HexStringDecoder.hexStringWithNoDelimiterAsBytes(interestString);
        int cheksum = Integer.valueOf(record.substring(record.length()-2, record.length()), 16).intValue();
        verifyCheckSum(bytes, cheksum);
        
        byte[] addressBytes = new byte[2];
        for (int i = 1; i < addressBytes.length; i++) 
            addressBytes[i] = bytes[i];
        
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(addressBytes);
        DataInputStream dataInputStream = new DataInputStream(arrayInputStream);
        address = dataInputStream.readUnsignedShort();
        
        int length = bytes[0] - 3;
        data = new byte[length];
        System.arraycopy(bytes, 3, data, 0, length);
        
        rawVersionString = new String(data);
    }

    public String getRawVersionString() {
        return rawVersionString;
    }

    public String getFormattedVersionString() {
        StringBuilder stringBuilder = new StringBuilder();
        Matcher matcher = version1CodingPattern.matcher(rawVersionString);
        if (matcher.matches() == false)
            matcher = version2CodingPattern.matcher(rawVersionString);
        
        if (matcher.matches()) {
            versionNumber = Integer.parseInt(matcher.group(1));
            buildNumber = Integer.parseInt(matcher.group(2));
            int major = versionNumber / 100;
            int minor = (versionNumber % 100);
            Formatter formatter = new Formatter(stringBuilder);
            formatter.format(VERSION_FORMAT, major, minor, buildNumber);
            formatter.close();
        } else
            stringBuilder.append(UNKNOWN_VERSION_FORMAT_ERROR + rawVersionString);
        return stringBuilder.toString();
    }

    public int getVersionNumber() {
        return versionNumber;
    }
    
    public int getBuildNumber() {
        return buildNumber;
    }
}
