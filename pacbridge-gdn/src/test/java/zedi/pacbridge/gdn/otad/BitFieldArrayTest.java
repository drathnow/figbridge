package zedi.pacbridge.gdn.otad;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class BitFieldArrayTest extends BaseTestCase {

    private static final int BYTE_NUMBER = 2;
    private static final int BIT_NUMBER = 5;


    @Test
    public void testSetBitsWithMask() {
        BitFieldArray byteMapArray = new BitFieldArray();
        byteMapArray.setBitsInByte(0x01, true, 0);
        byteMapArray.setBitsInByte(0x80, true, 0);

        assertTrue(byteMapArray.isBitSetInByte(0, 0));
        assertTrue(byteMapArray.isBitSetInByte(7, 0));
    }
    
    @Test
    public void testAsByteArray() throws Exception {
        BitFieldArray byteMapArray = new BitFieldArray(4);
        
        ((BitField)byteMapArray.bitFields.get(0)).theByte = 0x0a;
        ((BitField)byteMapArray.bitFields.get(1)).theByte = 0x0b;
        ((BitField)byteMapArray.bitFields.get(2)).theByte = 0x0c;
        ((BitField)byteMapArray.bitFields.get(3)).theByte = 0x0d;
        
        byte[] bytes = byteMapArray.asByteArray();
        
        assertEquals(0x0a, bytes[0]);
        assertEquals(0x0b, bytes[1]);
        assertEquals(0x0c, bytes[2]);
        assertEquals(0x0d, bytes[3]);
    }
    
    @Test
    public void testSetBitInByte() {
        BitFieldArray byteMapArray = new BitFieldArray(4);
        
        byteMapArray.setBitInByte(BIT_NUMBER, BYTE_NUMBER);
        
        BitField byteMap = (BitField)byteMapArray.bitFields.get(BYTE_NUMBER);
        assertTrue(byteMap.isBitSet(BIT_NUMBER));

        byteMapArray.clearBitInByte(BIT_NUMBER, BYTE_NUMBER);
        
        byteMap = (BitField)byteMapArray.bitFields.get(BYTE_NUMBER);
        assertFalse(byteMap.isBitSet(BIT_NUMBER));
    }
}
