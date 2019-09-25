package zedi.pacbridge.gdn.otad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringDecoder;


public class CodeBlockTest extends BaseTestCase{
    protected static final int ADDRESS = 0x380000;
    protected static final String BINARY_DATA = "3B3BCC001616CFC76C80260FCC99213BEC8416DFC21B8287C7201ECC000416CF";

    protected byte[] bigArray;
    protected CodeBlock codeBlock;
    protected ByteArrayOutputStream byteArrayOutputStream;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        codeBlock = new CodeBlock(ADDRESS);
        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        codeBlock = null;
        super.tearDown();
    }

    @Test
    public void testFillByteArrayWithFF() throws IOException {
        int address = 0x380000;
        codeBlock = new CodeBlock(address);
        byteArrayOutputStream.reset();
        OtaFixture.fillStreamWithJunk(byteArrayOutputStream, 10);
        codeBlock.addCodeData(byteArrayOutputStream.toByteArray());
        assertEquals(10, codeBlock.byteArrayOutputStream.size());
        assertEquals(address + 9, codeBlock.lastAddressOfData());
        codeBlock.fillByteArrayWithFFToAddress(address + 20);
        assertEquals(20, codeBlock.byteArrayOutputStream.size());
    }

    @Test
    public void testAddCodeDataWithGapFilling() throws IOException {
        int address = 0x380000;
        codeBlock = new CodeBlock(address);
        byteArrayOutputStream.reset();
        OtaFixture.fillStreamWithJunk(byteArrayOutputStream, 10);
        codeBlock.addCodeData(byteArrayOutputStream.toByteArray());
        codeBlock.addCodeData(address + 20, byteArrayOutputStream.toByteArray());
        assertEquals(30, codeBlock.byteArrayOutputStream.size());
    }

    @Test
    public void testLastAddressOfBlock() throws IOException {
        int address = 0x380000;
        codeBlock = new CodeBlock(address);
        assertEquals(0x3803ff, codeBlock.lastAddressOfBlock());
    }

    @Test
    public void testAvailableSpace() throws IOException {
        int address = 0x380000;
        codeBlock = new CodeBlock(address);
        byteArrayOutputStream.reset();
        OtaFixture.fillStreamWithJunk(byteArrayOutputStream, 10);
        codeBlock.addCodeData(byteArrayOutputStream.toByteArray());
        assertEquals(1014, codeBlock.availableSpace());
    }

    @Test
    public void testEEPromCodeMapsWithNoData() {
        codeBlock = new CodeBlock(0x1000);
        int codeMap[] = codeBlock.codeMaps();
        assertEquals(1, codeMap.length);
        assertEquals(0, codeMap[0]);
    }

    @Test
    public void testEEPromCodeMapsWithBoundaryData() throws IOException {
        codeBlock = OtaFixture.codeBlockForAddress(0x0401, CodeBlock.MAX_CODEBLOCK_SIZE);
        int codeMap[] = codeBlock.codeMaps();
        assertFalse(codeBlock.isFlashBlock());
        assertEquals(1, codeMap.length);
        assertEquals(2, codeMap[0]);
    }

    @Test
    public void testEEPromCodeMapsWithData2() throws IOException {
        codeBlock = OtaFixture.codeBlockForAddress(0x0401, CodeBlock.MAX_CODEBLOCK_SIZE - 2);
        int codeMap[] = codeBlock.codeMaps();
        assertEquals(1, codeMap.length);
        assertEquals(2, codeMap[0]);
    }

    @Test
    public void testEEPromCodeMapsWithData1() throws IOException {
        codeBlock = OtaFixture.codeBlockForAddress(0x000, CodeBlock.MAX_CODEBLOCK_SIZE - 2);
        int codeMap[] = codeBlock.codeMaps();
        assertEquals(1, codeMap.length);
        assertEquals(1, codeMap[0]);
    }

    @Test
    public void testFlashCodeMapsWithBoundaryData2() throws IOException {
        codeBlock = OtaFixture.codeBlockForAddress(0x381801, CodeBlock.MAX_CODEBLOCK_SIZE);
        int codeMap[] = codeBlock.codeMaps();
        assertEquals(1, codeMap.length);
        assertEquals(0x40, codeMap[0]);
    }

    @Test
    public void testFlashCodeMapsWithBoundaryData1() throws IOException {
        codeBlock = OtaFixture.codeBlockForAddress(0x381C01, CodeBlock.MAX_CODEBLOCK_SIZE);
        int codeMap[] = codeBlock.codeMaps();
        assertEquals(1, codeMap.length);
        assertEquals(0x80, codeMap[0]);
    }

    @Test
    public void testFlashCodeMapsWithNoBoundaryData2() throws IOException {
        codeBlock = OtaFixture.codeBlockForAddress(0x381C01, CodeBlock.MAX_CODEBLOCK_SIZE - 2);
        int codeMap[] = codeBlock.codeMaps();
        assertEquals(1, codeMap.length);
        assertEquals(0x80, codeMap[0]);
    }

    @Test
    public void testFlashCodeMapsWithNoBoundaryData1() throws IOException {
        codeBlock = OtaFixture.codeBlockForAddress(ADDRESS, CodeBlock.MAX_CODEBLOCK_SIZE - 2);
        int codeMap[] = codeBlock.codeMaps();
        assertEquals(1, codeMap.length);
        assertEquals(0x01, codeMap[0]);
    }

    @Test
    public void testFlashCodeMapsWithNoData() {
        int codeMap[] = codeBlock.codeMaps();
        assertEquals(1, codeMap.length);
        assertEquals(0, codeMap[0]);
    }

    @Test
    public void testNormalizeAddress() {
        assertEquals(0, codeBlock.normalizedAddress(ADDRESS));
        codeBlock = new CodeBlock(0x380001);
        assertEquals(1, codeBlock.normalizedAddress(0x380001));
        codeBlock = new CodeBlock(0x382001);
        assertEquals(1, codeBlock.normalizedAddress(0x382001));
    }

    @Test
    public void testStartingByteOfFlashCodeMap() {
        codeBlock = new CodeBlock(ADDRESS);
        assertEquals(0, codeBlock.startingByteOfCodeMap());
        codeBlock = new CodeBlock(0x384000);
        assertEquals(2, codeBlock.startingByteOfCodeMap());
    }

    @Test
    public void testAddUnalignedDataSpanningBoundary() throws IOException {
        int address = 0x380001;
        codeBlock = new CodeBlock(address);
        OtaFixture.fillStreamWithJunk(byteArrayOutputStream, CodeBlock.MAX_CODEBLOCK_SIZE);
        assertEquals(CodeBlock.MAX_CODEBLOCK_SIZE - 1, codeBlock.addCodeData(byteArrayOutputStream.toByteArray()));
    }

    @Test
    public void testAddCodeDataMoreThanCapacity2() throws IOException {
        assertEquals(1023, codeBlock.addCodeData(OtaFixture.byteArrayWithJunk(CodeBlock.MAX_CODEBLOCK_SIZE - 1)));
        byteArrayOutputStream.reset();
        byteArrayOutputStream.write(0xff);
        OtaFixture.fillStreamWithJunk(byteArrayOutputStream, 10);
        assertEquals(1, codeBlock.addCodeData(byteArrayOutputStream.toByteArray()));
        assertEquals((byte)0xff, codeBlock.codeData()[codeBlock.codeData().length - 1]);
    }

    @Test
    public void testAddCodeDataMoreThanCapacity() throws IOException {
        int consumed;
        OtaFixture.fillStreamWithJunk(byteArrayOutputStream, CodeBlock.MAX_CODEBLOCK_SIZE);
        byteArrayOutputStream.write(0xff);
        assertEquals(1024, consumed = codeBlock.addCodeData(byteArrayOutputStream.toByteArray()));
        assertEquals((byte)0xff, byteArrayOutputStream.toByteArray()[consumed]);
    }

    @Test
    public void testAddCodeDataEqualToCapacity() throws IOException {
        OtaFixture.fillStreamWithJunk(byteArrayOutputStream, CodeBlock.MAX_CODEBLOCK_SIZE - 1);
        byteArrayOutputStream.write(0xff);
        assertEquals(1024, codeBlock.addCodeData(byteArrayOutputStream.toByteArray()));
    }

    @Test
    public void testAddCodeDataLessThanCapacity() throws IOException {
        OtaFixture.fillStreamWithJunk(byteArrayOutputStream, CodeBlock.MAX_CODEBLOCK_SIZE - 2);
        byteArrayOutputStream.write(0xff);
        assertEquals(1023, codeBlock.addCodeData(byteArrayOutputStream.toByteArray()));
    }

    @Test
    public void testLastAddress() throws IOException {
        byte[] bytes = HexStringDecoder.hexStringAsBytes(BINARY_DATA);
        assertEquals((BINARY_DATA.length() / 2), codeBlock.addCodeData(bytes));
        assertEquals(ADDRESS + (BINARY_DATA.length() / 2) - 1, codeBlock.lastAddressOfData());
    }
}
