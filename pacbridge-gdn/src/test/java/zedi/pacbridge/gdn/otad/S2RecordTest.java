package zedi.pacbridge.gdn.otad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import zedi.pacbridge.utl.HexStringDecoder;



public class S2RecordTest {

    protected static final String S2RECORD1 = "S2243800003B3BCC001616CFC76C80260FCC99213BEC8416DFC21B8287C7201ECC000416CFB8";
    protected static final String S2RECORD1_DATA = "3B3BCC001616CFC76C80260FCC99213BEC8416DFC21B8287C7201ECC000416CF";

    
    @Test
    public void testParseWithBadChecksum() throws Exception {
        String badChecksumString = S2RECORD1.substring(0, S2RECORD1.length() - 2) + "00";
        try {
            new S2Record(badChecksumString);
            fail();
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("Invalid checksum found in S2 record"));
        }
    }
    
    @Test
    public void testParse() throws Exception {
        S2Record record = new S2Record(S2RECORD1);
        assertEquals(0x380000, record.getAddress());
        byte[] expectedBytes = HexStringDecoder.hexStringWithNoDelimiterAsBytes(S2RECORD1_DATA);
        assertTrue(Arrays.equals(expectedBytes, record.getData()));
    }
}
