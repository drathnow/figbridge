package zedi.pacbridge.gdn.otad;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;


public class CodeBlockMapTest extends BaseTestCase {

    public static final int TEST_BYTE0 = 0x01;
    public static final int TEST_BYTE1 = 0x02;
    public static final int TEST_BYTE2 = 0x04;
    public static final int TEST_BYTE3 = 0x08;
    public static final int TEST_BYTE4 = 0x10;
    public static final int TEST_BYTE5 = 0x20;
    public static final int TEST_BYTE6 = 0x40;
    public static final int TEST_BYTE7 = 0x80;

    private CodeBlockMap codeMap;
    
    @Test
    public void testCreateEmpty() {
        codeMap = new CodeBlockMap();
        assertEquals(0, codeMap.blocks.size());
        assertEquals(0, codeMap.firstBlock());
    }

    @Test
    public void testSetBitInByte() {
        codeMap = new CodeBlockMap();
        codeMap.setBit0(true, 1);
        assertTrue(codeMap.isBit0Set(1));
    }

    @Test
    public void testSetBitsWithMask() {
        codeMap = new CodeBlockMap();
        assertFalse(codeMap.isBit0Set());
        assertFalse(codeMap.isBit7Set());
        codeMap.setBit0(true);
        codeMap.setBitsInByte(0x80, true, 0);
        assertTrue(codeMap.isBit0Set());
        assertTrue(codeMap.isBit7Set());
    }

    @Test
    public void testIsSet() {
        codeMap = new CodeBlockMap(TEST_BYTE0);
        assertTrue(codeMap.isBit0Set());
        codeMap = new CodeBlockMap(TEST_BYTE1);
        assertTrue(codeMap.isBit1Set());
        codeMap = new CodeBlockMap(TEST_BYTE2);
        assertTrue(codeMap.isBit2Set());
        codeMap = new CodeBlockMap(TEST_BYTE3);
        assertTrue(codeMap.isBit3Set());
        codeMap = new CodeBlockMap(TEST_BYTE4);
        assertTrue(codeMap.isBit4Set());
        codeMap = new CodeBlockMap(TEST_BYTE5);
        assertTrue(codeMap.isBit5Set());
        codeMap = new CodeBlockMap(TEST_BYTE6);
        assertTrue(codeMap.isBit6Set());
        codeMap = new CodeBlockMap(TEST_BYTE7);
        assertTrue(codeMap.isBit7Set());
    }

    @Test
    public void testSetOn() {
        codeMap = new CodeBlockMap();
        assertFalse(codeMap.isBit0Set());
        codeMap.setBit0(true);
        assertTrue(codeMap.isBit0Set());

        assertFalse(codeMap.isBit1Set());
        codeMap.setBit1(true);
        assertTrue(codeMap.isBit1Set());

        assertFalse(codeMap.isBit2Set());
        codeMap.setBit2(true);
        assertTrue(codeMap.isBit2Set());

        assertFalse(codeMap.isBit3Set());
        codeMap.setBit3(true);
        assertTrue(codeMap.isBit3Set());

        assertFalse(codeMap.isBit4Set());
        codeMap.setBit4(true);
        assertTrue(codeMap.isBit4Set());

        assertFalse(codeMap.isBit5Set());
        codeMap.setBit5(true);
        assertTrue(codeMap.isBit5Set());

        assertFalse(codeMap.isBit6Set());
        codeMap.setBit6(true);
        assertTrue(codeMap.isBit6Set());

        assertFalse(codeMap.isBit7Set());
        codeMap.setBit7(true);
        assertTrue(codeMap.isBit7Set());
    }

    @Test
    public void testSetOff() {
        codeMap = new CodeBlockMap();
        codeMap.setBit0(true);
        assertTrue(codeMap.isBit0Set());
        codeMap.setBit0(false);
        assertFalse(codeMap.isBit0Set());
    }

    @Test
    public void testClone() {
        CodeBlockMap aBlockMap = new CodeBlockMap();
        aBlockMap.setBit0(true);
        CodeBlockMap anotherBlockMap = (CodeBlockMap)aBlockMap.clone();
        assertTrue(anotherBlockMap.isBit0Set());
        anotherBlockMap.setBit1(true);
        assertFalse(aBlockMap.isBit1Set());
    }
}
