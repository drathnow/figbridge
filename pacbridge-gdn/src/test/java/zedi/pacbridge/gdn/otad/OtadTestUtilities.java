package zedi.pacbridge.gdn.otad;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import zedi.pacbridge.utl.HexStringDecoder;
import zedi.pacbridge.utl.crc.CcittCheckSum;

import com.sun.corba.se.impl.orbutil.HexOutputStream;


public class OtadTestUtilities {

    protected static final int MAX_S2RECORD_BYTES = 0x24;
    static boolean trace;

    protected static final String S0RECORD = "S00D00005645523236304231303694";
    protected static final String S2RECORD1 = "S2243800003B3BCC001616CFC76C80260FCC99213BEC8416DFC21B8287C7201ECC000416CFB8";
    protected static final String S2RECORD2 = "S224380020C7ED806C412615EC8016CF54CC98F53BEC8416DFC21B8287C71B840ACC001D1674";
    protected static final String S2RECORD3 = "S224380100E6306B7026FAED8019E887CE9898E6306B7026FA4A8000F6ED806CE8BD2604879E";
    protected static final String S2RECORD4 = "S224381440ED84ECE8BD16C3851B84CC00023BCC964C3BED84ECE8BD16C3851B8487C71B8472";
    protected static final String S2RECORD5 = "S2053814600A44";
    protected static final String S2RECORD6 = "S2243845E06C3BEC82C3026E3BECF3000416C3851B84ED80ACEA026C2705CCFFFF310AED800C";
    protected static final String S2RECORD7 = "S224391503726965720A004154410D0A004154410A0043454C3A204361727269657220446542";
    protected static final String S2RECORD8 = "S22439918009E6EEC110260AC610ED821A416E826B40ED84EEF010EC09E6EEED821A416E8296";
    protected static final String S8RECORD = "S804FFFFFFFE";

    protected static final String S2IEEPROM_RECORD1_NOCHECKSUM = "S2240010003B3BCC001616CFC76C80260FCC99213BEC8416DFC21B8287C7201ECC000416CF";
    protected static final String S2IEEPROM_RECORD2_NOCHECKSUM = "S224001020C7ED806C412615EC8016CF54CC98F53BEC8416DFC21B8287C71B840ACC001D16";
    protected static final String S2IEEPROM_RECORD3_NOCHECKSUM = "S224001820ED84ECE8BD16C3851B84CC00023BCC964C3BED84ECE8BD16C3851B8487C71B84";


    public static DownloadImage flashDownloadImageForSingleStateTest() throws IOException {
        StringWriter stringWriter = new StringWriter();
        stringWriter.write(S0RECORD + "\n");
        stringWriter.write(S2RECORD1 + "\n");
        stringWriter.write(S2RECORD2 + "\n");
        stringWriter.write(S8RECORD + "\n");
        return new DownloadImage(new ByteArrayInputStream(stringWriter.toString().getBytes()));

    }

    public static DownloadImage flashDownloadImageMultipleStateForTest() throws IOException {
        StringWriter stringWriter = new StringWriter();
        stringWriter.write(S0RECORD + "\n");
        stringWriter.write(S2RECORD1 + "\n");
        stringWriter.write(S2RECORD2 + "\n");
        stringWriter.write(S2RECORD3 + "\n");
        stringWriter.write(S2RECORD4 + "\n");
        stringWriter.write(S2RECORD5 + "\n");
        stringWriter.write(S2RECORD6 + "\n");
        stringWriter.write(S8RECORD + "\n");
        return new DownloadImage(new ByteArrayInputStream(stringWriter.toString().getBytes()));
    }

    public static DownloadImage flashDownloadImageWith4CodeBlocks() throws IOException {
        StringWriter stringWriter = flashDownloadImageWith4CodeBlocksAsString();
        return new DownloadImage(new ByteArrayInputStream(stringWriter.toString().getBytes()));
    }

    public static StringWriter flashDownloadImageWith4CodeBlocksAsString() {
        StringWriter stringWriter = new StringWriter();
        stringWriter.write(S0RECORD + "\n");
        stringWriter.write(S2RECORD1 + "\n");
        stringWriter.write(S2RECORD2 + "\n");
        stringWriter.write(S2RECORD3 + "\n");
        stringWriter.write(S2RECORD4 + "\n");
        stringWriter.write(S2RECORD5 + "\n");
        stringWriter.write(S2RECORD6 + "\n");
        stringWriter.write(S2RECORD7 + "\n");
        stringWriter.write(S2RECORD8 + "\n");
        stringWriter.write(S8RECORD + "\n");
        return stringWriter;
    }

    public static DownloadImage internalEEPromDownloadImage() throws IOException {
        StringWriter stringWriter = new StringWriter();
        stringWriter.write(s2recordWithCheckSum(S2IEEPROM_RECORD1_NOCHECKSUM) + "\n");
        stringWriter.write(s2recordWithCheckSum(S2IEEPROM_RECORD2_NOCHECKSUM) + "\n");
        return new DownloadImage(new ByteArrayInputStream(stringWriter.toString().getBytes()));
    }

    public static String s2recordWithCheckSum(String s2Record) throws IOException {
        CcittCheckSum checksum = new CcittCheckSum();
        String hexString = s2Record.substring(2);
        byte[] bytes = HexStringDecoder.hexStringAsBytes(hexString);
        int cs = checksum.calculatedChecksumForByteArray(bytes, 0, bytes.length);
        String str = Integer.toHexString((int)cs & 0xff);
        if (str.length() < 2) {
            str = "0" + str;
        }
        return s2Record + str;
    }    
    public static DownloadImage downloadImageWithNumberOfCodeBlocks(int numberOfCodeBlocks) throws IOException {
        if (numberOfCodeBlocks == 49)
            trace = true;
        if (numberOfCodeBlocks > 256)
            throw new IllegalArgumentException("Number of code blocks must be 256 or less");
        int address = 0x380000;
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < numberOfCodeBlocks * 4; i++) {
            stringBuffer.append(bunchOfS2RecordsFromStartingAddress(address));
            address += 0x100;
        }
        return new DownloadImage(new ByteArrayInputStream(stringBuffer.toString().getBytes()));
    }

    protected static String bunchOfS2RecordsFromStartingAddress(int aStartingAddress) throws IOException {
        byte[] bytes = bytesWithSomeJunk(CodeBlock.MAX_CODEBLOCK_SIZE);
        int offset = 0;
        int length = 0;
        int startingAddress = aStartingAddress;
        StringBuffer stringBuffer = new StringBuffer();
        while (offset < bytes.length - 1) {
            int whatsLeft = bytes.length - offset + 1;
            length = (whatsLeft > MAX_S2RECORD_BYTES) ? MAX_S2RECORD_BYTES : whatsLeft - 1;
            stringBuffer.append(s2recordForAddressAndBytes(startingAddress, bytes, offset, length));
            offset += length;
        }
        return stringBuffer.toString();
    }

    protected static String s2recordForAddressAndBytes(int anAddress, byte[] someBytes, int offset, int length) throws IOException {
        StringWriter stringWriter = new StringWriter();
        HexOutputStream hexOutputStream = new HexOutputStream(stringWriter);
        stringWriter.write("S2");
        hexOutputStream.write(length);
        stringWriter.write(addressAs6ByteHexString(anAddress));
        hexOutputStream.write(someBytes, offset, length);
        hexOutputStream.write((int)OtadUtilities.checksumForHexString(new CcittCheckSum(), stringWriter.toString().substring(2), false));
        stringWriter.write('\n');
        hexOutputStream.close();
        return stringWriter.toString();
    }

    protected static String s2recordForAddressAndBytesFoo(int anAddress, byte[] someBytes, int offset, int length) throws IOException {
        StringWriter stringWriter = new StringWriter();
        HexOutputStream hexOutputStream = new HexOutputStream(stringWriter);
        stringWriter.write("S2");
        hexOutputStream.write(length);
        stringWriter.write(addressAs6ByteHexString(anAddress));
        hexOutputStream.write(someBytes, offset, length);
        hexOutputStream.write((int)OtadUtilities.checksumForHexString(new CcittCheckSum(), stringWriter.toString().substring(2), false));
        stringWriter.write('\n');
        hexOutputStream.close();
        return stringWriter.toString();
    }

    protected static String addressAs6ByteHexString(int anAddress) throws IOException {
        String foo = Integer.toHexString(anAddress).toUpperCase();
        StringBuffer buffer = new StringBuffer();
        for (int i = foo.length(); i < 6; i++)
            buffer.append('0');
        buffer.append(foo);
        return buffer.toString();
    }

    protected static byte[] bytesWithSomeJunk(int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++)
            bytes[i] = (byte)i;
        return bytes;
    }

}
