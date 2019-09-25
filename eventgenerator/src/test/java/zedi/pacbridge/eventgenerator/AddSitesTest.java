package zedi.pacbridge.eventgenerator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import zedi.pacbridge.eventgen.util.StaticEventGenerator;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.ObjectType;


public class AddSitesTest extends ConfigureBaseTest {

    @Test
    public void shouldHandleSuccessfulAdd() throws Exception {
        testSuccessfulAddAction(addFields(), ObjectType.SITE);
    }

    @Test
    public void shouldHandleMissingName() throws Exception {
        testForMissingField("Name", addFields(), ActionType.ADD, ObjectType.SITE);
    }
    
    private List<Field<?>> addFields() {
        List<Field<?>> fields = new ArrayList<>();
        fields.add(fieldForFieldNameAndValue("CorrelationId",  StaticEventGenerator.nextCorrelationId()));
        fields.add(fieldForFieldNameAndValue("Name", "FooManChoo"));
        return fields;
    }
}
