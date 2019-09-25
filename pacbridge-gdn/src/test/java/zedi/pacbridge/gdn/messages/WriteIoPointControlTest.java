package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.gdn.GdnFloat;
import zedi.pacbridge.gdn.GdnUnsignedLong;
import zedi.pacbridge.gdn.GdnValue;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.io.Unsigned;


public class WriteIoPointControlTest extends BaseTestCase {

    protected static final int PAC_INTERNAL_DATATYPE = GdnDataType.NUMBER_FOR_TYPE_FLOAT | 0x80;
    protected static final float TEST_FLOAT = (float)3.99393;
    protected static final int TEST_ULONG = 1068161250;
    protected static final int INDEX = 2;
    protected static final byte[] WRITE_CONTROL = new byte[]{0x04, 0x00, 0x01, 0x00, 0x0f};
    protected ByteArrayOutputStream byteArrayOutputStream;
    protected DataOutputStream dataOutputStream;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        byteArrayOutputStream = new ByteArrayOutputStream();
        dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    }

    @Test
    public void shouldSerializeUnsignedLongToByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        WriteIoPointControl writeIoPointControl = new WriteIoPointControl(INDEX, new GdnUnsignedLong((long)TEST_ULONG));
        writeIoPointControl.serialize(byteBuffer);

        byteBuffer.flip();
        assertEquals(GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_LONG, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(INDEX, Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(TEST_ULONG, Unsigned.getUnsignedInt(byteBuffer));
    }

    @Test
    public void testWriteFloat() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        GdnValue<?> gdnValue = new GdnFloat(TEST_FLOAT);
        WriteIoPointControl writeIoPointControl = new WriteIoPointControl(INDEX, gdnValue);
        writeIoPointControl.setIoPointClass(GdnIoPointClass.IoBoard);
        writeIoPointControl.serialize(byteBuffer);
        byteBuffer.flip();
        
        assertEquals(PAC_INTERNAL_DATATYPE, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(INDEX, Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(TEST_FLOAT, byteBuffer.getFloat(), 0.001);
    }

    @Test
    public void testWriteInternal() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);

        WriteIoPointControl writeIoPointControl = new WriteIoPointControl(INDEX, new GdnFloat(TEST_FLOAT));
        writeIoPointControl.serialize(byteBuffer);
        
        byteBuffer.flip();
        assertEquals(GdnDataType.NUMBER_FOR_TYPE_FLOAT, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(INDEX, Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(TEST_FLOAT, byteBuffer.getFloat(), 0.001);

    }

    @Test
    public void shouldDeserializeFromByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(WRITE_CONTROL);
        byteBuffer.flip();

        WriteIoPointControl writeIoPointControl = WriteIoPointControl.writeIoPointControlFromByteBuffer(byteBuffer);
        assertEquals(1, writeIoPointControl.getIndex().intValue());
        assertEquals(4, writeIoPointControl.getValue().dataType().getNumber().intValue());
        assertEquals(15, ((Number)writeIoPointControl.getValue().getValue()).intValue());
        assertEquals(GdnDataType.Integer, writeIoPointControl.getDataType());
    }
    
    @Test
    public void testDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put(WRITE_CONTROL);
        byteBuffer.flip();

        WriteIoPointControl writeIoPointControl = WriteIoPointControl.writeIoPointControlFromByteBuffer(byteBuffer);
        assertEquals(1, writeIoPointControl.getIndex().intValue());
        assertEquals(4, writeIoPointControl.getValue().dataType().getNumber().intValue());
        assertEquals(15, ((Number)writeIoPointControl.getValue().getValue()).intValue());
        assertEquals(GdnDataType.Integer, writeIoPointControl.getDataType());
    }
}
