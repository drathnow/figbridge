package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ActionTest extends BaseTestCase {

    public static final String PARAMETER = "9600:N:1";
    public static final Integer PORT_NUBMER = 506;
    
    private static FieldType Parameter = new FieldType("Parameter", 1, FieldDataType.STRING);   
    private static FieldType Port = new FieldType("Port", 2, FieldDataType.S16);
    
    @Test
    public void shouldDeserializeFromByteBuffer() throws Exception {
        FieldTypeLibrary fieldTypeLibrary = mock(FieldTypeLibrary.class);
        
        given(fieldTypeLibrary.fieldTypeForTag(1)).willReturn(Parameter);
        given(fieldTypeLibrary.fieldTypeForTag(2)).willReturn(Port);
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        byteBuffer.put(ActionType.UPDATE.getNumber().byteValue());
        byteBuffer.putShort((short)2);

        short type = TypeNumberEncoder.encodedNumberFor(FieldDataType.STRING.getNumber(), Parameter.getNumber());
        byteBuffer.putShort(type);
        byteBuffer.putShort((short)PARAMETER.length());
        byteBuffer.put(PARAMETER.getBytes());
        
        type = TypeNumberEncoder.encodedNumberFor(FieldDataType.S16.getNumber(), Port.getNumber());
        byteBuffer.putShort(type);
        IntegerTypeSerializer serializer = new IntegerTypeSerializer();
        serializer.serialize(byteBuffer,  Port.getTag(), (long)PORT_NUBMER);
        byteBuffer.flip();
        
        Action action = Action.actionFromByteBuffer(byteBuffer, fieldTypeLibrary);
        assertEquals(2, action.getFields().size());
        Field<?> field = action.getFields().get(0);
        assertEquals(Parameter, field.getFieldType());

        field = action.getFields().get(1);
        assertEquals(Port, field.getFieldType());
    }
    
}
