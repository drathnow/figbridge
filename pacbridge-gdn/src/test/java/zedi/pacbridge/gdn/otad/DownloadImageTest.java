package zedi.pacbridge.gdn.otad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.corba.se.impl.orbutil.HexOutputStream;

import zedi.pacbridge.utl.HexStringDecoder;

public class DownloadImageTest {
    protected static final int ADDRESS = 0x380000;
    protected static final String BOOT_AND_VECTORS = "S2243FFE00CF6C00CC0CF05C0A5C0ACC20FF5C0CCC31105C3C790030C6045B32C60F5BAFCC2C\n"
            + "S2243FFE2000005C3EC63F5B38C6115B12CC10F05C34CC00C05C36FC1FFCC44F84FF6C9E27B0\n"
            + "S2173FFE4003CC8000B745F61FFEC1FF2602C6F05B350500DA\n"
            + "S2243FFFC0200020002000200020002000200020002000200020172010200020082000200CA2\n"
            + "S2243FFFE0200020002000200020002000200020002001200420002000C21CC227C227FE008A\n";
    protected static final String BINARYSTRING1 =
            "CF6C00CC0CF05C0A5C0ACC20FF5C0CCC31105C3C790030C6045B32C60F5BAFCC"
            + "00005C3EC63F5B38C6115B12CC10F05C34CC00C05C36FC1FFCC44F84FF6C9E27"
            + "03CC8000B745F61FFEC1FF2602C6F05B350500";
    protected static final String BINARYSTRING2 =
            "200020002000200020002000200020002000200020172010200020082000200C"
            + "200020002000200020002000200020002001200420002000C21CC227C227FE00";
    protected static final String VERSION_STRING = "VER260B106";
    protected static final String HEX_VERSION_STRING = "56455232363042313036";
    protected static final String S0RECORD = "S00D00005645523236304231303694";
    protected static final String S2RECORD1 = "S2243800003B3BCC001616CFC76C80260FCC99213BEC8416DFC21B8287C7201ECC000416CFB8";
    protected static final String S2RECORD2 = "S224380020C7ED806C412615EC8016CF54CC98F53BEC8416DFC21B8287C71B840ACC001D1674";
    protected static final String S2RECORD3 = "S224380100E6306B7026FAED8019E887CE9898E6306B7026FA4A8000F6ED806CE8BD2604879E";
    protected static final String S2RECORD4 = "S224381440ED84ECE8BD16C3851B84CC00023BCC964C3BED84ECE8BD16C3851B8487C71B8472";
    protected static final String S2RECORD5 = "S2053814600A44";
    protected static final String S2RECORD6 = "S2243845E06C3BEC82C3026E3BECF3000416C3851B84ED80ACEA026C2705CCFFFF310AED800C";
    protected static final String S8RECORD = "S804FFFFFFFE";
    protected static final String BINARY_DATA1 = "3B3BCC001616CFC76C80260FCC99213BEC8416DFC21B8287C7201ECC000416CF";
    protected static final String BINARY_DATA2 = "C7ED806C412615EC8016CF54CC98F53BEC8416DFC21B8287C71B840ACC001D16";
    protected static final String BAD_SRECORD1 = "X2243800003B3BCC001616CFC76C80260FCC99213BEC8416DFC21B8287C7201ECC000416CFB8";
    protected static final String BAD_SRECORD2 = "S5243800003B3BCC001616CFC76C80260FCC99213BEC8416DFC21B8287C7201ECC000416CFB8";

    protected static final String S2IEEPROM_RECORD1_NOCHECKSUM = "S2240010003B3BCC001616CFC76C80260FCC99213BEC8416DFC21B8287C7201ECC000416CF";
    protected static final String S2IEEPROM_RECORD2_NOCHECKSUM = "S224001020C7ED806C412615EC8016CF54CC98F53BEC8416DFC21B8287C71B840ACC001D16";
    protected static final String S2IEEPROM_RECORD3_NOCHECKSUM = "S224001820ED84ECE8BD16C3851B84CC00023BCC964C3BED84ECE8BD16C3851B8487C71B84";

    protected static final String S2EEEPROM_RECORD1_NOCHECKSUM = "S2240000003B3BCC001616CFC76C80260FCC99213BEC8416DFC21B8287C7201ECC000416CF";
    protected static final String S2EEEPROM_RECORD2_NOCHECKSUM = "S224000020C7ED806C412615EC8016CF54CC98F53BEC8416DFC21B8287C71B840ACC001D16";
    protected static final String S2EEEPROM_RECORD3_NOCHECKSUM = "S224001820ED84ECE8BD16C3851B84CC00023BCC964C3BED84ECE8BD16C3851B8487C71B84";

    protected StringWriter stringWriter;
    private DownloadImage downloadImage;

    @Before
    public void setUp() throws Exception {
        downloadImage = new DownloadImage();
        stringWriter = new StringWriter();
    }

    @After
    public void tearDown() throws Exception {
        downloadImage = null;
    }

    @Test
    public void testNumberOfCodeBlocks() throws IOException {
        DownloadImage downloadImage = OtadTestUtilities.downloadImageWithNumberOfCodeBlocks(256);
        assertEquals(256,downloadImage.numberOfCodeBlocks());
    }

    @Test
    public void testLoadBootAndVectors() throws IOException {
        downloadImage = new DownloadImage(new ByteArrayInputStream(BOOT_AND_VECTORS.getBytes()));
        assertEquals(1, downloadImage.getCodeBlocks().size());
        assertEquals(0x3FFFE0 + 31, ((CodeBlock)downloadImage.getCodeBlocks().get(0)).lastAddressOfData());
        byte[] bytes = bar();
        byte[] codeBytes = ((CodeBlock)downloadImage.getCodeBlocks().get(0)).codeData();
        for (int i = 0; i < bytes.length; i++)
            assertEquals("Byte " + i + " is not equal.", Integer.toHexString(bytes[i]), Integer.toHexString(codeBytes[i]));
    }

    @Test
    public void testCodeMapForFlash() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(S2RECORD1.getBytes());
        outputStream.write('\n');
        outputStream.write(S2RECORD6.getBytes());
        outputStream.write('\n');
        
        downloadImage = new DownloadImage(new ByteArrayInputStream(outputStream.toByteArray()));
        CodeBlockMap codeMap = downloadImage.codeMap();
        assertTrue(codeMap.isBit0Set());
        assertTrue(codeMap.isBit1Set(2));
    }

    @Test
    public void testCodeMapForEEProm() throws IOException {

        String record1 = OtadTestUtilities.s2recordWithCheckSum(S2IEEPROM_RECORD1_NOCHECKSUM);
        String record2 = OtadTestUtilities.s2recordWithCheckSum(S2IEEPROM_RECORD2_NOCHECKSUM);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(record1.getBytes());
        outputStream.write('\n');
        outputStream.write(record2.getBytes());
        outputStream.write('\n');
        
        downloadImage = new DownloadImage(new ByteArrayInputStream(outputStream.toByteArray()));

        CodeBlockMap codeMap = downloadImage.codeMap();
        assertTrue(codeMap.isBit4Set());
    }

    @Test
    public void testInternalEEPromMapWithTwoBlocks() throws IOException {
        String record1 = OtadTestUtilities.s2recordWithCheckSum(S2IEEPROM_RECORD1_NOCHECKSUM);
        String record2 = OtadTestUtilities.s2recordWithCheckSum(S2IEEPROM_RECORD3_NOCHECKSUM);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(record1.getBytes());
        outputStream.write('\n');
        outputStream.write(record2.getBytes());
        outputStream.write('\n');
        
        downloadImage = new DownloadImage(new ByteArrayInputStream(outputStream.toByteArray()));
        CodeBlockMap codeMap = downloadImage.codeMap();
        assertTrue(codeMap.isBit4Set());
        assertFalse(codeMap.isBit5Set());
        assertTrue(codeMap.isBit6Set());
    }

    @Test
    public void testLoadFromFile() throws IOException {
        File file = File.createTempFile("DownloadImageTest", "s2");
        file.deleteOnExit();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write((S0RECORD + '\n').getBytes());
        fileOutputStream.write((S2RECORD1 + '\n').getBytes());
        fileOutputStream.write((S2RECORD2 + '\n').getBytes());
        fileOutputStream.write((S2RECORD3 + '\n').getBytes());
        fileOutputStream.write((S2RECORD4 + '\n').getBytes());
        fileOutputStream.write((S2RECORD5 + '\n').getBytes());
        fileOutputStream.write((S2RECORD6 + '\n').getBytes());
        fileOutputStream.write((S8RECORD + '\n').getBytes());
        fileOutputStream.close();
        downloadImage = new DownloadImage(file);
        assertEquals(3, downloadImage.getCodeBlocks().size());
        assertEquals(0x380000, downloadImage.getStartingAddress());
    }

    @Test
    public void testLoadFromStream() throws IOException {
        downloadImage = new DownloadImage(new ByteArrayInputStream(downloadImageAsBytes()));
        assertEquals(3, downloadImage.getCodeBlocks().size());
    }

    public static byte[] downloadImageAsBytes() {
        StringWriter stringWriter = new StringWriter();
        stringWriter.write(S0RECORD + '\n');
        stringWriter.write(S2RECORD1 + '\n');
        stringWriter.write(S2RECORD2 + '\n');
        stringWriter.write(S2RECORD3 + '\n');
        stringWriter.write(S2RECORD4 + '\n');
        stringWriter.write(S2RECORD5 + '\n');
        stringWriter.write(S2RECORD6 + '\n');
        stringWriter.write(S8RECORD + '\n');
        return stringWriter.toString().getBytes();
    }

    @Test
    public void testParseNoncontiguousShortS2Records() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(S2RECORD4.getBytes());
        outputStream.write('\n');
        outputStream.write(S2RECORD5.getBytes());
        outputStream.write('\n');
        outputStream.write(S2RECORD6.getBytes());
        outputStream.write('\n');
        
        downloadImage = new DownloadImage(new ByteArrayInputStream(outputStream.toByteArray()));

        assertEquals(2, downloadImage.getCodeBlocks().size());
    }

    @Test
    public void testParseNoncontiguousS2Records() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(S2RECORD1.getBytes());
        outputStream.write('\n');
        outputStream.write(S2RECORD2.getBytes());
        outputStream.write('\n');
        outputStream.write(S2RECORD3.getBytes());
        outputStream.write('\n');
        
        downloadImage = new DownloadImage(new ByteArrayInputStream(outputStream.toByteArray()));

        assertEquals(1, downloadImage.getCodeBlocks().size());
    }

    @Test
    public void testParseContiguousS2Records() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(S2RECORD1.getBytes());
        outputStream.write('\n');
        outputStream.write(S2RECORD2.getBytes());
        outputStream.write('\n');
        
        downloadImage = new DownloadImage(new ByteArrayInputStream(outputStream.toByteArray()));

        assertEquals(1, downloadImage.getCodeBlocks().size());
        CodeBlock codeBlock = (CodeBlock)downloadImage.getCodeBlocks().get(0);
        assertEquals(ADDRESS, codeBlock.startAddressOfData());
        assertEquals(ADDRESS + BINARY_DATA1.length() - 1, codeBlock.lastAddressOfData());
        StringWriter stringWriter = new StringWriter();
        HexOutputStream hexOutputStream = new HexOutputStream(stringWriter);
        hexOutputStream.write(codeBlock.codeData());
        hexOutputStream.close();
        assertEquals(BINARY_DATA1 + BINARY_DATA2, stringWriter.toString().toUpperCase());
    }

    @Test
    public void testParseBadSRecord() throws IOException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(BAD_SRECORD1.getBytes());
            outputStream.write('\n');
            
            downloadImage = new DownloadImage(new ByteArrayInputStream(outputStream.toByteArray()));
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(BAD_SRECORD2.getBytes());
            outputStream.write('\n');
            
            downloadImage = new DownloadImage(new ByteArrayInputStream(outputStream.toByteArray()));
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testParseSingleS2Record() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(S2RECORD1.getBytes());
        outputStream.write('\n');
        
        downloadImage = new DownloadImage(new ByteArrayInputStream(outputStream.toByteArray()));

        assertEquals(1, downloadImage.getCodeBlocks().size());
        CodeBlock codeBlock = (CodeBlock)downloadImage.getCodeBlocks().get(0);
        assertEquals(ADDRESS, codeBlock.startAddressOfData());
        StringWriter stringWriter = new StringWriter();
        HexOutputStream hexOutputStream = new HexOutputStream(stringWriter);
        hexOutputStream.write(codeBlock.codeData());
        hexOutputStream.close();
        assertEquals(BINARY_DATA1, stringWriter.toString().toUpperCase());
    }
        
    private byte[] bar() throws IOException {
        byte[] bytes1 = HexStringDecoder.hexStringAsBytes(BINARYSTRING1);
        byte[] bytes2 = HexStringDecoder.hexStringAsBytes(BINARYSTRING2);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(bytes1);
        for (int i = 0; i < 365; i++)
            byteArrayOutputStream.write(0xff);
        byteArrayOutputStream.write(bytes2);
        return byteArrayOutputStream.toByteArray();
    }
}
