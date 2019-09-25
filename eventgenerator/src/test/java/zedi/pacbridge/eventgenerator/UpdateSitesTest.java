package zedi.pacbridge.eventgenerator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import zedi.pacbridge.eventgen.util.StaticEventGenerator;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.ObjectType;


public class UpdateSitesTest extends ConfigureBaseTest {

    @Test
    public void shouldHandleSuccessfulAdd() throws Exception {
        testSuccessfulAddAction(addFields(), ObjectType.SITE);
    }

    @Test
    public void shouldHandleMissingId() throws Exception {
        testForMissingField("Id", addFields(), ActionType.UPDATE, ObjectType.SITE);
    }
    
    @Test
    public void shouldHandleMissingName() throws Exception {
        testForMissingField("Name", addFields(), ActionType.UPDATE, ObjectType.SITE);
    }
    
    private List<Field<?>> addFields() {
        List<Field<?>> fields = new ArrayList<>();
        fields.add(fieldForFieldNameAndValue("CorrelationId",  StaticEventGenerator.nextCorrelationId()));
        fields.add(fieldForFieldNameAndValue("Id", 100));
        fields.add(fieldForFieldNameAndValue("Name", "FooManChoo"));
        return fields;
    }
}
