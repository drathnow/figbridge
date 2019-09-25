package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.Utilities;

public class ActionTypeTest extends BaseTestCase {
 
    @Test
    public void shouldReturnObjectForName() throws Exception {
        for (ActionType object : Utilities.listOfObjecType(ActionType.class))
            assertEquals(object, ActionType.actionTypeForName(object.getName()));
    }

    @Test
    public void shouldReturnObjectForNumber() throws Exception {
        for (ActionType object : Utilities.listOfObjecType(ActionType.class))
            assertEquals(object, ActionType.actionTypeForNumber(object.getNumber()));
    }

}
