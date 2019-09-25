package zedi.pacbridge.gdn.otad;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;


public class CodeMapTest extends BaseTestCase {

    private static final int START_ADDRESS = 380000;
    private static final int BIT1 = 1;


    /**
     * Start address = 10000
     * 
     * CodeMap: 10101010 01010000
     *          0xaa     0x50
     */
    @Test
    public void testLastAddressFromStartingAddress() throws Exception {
        CodeMap codeMap = new CodeMap();
        BitFieldArray array = new BitFieldArray();
        array.addBitField(new BitField(0x00));
        codeMap.setMapBytes(array.asByteArray());
        
        int resultingAddress = codeMap.lastAddressFromStartingAddress(START_ADDRESS);
        int expectedAddress = START_ADDRESS + (7 * CodeBlock.MAX_CODEBLOCK_SIZE);
        
        String msg = "expected <0x" 
            + Integer.toHexString(expectedAddress)
            + "> but was <0x"
            + Integer.toHexString(resultingAddress)
            + ">";
        assertEquals(msg, expectedAddress, resultingAddress);

        array.addBitField(new BitField(0x00));
        codeMap.setMapBytes(array.asByteArray());

        resultingAddress = codeMap.lastAddressFromStartingAddress(START_ADDRESS);
        expectedAddress = START_ADDRESS + ((7 * CodeBlock.MAX_CODEBLOCK_SIZE) * 2);
        
        msg = "expected <0x" 
            + Integer.toHexString(expectedAddress)
            + "> but was <0x"
            + Integer.toHexString(resultingAddress)
            + ">";
        assertEquals(msg, expectedAddress, resultingAddress);
    }
    
   /**
     * Start address = 10000
     * 
     * CodeMap: 10101010 01010000
     *          0xaa     0x50
     */    
    @Test
    public void testFirstMissingAddress() throws Exception {
        CodeMap codeMap = new CodeMap();
        BitFieldArray array = new BitFieldArray();
        array.addBitField(new BitField(0xaa));
        array.addBitField(new BitField(0x50));
        codeMap.setMapBytes(array.asByteArray());
        
        int address = codeMap.firstMissingAddressesFromStartingAddress(START_ADDRESS);
        
        assertEquals(address, new Integer(START_ADDRESS + (BIT1 * 1024)).intValue());
    }

    @Test
    public void testSerialize() throws Exception {
        CodeMap codeMap = new CodeMap();
        codeMap.setMapBytes(new byte[]{0x01, 0x02, 0x03});
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutput dataOutput = new DataOutputStream(outputStream);
        
        codeMap.serialize(dataOutput);
        
        assertEquals(3, outputStream.toByteArray().length);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        DataInput dataInput = new DataInputStream(inputStream);
        
        assertEquals(1, dataInput.readUnsignedByte());
        assertEquals(2, dataInput.readUnsignedByte());
        assertEquals(3, dataInput.readUnsignedByte());
    }
    
    @Test
    public void testCodeMapForByteArray() throws Exception {
        byte[] bytes = new byte[]{0x01, 0x02, 0x03};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        DataInput dataInput = new DataInputStream(inputStream);
        
        CodeMap codeMap = new CodeMap();
        codeMap.deserialize(dataInput);
        
        assertEquals(3, codeMap.getLength());
        assertEquals(1, codeMap.bitFieldArray.getByteNumber(0));
        assertEquals(2, codeMap.bitFieldArray.getByteNumber(1));
        assertEquals(3, codeMap.bitFieldArray.getByteNumber(2));
    }
}
