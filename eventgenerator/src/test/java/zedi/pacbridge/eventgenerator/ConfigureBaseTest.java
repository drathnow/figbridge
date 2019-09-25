package zedi.pacbridge.eventgenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Element;

import zedi.pacbridge.app.events.zios.ConfigureEvent;
import zedi.pacbridge.app.events.zios.ConfigureResponseEvent;
import zedi.pacbridge.eventgen.EventPublisher;
import zedi.pacbridge.eventgen.Main;
import zedi.pacbridge.eventgen.util.StaticEventGenerator;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.zap.messages.Action;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.FieldType;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.ObjectType;

public class ConfigureBaseTest extends JmsPublisherBaseTest {
    protected FieldTypeLibrary typeLibrary;
    protected NuidSiteAddress siteAddress = new NuidSiteAddress("Daver1", 0);
    private EventPublisher eventPublisher;    

    @Override
    public void setUp() throws Exception {
        super.setUp();
        typeLibrary = injector().getInstance(FieldTypeLibrary.class);
        eventPublisher = injector().getInstance(EventPublisher.class);
    }
    
    protected Field<?> fieldForFieldNameAndValue(String fieldName, Object value) {
        FieldType type = typeLibrary.fieldTypeForName(fieldName);
        if (type == null)
            throw new RuntimeException("Unknown field name: " + fieldName);
        return Field.fieldForFieldTypeAndValue(type, value);
    }
    
    protected Field<?> fieldWithNameFromAction(String fieldName, Action action) {
        for (Field<?> field : action.getFields()) {
            if (field.getFieldType().getName().equalsIgnoreCase(fieldName))
                return field;
        }
        return null;
    }
    
    protected Long correlationIdFromAction(Action action) {
        Field<?> field = fieldWithNameFromAction("CorrelationId", action);
        return (Long)(field == null ? null : field.getValue());
    }
    
    protected List<Field<?>> removeFieldWithNameFromFieldList(String fieldName, List<Field<?>> fieldList) {
        for (Iterator<Field<?>> iter = fieldList.iterator(); iter.hasNext(); ) {
            Field<?> field = iter.next();
            if (field.getFieldType().getName().equals(fieldName)) {
                iter.remove();
                return fieldList;
            }
        }
        return fieldList;
    }

    protected List<Field<?>> replaceFieldInList(Field<?> field, List<Field<?>> fields) {
        List<Field<?>> newList = removeFieldWithNameFromFieldList(field.getFieldType().getName(), fields);
        newList.add(field);
        return newList;
    }
    
    protected void testSuccessfulAddAction(List<Field<?>> fields, ObjectType objectType) throws Exception {        
        String errMsg = "Successful ADD action test: " + objectType;
        List<Action> actions = new ArrayList<>();
        Action addAction = new Action(ActionType.ADD, fields);
        actions.add(addAction);
        Long correlationId = correlationIdFromAction(addAction);
        ConfigureResponseEvent event = waitForConfigureResponse(addAction, objectType);
        assertEquals(errMsg, objectType, event.getObjectType());
        assertEquals(errMsg, 1, event.getActions().size());
        assertEquals(errMsg, correlationId, correlationIdFromAction(event.getActions().get(0)));
        assertNotNull(errMsg, fieldWithNameFromAction("Id", event.getActions().get(0)));
        assertNotNull(errMsg, fieldWithNameFromAction("ErrorCode", event.getActions().get(0)));
        assertEquals(errMsg, 0, (long)fieldWithNameFromAction("ErrorCode", event.getActions().get(0)).getValue());
    }
    
    protected void testSuccessfulUpdateDeleteAction(List<Field<?>> fields, ActionType actionType, ObjectType objectType) throws Exception {        
        String errMsg = "Successful " + actionType.getName() + " action test: " + objectType;
        List<Action> actions = new ArrayList<>();
        Action addAction = new Action(actionType, fields);
        actions.add(addAction);
        Long correlationId = correlationIdFromAction(addAction);
        ConfigureResponseEvent event = waitForConfigureResponse(addAction, objectType);
        assertEquals(errMsg, objectType, event.getObjectType());
        assertEquals(errMsg, 1, event.getActions().size());
        assertEquals(errMsg, correlationId, correlationIdFromAction(event.getActions().get(0)));
        assertNotNull(errMsg, fieldWithNameFromAction("ErrorCode", event.getActions().get(0)));
        assertEquals(errMsg, 0, (long)fieldWithNameFromAction("ErrorCode", event.getActions().get(0)).getValue());
    }

    protected void testForMissingField(String missingFieldName, List<Field<?>> fields, ActionType actionType, ObjectType objectType) throws Exception {
        String errMsg = "Test for missing field: " + missingFieldName;
        List<Field<?>> myFields = removeFieldWithNameFromFieldList(missingFieldName, fields);
        List<Action> actions = new ArrayList<>();
        Action addAction = new Action(actionType, myFields);
        actions.add(addAction);
        Long correlationId = correlationIdFromAction(addAction);
        ConfigureResponseEvent event = waitForConfigureResponse(addAction, objectType);
        assertEquals(errMsg, objectType, event.getObjectType());
        assertEquals(errMsg, 1, event.getActions().size());
        assertEquals(errMsg, correlationId, correlationIdFromAction(event.getActions().get(0)));
        assertNotNull(errMsg, fieldWithNameFromAction("ErrorCode", event.getActions().get(0)));
        assertEquals(errMsg, 46, (long)fieldWithNameFromAction("ErrorCode", event.getActions().get(0)).getValue());
        assertNotNull(errMsg, fieldWithNameFromAction("Message", event.getActions().get(0)));
        assertEquals(errMsg, "Missing " + missingFieldName + " field", (String)fieldWithNameFromAction("Message", event.getActions().get(0)).getValue());
    }
    
    protected void testForInvalidValueField(String invalidFieldName, List<Field<?>> fields, ActionType actionType, ObjectType objectType) throws Exception {
        String errMsg = "Test for invalid field: " + invalidFieldName;
        List<Action> actions = new ArrayList<>();
        Action addAction = new Action(actionType, fields);
        actions.add(addAction);
        Long correlationId = correlationIdFromAction(addAction);
        ConfigureResponseEvent event = waitForConfigureResponse(addAction, objectType);
        assertEquals(errMsg, objectType, event.getObjectType());
        assertEquals(errMsg, 1, event.getActions().size());
        assertEquals(errMsg, correlationId, correlationIdFromAction(event.getActions().get(0)));
        assertNotNull(errMsg, fieldWithNameFromAction("ErrorCode", event.getActions().get(0)));
        assertEquals(errMsg, 49, (long)fieldWithNameFromAction("ErrorCode", event.getActions().get(0)).getValue());
        assertNotNull(errMsg, fieldWithNameFromAction("Message", event.getActions().get(0)));
        assertEquals(errMsg, "Invalid value for " + invalidFieldName, (String)fieldWithNameFromAction("Message", event.getActions().get(0)).getValue());
    }

    private ConfigureResponseEvent waitForConfigureResponse(Action action, ObjectType objectType) throws Exception {
        List<Action> actions = new ArrayList<>();
        actions.add(action);
        ConfigureEvent configureEvent = new ConfigureEvent(StaticEventGenerator.nextEventId(), siteAddress, objectType, actions);
        configureEvent.getEventId();
        listener.lock();
        eventPublisher.publishEvent(configureEvent, false);
        listener.waitForResponse(5, configureEvent.getEventId());
        listener.unlock();
        assertNotNull(listener.result());
        Element root = JDomUtilities.elementForXmlString(listener.result());
        return ConfigureResponseEvent.configureResponseEventForElement(root, typeLibrary);
    }
    
}
