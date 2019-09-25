package zedi.pacbridge.gdn.otad;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;


public class OtaFixture {

    public static final String S0RECORD = "S00D00005645523236304231303694";
    public static final String S2RECORD1 = "S2243800003B3BCC001616CFC76C80260FCC99213BEC8416DFC21B8287C7201ECC000416CFB8";
    public static final String S2RECORD2 = "S224380020C7ED806C412615EC8016CF54CC98F53BEC8416DFC21B8287C71B840ACC001D1674";
    public static final String S2RECORD3 = "S224380100E6306B7026FAED8019E887CE9898E6306B7026FA4A8000F6ED806CE8BD2604879E";
    public static final String S2RECORD4 = "S224381440ED84ECE8BD16C3851B84CC00023BCC964C3BED84ECE8BD16C3851B8487C71B8472";
    public static final String S2RECORD5 = "S2053814600A44";
    public static final String S2RECORD6 = "S2243845E06C3BEC82C3026E3BECF3000416C3851B84ED80ACEA026C2705CCFFFF310AED800C";
    public static final String S8RECORD = "S804FFFFFFFE";

    public static final String S2IEEPROM_RECORD1_NOCHECKSUM = "S2240010003B3BCC001616CFC76C80260FCC99213BEC8416DFC21B8287C7201ECC000416CF";
    public static final String S2IEEPROM_RECORD2_NOCHECKSUM = "S224001020C7ED806C412615EC8016CF54CC98F53BEC8416DFC21B8287C71B840ACC001D16";
    public static final String S2IEEPROM_RECORD3_NOCHECKSUM = "S224001820ED84ECE8BD16C3851B84CC00023BCC964C3BED84ECE8BD16C3851B8487C71B84";

    public OtaFixture() {
    }

    public void setUp() {
    }

    public void tearDown() {
    }

    public static CodeBlock codeBlockForAddress(int address, int dataSize) throws IOException {
        CodeBlock codeBlock = new CodeBlock(address);
        codeBlock.addCodeData(byteArrayWithJunk(dataSize));
        return codeBlock;
    }

    public static byte[] byteArrayWithJunk(int sizeOfJunk) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        fillStreamWithJunk(byteArrayOutputStream, sizeOfJunk);
        return byteArrayOutputStream.toByteArray();
    }

    public static void fillStreamWithJunk(OutputStream outputStream, int sizeOfJunk) throws IOException {
        for (int i = 0; i < sizeOfJunk; i++)
            outputStream.write(i % 10);
    }
    
    public static DownloadImage downloadImage() throws IOException {
        return new DownloadImage(new ByteArrayInputStream(downloadImageAsBytes()));
    }
    
    public static byte[] downloadImageAsBytes() { 
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        printStream.println(S0RECORD);
        printStream.println(S2RECORD1);
        printStream.println(S2RECORD2);
        printStream.println(S2RECORD3);
        printStream.println(S2RECORD4);
        printStream.println(S2RECORD5);
        printStream.println(S2RECORD6);
        printStream.println(S8RECORD);
        return outputStream.toByteArray();
    }
}
