package zedi.pacbridge.eventgenerator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import zedi.pacbridge.eventgen.util.StaticEventGenerator;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.ObjectType;

public class DeletePortsTest extends ConfigureBaseTest {
    
    @Test
    public void shouldHandleSuccessfulDelete() throws Exception {
        testSuccessfulUpdateDeleteAction(deleteFields(), ActionType.DELETE, ObjectType.EVENT);
    }
    
    @Test
    public void shouldHandleMissingId() throws Exception {
        testForMissingField("Id", deleteFields(), ActionType.DELETE, ObjectType.EVENT);
    }
    
    
    private List<Field<?>> deleteFields() {
        List<Field<?>> fields = new ArrayList<>();
        fields.add(fieldForFieldNameAndValue("CorrelationId",  StaticEventGenerator.nextCorrelationId()));
        fields.add(fieldForFieldNameAndValue("Id", 1));
        return fields;
    }
}
