package zedi.pacbridge.gdn.otad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;


public class S0RecordTest {

    protected static final String S0RECORD_VERSION1 = "S00D00005645523236304231303694";
    protected static final String S0RECORD_VERSION2 = "S01100003256455231303630423031323334CC";
    protected static final String BAD_S0RECORD= "S0080000464F4F313233333416";
    protected static final String VERSION1_STRING = "VER260B106";
    protected static final String VERSION2_STRING = "2VER1060B01234";
    

    @Test
    public void testUknownVersion() throws Exception {
        S0Record record = new S0Record(BAD_S0RECORD);
        assertEquals(0, record.getAddress());
        assertTrue(record.getFormattedVersionString().startsWith(S0Record.UNKNOWN_VERSION_FORMAT_ERROR));
    }
    
    @Test
    public void testParseVersion2Encoding() throws Exception {
        S0Record record = new S0Record(S0RECORD_VERSION2);
        assertEquals(0, record.getAddress());
        assertEquals(VERSION2_STRING, record.getRawVersionString());
        assertEquals("V10.60 Build 01234", record.getFormattedVersionString());
        assertEquals(1060, record.getVersionNumber());
        assertEquals(1234, record.getBuildNumber());
    }
    
    @Test
    public void testGetFormattedVersionString() throws Exception {
        S0Record record = new S0Record(S0RECORD_VERSION1);
        assertEquals(0, record.getAddress());
        assertEquals("V2.60 Build 00106", record.getFormattedVersionString());
        assertEquals(260, record.getVersionNumber());
        assertEquals(106, record.getBuildNumber());
    }
    
    @Test
    public void testGetRawVersionString() throws Exception {
        S0Record record = new S0Record(S0RECORD_VERSION1);
        assertEquals(0, record.getAddress());
        assertEquals(VERSION1_STRING, record.getRawVersionString());
    }
        
    @Test
    public void testParseWithBadChecksum() throws Exception {
        String badCheckSumString = S0RECORD_VERSION1.substring(0, S0RECORD_VERSION1.length() - 2) + "00";
        try {
            new S0Record(badCheckSumString);
            fail();
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("Invalid checksum found in S2 record"));
        }
    }
    
    @Test
    public void testParse() throws Exception {
        S0Record record = new S0Record(S0RECORD_VERSION1);
        assertEquals(0, record.getAddress());
        assertEquals(VERSION1_STRING, new String(record.getData()));
    }
}
