package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.Utilities;

public class ObjectTypeTest extends BaseTestCase {

    @Test
    public void shouldReturnObjectForName() throws Exception {
        for (ObjectType object : Utilities.listOfObjecType(ObjectType.class))
            assertEquals(object, ObjectType.objectTypeForName(object.getName()));
    }

    @Test
    public void shouldReturnObjectForNumber() throws Exception {
        for (ObjectType object : Utilities.listOfObjecType(ObjectType.class))
            assertEquals(object, ObjectType.objectTypeForNumber(object.getNumber()));
    }
}
