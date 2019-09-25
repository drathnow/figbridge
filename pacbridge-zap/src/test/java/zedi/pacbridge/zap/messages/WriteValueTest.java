package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import zedi.pacbridge.utl.HexStringEncoder;
import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.values.ZapDataType;
import zedi.pacbridge.zap.values.ZapFloat;
import zedi.pacbridge.zap.values.ZapString;

public class WriteValueTest {

    private static final Long IOID = 42L;
    private static final ZapFloat FLOAT_VALUE = new ZapFloat(1.3f);
    private static final ZapString STRING_VALUE = new ZapString("herm3");

    @Test
    public void shouldSerializeString() {
        byte[] bytes = new byte[32];
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        WriteValue writeValue = new WriteValue(IOID, STRING_VALUE);
        //writeValue.serialize(byteBuffer);
        List<WriteValue> values = new ArrayList<>();
        values.add(writeValue);
        WriteIoPointsControl control = new WriteIoPointsControl(values, IOID);
        
        ZapPacketHeader hdr = new SessionHeader(ZapMessageType.WriteIOPoints);
        ZapPacket pkt = new ZapPacket(hdr, control);
        pkt.serialize(byteBuffer);
        System.out.println("Hex: " + HexStringEncoder.bytesAsHexString(bytes, byteBuffer.position()));
//        byteBuffer.flip();
//        assertEquals(IOID.intValue(), Unsigned.getUnsignedInt(byteBuffer));
//        assertEquals(ZapDataType.Float.getNumber().byteValue(), Unsigned.getUnsignedByte(byteBuffer));
//        assertEquals(FLOAT_VALUE.getValue().floatValue(), byteBuffer.getFloat(), 0.01);
    }
        
    @Test
    public void shouldSerializeFloat() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(32);
        WriteValue writeValue = new WriteValue(IOID, FLOAT_VALUE);
        writeValue.serialize(byteBuffer);
        byteBuffer.flip();
        
        assertEquals(IOID.intValue(), Unsigned.getUnsignedInt(byteBuffer));
        assertEquals(ZapDataType.Float.getNumber().byteValue(), Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(FLOAT_VALUE.getValue().floatValue(), byteBuffer.getFloat(), 0.01);
    }
    
    @Test
    public void shouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(32);
        byteBuffer.putInt(IOID.intValue());
        byteBuffer.put(ZapDataType.Float.getNumber().byteValue());
        FLOAT_VALUE.serialize(byteBuffer);
        byteBuffer.flip();
        
        WriteValue writeValue = WriteValue.writeValueFromByteBuffer(byteBuffer);
        
        assertEquals(IOID, writeValue.getIoId());
        assertEquals(ZapDataType.Float, writeValue.getValue().dataType());
        assertEquals(FLOAT_VALUE.getValue().floatValue(), ((ZapFloat)writeValue.getValue()).getValue().floatValue(), 0.1);
    }
}
