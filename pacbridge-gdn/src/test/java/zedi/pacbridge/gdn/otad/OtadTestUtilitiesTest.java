package zedi.pacbridge.gdn.otad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;


public class OtadTestUtilitiesTest {
    
    @Test
    public void testAddressAs4ByteHexString() throws Exception {
        assertEquals("000400", OtadTestUtilities.addressAs6ByteHexString(1024));
        assertEquals("002800", OtadTestUtilities.addressAs6ByteHexString(10240));
    }

    @Test
    public void testDownloadImageWithNumberOfCodeBlocks() throws Exception {
        DownloadImage downloadImage = null;
        downloadImage = OtadTestUtilities.downloadImageWithNumberOfCodeBlocks(1);
        assertEquals(1, downloadImage.getCodeBlocks().size());
        downloadImage = OtadTestUtilities.downloadImageWithNumberOfCodeBlocks(3);
        assertEquals(3, downloadImage.getCodeBlocks().size());
        downloadImage = OtadTestUtilities.downloadImageWithNumberOfCodeBlocks(256);
        assertEquals(256, downloadImage.getCodeBlocks().size());
    }

    @Test
    public void testS2recordForAddressAndBytes() throws Exception {
        String s2Record = OtadTestUtilities.s2recordForAddressAndBytes(1024, OtadTestUtilities.bytesWithSomeJunk(36), 0, 36);
        try {
            new DownloadImage(new ByteArrayInputStream(s2Record.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testBunchOfS2RecordsFromStartingAddress() throws Exception {
        String bunchOfS2Records = OtadTestUtilities.bunchOfS2RecordsFromStartingAddress(1024);
        try {
            new DownloadImage(new ByteArrayInputStream(bunchOfS2Records.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}