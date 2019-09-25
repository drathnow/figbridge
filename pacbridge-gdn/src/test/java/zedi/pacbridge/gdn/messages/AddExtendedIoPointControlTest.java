package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.junit.Test;

import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringDecoder;
import zedi.pacbridge.utl.io.Unsigned;


public class AddExtendedIoPointControlTest extends BaseTestCase{

    private static final String LE_HEX_MESSAGE = "00 02 00 08 01 02 01 00 40 9C 01 00 00 00 00 00 00 00 00 40 00 00 00 00";
        
    private static final Integer VERSION = 0; 
    private static final Integer INDEX = 2;
    private static final GdnDataType DATA_TYPE = GdnDataType.Float;
    private static final Integer POLLSET_NUMBER = 1; 
    private static final GdnIoPointClass IO_CLASS = GdnIoPointClass.Rtu;
    private static final Integer ADDRESS = 1;
    private static final Integer F1 = 40000;
    private static final Integer F2 = 1;
    private static final Integer F3 = 0; 
    private static final Integer F4 = 0;
    private static final Float FACTOR = 2.0f;
    private static final Float OFFSET = 0.0f;
    
    
    @Test
    public void testMessageNumber() throws Exception {
        AddExtendedIoPointControl message = messageForTest();
        assertEquals(GdnMessageType.AddExtendedIoPoint, message.messageType());
    }
    
    @Test
    public void testComparseBytes() throws Exception {
        byte[] bytesFromThePAC = HexStringDecoder.hexStringAsBytes(LE_HEX_MESSAGE);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytesFromThePAC).order(ByteOrder.LITTLE_ENDIAN);

        AddExtendedIoPointControl message = messageForTest();

        message.serialize(byteBuffer);
        
        byteBuffer.flip();
        byte[] myBytes = new byte[byteBuffer.limit()];
        byteBuffer.get(myBytes);
        
        assertTrue(Arrays.equals(bytesFromThePAC, myBytes));
    }
    
    @Test
    public void testDeserialize() throws Exception {
        byte[] bytes = HexStringDecoder.hexStringAsBytes(LE_HEX_MESSAGE);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        
        AddExtendedIoPointControl message = AddExtendedIoPointControl.addExtendedIoPointControlFromByteBuffer(byteBuffer);
        
        assertEquals(INDEX, message.getIndex());
        assertEquals(DATA_TYPE, message.getDataType());
        assertEquals(POLLSET_NUMBER, message.getPollSetNumber()); 
        assertEquals(GdnIoPointClass.Rtu, message.getIoPointClass());
        assertEquals(ADDRESS, message.getRtuAddress());
        assertEquals(F1, message.getF1());
        assertEquals(F2, message.getF2());
        assertEquals(F3, message.getF3()); 
        assertEquals(F4, message.getF4());
        assertEquals(FACTOR, message.getFactor().floatValue(), 0f);
        assertEquals(OFFSET, message.getOffset().floatValue(), 0f);
    }
    
    @Test
    public void shouldReturnCorrectSize() throws Exception {
        AddExtendedIoPointControl message = messageForTest();
        assertEquals(24, message.size().intValue());
    }
    
    @Test
    public void testSerialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        AddExtendedIoPointControl message = messageForTest();
        
        message.serialize(byteBuffer);
        
        byteBuffer.flip();
        assertEquals(VERSION.intValue(), Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(INDEX.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(DATA_TYPE.getNumber().byteValue(), Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(POLLSET_NUMBER.intValue(), Unsigned.getUnsignedByte(byteBuffer)); 
        assertEquals(GdnIoPointClass.Rtu.getNumber().intValue(), Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(ADDRESS.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(F1.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(F2.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(F3.intValue(), Unsigned.getUnsignedShort(byteBuffer)); 
        assertEquals(F4.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(FACTOR, byteBuffer.getFloat(), 0.1f);
        assertEquals(OFFSET, byteBuffer.getFloat(), 0.1f);
    }

    private AddExtendedIoPointControl messageForTest() {
        return new AddExtendedIoPointControl(DATA_TYPE, INDEX, POLLSET_NUMBER, ADDRESS, F1, F2, F3, F4, FACTOR, OFFSET, IO_CLASS);
    }
}