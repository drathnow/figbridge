package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ConfigureControlTest extends BaseTestCase {
        
    @Test
    public void shouldCalculateConsistentSize() throws Exception {
        FieldType fieldType = new FieldType("IOPointClass", 8, FieldDataType.S8);
        FieldTypeLibrary fieldTypeLibrary = mock(FieldTypeLibrary.class);
        given(fieldTypeLibrary.fieldTypeForName("IOPointClass")).willReturn(fieldType);
        Field<?> f1 = Field.fieldForFieldTypeAndValue(fieldType, 100);
        List<Field<?>> fields = new ArrayList<>();
        fields.add(f1);
        Action action = new Action(ActionType.ADD, fields);
        List<Action> actions = new ArrayList<>();
        actions.add(action);
        ConfigureControl control = new ConfigureControl(1111L, ObjectType.IO_POINT, actions);
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        control.serialize(byteBuffer);
        assertEquals(control.size().intValue(), byteBuffer.position());
    }
}
