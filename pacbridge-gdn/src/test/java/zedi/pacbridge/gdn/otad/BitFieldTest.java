package zedi.pacbridge.gdn.otad;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class BitFieldTest extends BaseTestCase {


    public static final int TEST_BYTE0 = 0x01;
    public static final int TEST_BYTE1 = 0x02;
    public static final int TEST_BYTE2 = 0x04;
    public static final int TEST_BYTE3 = 0x08;
    public static final int TEST_BYTE4 = 0x10;
    public static final int TEST_BYTE5 = 0x20;
    public static final int TEST_BYTE6 = 0x40;
    public static final int TEST_BYTE7 = 0x80;

    // CodeMap: 10101010 
    @Test
    public void testByte() {
        BitField bitField = new BitField(0xaa);
        
        assertFalse(bitField.isBitSet(0));
        assertTrue(bitField.isBitSet(1));
        assertFalse(bitField.isBitSet(2));
        assertTrue(bitField.isBitSet(3));
        assertFalse(bitField.isBitSet(4));
        assertTrue(bitField.isBitSet(5));
        assertFalse(bitField.isBitSet(6));
        assertTrue(bitField.isBitSet(7));
        
    }
    
    @Test
    public void testSetBit() {
        BitField bitField = new BitField();
        
        bitField.setBit(0);
        assertEquals(1, bitField.theByte);

        bitField.setBit(1);
        assertEquals(3, bitField.theByte);

        bitField.setBit(5);
        assertEquals(35, bitField.theByte);
    }
    
    @Test
    public void testClearBit() throws Exception {
        BitField bitField = new BitField();
        
        bitField.setBit(5);
        assertEquals(32, bitField.theByte);

        bitField.clearBit(5);
        assertEquals(0, bitField.theByte);
        
    }
    
    @Test
    public void testIsBit() {
        BitField bitField = new BitField();
        
        bitField.setBit(0);
        assertTrue(bitField.isBitSet(0));
        assertFalse(bitField.isBitCleared(0));

        bitField.setBit(0);
        assertTrue(bitField.isBitSet(0));
        assertFalse(bitField.isBitCleared(0));

        assertFalse(bitField.isBitSet(7));
        assertTrue(bitField.isBitCleared(7));

        bitField.setBit(7);
        assertTrue(bitField.isBitSet(7));
        assertFalse(bitField.isBitCleared(0));
    }

}
