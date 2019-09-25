package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.InOrder;

import zedi.pacbridge.utl.HexStringEncoder;
import zedi.pacbridge.zap.values.ZapDataType;
import zedi.pacbridge.zap.values.ZapFloat;
import zedi.pacbridge.zap.values.ZapShort;
import zedi.pacbridge.zap.values.ZapString;

public class WriteIoPointsControlTest {

    private static final Long EVENT_ID = 1233L;
    private static final Long IOID1 = 42L;
    private static final Long IOID2 = 43L;
    private static final ZapFloat VALUE1 = new ZapFloat(1.3f);
    private static final ZapShort VALUE2 = new ZapShort(242);
    private static final ZapString STRING_VALUE = new ZapString("herm3");
    
    @Test
    public void shouldReturnJSONObject() throws Exception {
        List<WriteValue> values = new ArrayList<>();
        values.add(new WriteValue(IOID1, VALUE1));
        values.add(new WriteValue(IOID2, VALUE2));

        WriteIoPointsControl control = new WriteIoPointsControl(values, EVENT_ID);
        assertEquals(EVENT_ID, control.getEventId());
        
        JSONObject json = (JSONObject)new JSONObject(control.toJSONString()).get(control.messageType().getName());
        JSONArray writeValues = json.getJSONArray("WriteValues");
        
        assertEquals(2, writeValues.length());
        
        JSONObject value = writeValues.getJSONObject(0);
        assertEquals(IOID1.intValue(), value.get("ioId"));
        assertEquals(VALUE1.toString(), value.get("value"));
        assertEquals(ZapDataType.Float.getName(), value.get("type"));
        
        value = writeValues.getJSONObject(1);
        assertEquals(IOID2.intValue(), value.get("ioId"));
        assertEquals(VALUE2.toString(), value.get("value"));
        assertEquals(ZapDataType.Integer.getName(), value.get("type"));
    }
    
    @Test
    public void shouldSerializeStringValue() throws Exception {
        List<WriteValue> values = new ArrayList<>();
        values.add(new WriteValue(IOID1, STRING_VALUE));

        WriteIoPointsControl control = new WriteIoPointsControl(values, EVENT_ID);
        byte[] bytes = new byte[100];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        control.serialize(buffer);
        System.out.println("Hex: " + HexStringEncoder.bytesAsHexString(bytes, buffer.position()));
    }
    
    @Test
    public void shouldSerialize() {
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        WriteValue writeValue1 = mock(WriteValue.class);
        WriteValue writeValue2 = mock(WriteValue.class);
        List<WriteValue> values = new ArrayList<>();
        values.add(writeValue1);
        values.add(writeValue2);
        
        InOrder inOrder = inOrder(byteBuffer, writeValue1, writeValue2);
        given(writeValue1.size()).willReturn(9);
        given(writeValue2.size()).willReturn(7);
        
        WriteIoPointsControl message = new WriteIoPointsControl(values, EVENT_ID);
        message.serialize(byteBuffer);
        
        inOrder.verify(byteBuffer).putShort((short)17);
        inOrder.verify(byteBuffer).put((byte)2);
        inOrder.verify(writeValue1).serialize(byteBuffer);
        inOrder.verify(writeValue2).serialize(byteBuffer);
    }

    @Test
    public void shouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(128);
        
        byteBuffer.putShort((short)17);
        byteBuffer.put((byte)2);
        byteBuffer.putInt(IOID1.intValue());
        byteBuffer.put(VALUE1.dataType().getNumber().byteValue());
        byteBuffer.putFloat(VALUE1.getValue().floatValue());

        byteBuffer.putInt(IOID2.intValue());
        byteBuffer.put(VALUE2.dataType().getNumber().byteValue());
        byteBuffer.putShort(VALUE2.getValue().shortValue());
        
        byteBuffer.flip();
        
        WriteIoPointsControl message = WriteIoPointsControl.messageFromByteBuffer(byteBuffer);
        
        assertEquals(2, message.getWriteValues().size());
        
        WriteValue writeValue = message.getWriteValues().get(0);
        assertTrue(writeValue.getValue() instanceof ZapFloat);
        assertEquals(IOID1, writeValue.getIoId());
        assertEquals(ZapDataType.Float, writeValue.getValue().dataType());
        assertEquals(VALUE1.getValue().floatValue(), ((ZapFloat)writeValue.getValue()).getValue().floatValue(), 0.1);
        
        writeValue = message.getWriteValues().get(1);
        assertTrue(writeValue.getValue() instanceof ZapShort);
        assertEquals(IOID2, writeValue.getIoId());
        assertEquals(ZapDataType.Integer, writeValue.getValue().dataType());
        assertEquals(VALUE2.getValue().shortValue(), ((ZapShort)writeValue.getValue()).getValue().shortValue());
}
    
}