package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.utl.io.Unsigned;

public class AddStandardIoPointControlTest extends PointMessageTest {

    @Test
    public void shouldDeserializeToByteBufferForInternalIoPoint() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put((byte)POLLSETID.byteValue());
        byteBuffer.putShort((short)INDEX.shortValue());
        byteBuffer.putShort((short)ADDRESS.shortValue());
        byteBuffer.putShort((short)FIELD1.shortValue());
        byteBuffer.putShort((short)FIELD2.shortValue());
        byteBuffer.putShort((short)FIELD3.shortValue());
        byteBuffer.putShort((short)FIELD4.shortValue());
        byteBuffer.put((byte)(TYPE | GdnIoPointClass.INTERNAL_ATTRIBUTE_VALUE));
        
        byteBuffer.flip();
        AddStandardIoPointControl addPointMessage = AddStandardIoPointControl.addStandardIoPointControlFromByteBuffer(byteBuffer);
        assertEquals(14, addPointMessage.size().intValue());
        assertEquals(GdnMessageType.AddIoPointMessage, addPointMessage.messageType());

        assertEquals(POLLSETID, addPointMessage.getPollSetNumber());
        assertEquals(INDEX, addPointMessage.getIndex());
        assertEquals(ADDRESS, addPointMessage.getRtuAddress());
        assertEquals(FIELD1, addPointMessage.getF1());
        assertEquals(FIELD2, addPointMessage.getF2());
        assertEquals(FIELD3, addPointMessage.getF3());
        assertEquals(FIELD4, addPointMessage.getF4());
        assertEquals(GdnDataType.Binary, addPointMessage.getDataType());
        
    }
    
    @Test
    public void testDeserializeInputForInternalIOPoint() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        byteBuffer.put((byte)POLLSETID.byteValue());
        byteBuffer.putShort((short)INDEX.shortValue());
        byteBuffer.putShort((short)ADDRESS.shortValue());
        byteBuffer.putShort((short)FIELD1.shortValue());
        byteBuffer.putShort((short)FIELD2.shortValue());
        byteBuffer.putShort((short)FIELD3.shortValue());
        byteBuffer.putShort((short)FIELD4.shortValue());
        byteBuffer.put((byte)(TYPE | GdnIoPointClass.INTERNAL_ATTRIBUTE_VALUE));
        byteBuffer.flip();
        
        AddStandardIoPointControl addPointMessage = AddStandardIoPointControl.addStandardIoPointControlFromByteBuffer(byteBuffer);
        assertEquals(14, addPointMessage.size().intValue());

        assertEquals(POLLSETID, addPointMessage.getPollSetNumber());
        assertEquals(INDEX, addPointMessage.getIndex());
        assertEquals(ADDRESS, addPointMessage.getRtuAddress());
        assertEquals(FIELD1, addPointMessage.getF1());
        assertEquals(FIELD2, addPointMessage.getF2());
        assertEquals(FIELD3, addPointMessage.getF3());
        assertEquals(FIELD4, addPointMessage.getF4());
        assertEquals(GdnIoPointClass.IoBoard, addPointMessage.getIoPointClass());
        assertEquals(GdnDataType.Binary, addPointMessage.getDataType());
    }
    
    @Test
    public void testDeserializeInput() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        byteBuffer.put((byte)POLLSETID.byteValue());
        byteBuffer.putShort((short)INDEX.shortValue());
        byteBuffer.putShort((short)ADDRESS.shortValue());
        byteBuffer.putShort((short)FIELD1.shortValue());
        byteBuffer.putShort((short)FIELD2.shortValue());
        byteBuffer.putShort((short)FIELD3.shortValue());
        byteBuffer.putShort((short)FIELD4.shortValue());
        byteBuffer.put((byte)TYPE.byteValue());
        byteBuffer.flip();
        
        AddStandardIoPointControl addPointMessage = AddStandardIoPointControl.addStandardIoPointControlFromByteBuffer(byteBuffer);
        assertEquals(14, addPointMessage.size().intValue());
        
        assertEquals(POLLSETID, addPointMessage.getPollSetNumber());
        assertEquals(INDEX, addPointMessage.getIndex());
        assertEquals(ADDRESS, addPointMessage.getRtuAddress());
        assertEquals(FIELD1, addPointMessage.getF1());
        assertEquals(FIELD2, addPointMessage.getF2());
        assertEquals(FIELD3, addPointMessage.getF3());
        assertEquals(FIELD4, addPointMessage.getF4());
        assertEquals(GdnDataType.Binary, addPointMessage.getDataType());
    }

    @Test
    public void shouldSerializeInternalIOPointToByteBuffer() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        AddStandardIoPointControl addPointMessage = new AddStandardIoPointControl(GdnDataType.Binary, 
                                                                            INDEX, 
                                                                            POLLSETID, 
                                                                            ADDRESS, 
                                                                            FIELD1, 
                                                                            FIELD2, 
                                                                            FIELD3, 
                                                                            FIELD4, 
                                                                            GdnIoPointClass.IoBoard);

        addPointMessage.serialize(byteBuffer);
        byteBuffer.flip();

        assertEquals(POLLSETID.intValue(), Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(INDEX.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(ADDRESS.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(FIELD1.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(FIELD2.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(FIELD3.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(FIELD4.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(TYPE | GdnIoPointClass.INTERNAL_ATTRIBUTE_VALUE, Unsigned.getUnsignedByte(byteBuffer));
    }
    
    
    @Test
    public void testSerializeOutputWithInternalIOPoint() throws IOException {
        AddStandardIoPointControl addPointMessage = new AddStandardIoPointControl(GdnDataType.Binary, 
                                                                                    INDEX, 
                                                                                    POLLSETID, 
                                                                                    ADDRESS, 
                                                                                    FIELD1, 
                                                                                    FIELD2, 
                                                                                    FIELD3, 
                                                                                    FIELD4, 
                                                                                    GdnIoPointClass.IoBoard);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        addPointMessage.serialize(byteBuffer);
                
        byteBuffer.flip();
        assertEquals(POLLSETID.intValue(), Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(INDEX.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(ADDRESS.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(FIELD1.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(FIELD2.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(FIELD3.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(FIELD4.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(TYPE | GdnIoPointClass.INTERNAL_ATTRIBUTE_VALUE, Unsigned.getUnsignedByte(byteBuffer));
    }
    
    @Test
    public void testSerializeOutput() throws IOException {
        AddStandardIoPointControl addPointMessage = new AddStandardIoPointControl(GdnDataType.Binary, INDEX, POLLSETID, ADDRESS, FIELD1, FIELD2, FIELD3, FIELD4, null);
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        addPointMessage.serialize(byteBuffer);
        
        byteBuffer.flip();
        assertEquals(POLLSETID.intValue(), Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(INDEX.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(ADDRESS.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(FIELD1.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(FIELD2.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(FIELD3.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(FIELD4.intValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(TYPE.intValue(), Unsigned.getUnsignedByte(byteBuffer));
    }
}
