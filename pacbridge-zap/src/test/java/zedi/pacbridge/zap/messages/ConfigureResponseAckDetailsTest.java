package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.nio.ByteBuffer;
import java.util.List;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringDecoder;

public class ConfigureResponseAckDetailsTest extends BaseTestCase {
    private static final byte[] ACK_BYTES = HexStringDecoder.hexStringAsBytes("00 00 00 00 00 00 07 d0 01 00 03 01 00 01 20 01 7f fe 02 00 01 10 02 7e 03 00 01 30 03 00 09 ff f1");
    private static final Long COMMAND_ID = 2000L;
    private static final Integer TAG1 = 1;
    private static final Integer TAG2 = 2;
    private static final Integer TAG3 = 3;
    private static final Long S8_VALUE = 126L;
    private static final Long S16_VALUE = 32766L;
    private static final Long S32_VALUE = 655345L;
    
    @Test
    @SuppressWarnings("unchecked")
    public void shouldDeserialize() throws Exception {
        FieldTypeLibrary fieldTypeLibrary = mock(FieldTypeLibrary.class);
        FieldType s8FieldType = mock(FieldType.class);
        FieldType s16FieldType = mock(FieldType.class);
        FieldType s32FieldType = mock(FieldType.class);
        ByteBuffer byteBuffer = ByteBuffer.wrap(ACK_BYTES);
        
        ConfigureResponseAckDetails details = ConfigureResponseAckDetails.configureResponseAckDetailsFromByteBuffer(byteBuffer);
        
        assertEquals(COMMAND_ID, details.getEventId());
        assertEquals(ObjectType.SITE, details.getObjectType());
        
        given(fieldTypeLibrary.fieldTypeForTag(TAG1)).willReturn(s8FieldType);
        given(s8FieldType.getDataType()).willReturn(FieldDataType.S8);
        given(s8FieldType.getTag()).willReturn(TAG1);        

        given(fieldTypeLibrary.fieldTypeForTag(TAG2)).willReturn(s16FieldType);
        given(s16FieldType.getDataType()).willReturn(FieldDataType.S16);
        given(s16FieldType.getTag()).willReturn(TAG2);

        given(fieldTypeLibrary.fieldTypeForTag(TAG3)).willReturn(s32FieldType);
        given(s32FieldType.getDataType()).willReturn(FieldDataType.S32);
        given(s32FieldType.getTag()).willReturn(TAG3);
        
        List<Action> actions = details.actionsUsingFieldTypeLibarary(fieldTypeLibrary);
        assertEquals(3, actions.size());
        Action action = actions.get(0);
        assertEquals(1, action.getFields().size());
        Field<Long> field = (Field<Long>)action.getFields().get(0);
        assertEquals(TAG1, field.getFieldType().getTag());
        assertEquals(S16_VALUE, field.getValue());
        
        action = actions.get(1);
        assertEquals(1, action.getFields().size());
        field = (Field<Long>)action.getFields().get(0);
        assertEquals(TAG2, field.getFieldType().getTag());
        assertEquals(S8_VALUE, field.getValue());

        action = actions.get(2);
        assertEquals(1, action.getFields().size());
        field = (Field<Long>)action.getFields().get(0);
        assertEquals(TAG3, field.getFieldType().getTag());
        assertEquals(S32_VALUE, field.getValue());
    }
}
