package zedi.pacbridge.eventgen.util;

import java.util.ArrayList;
import java.util.List;

import zedi.pacbridge.zap.messages.Action;
import zedi.pacbridge.zap.messages.ActionType;
import zedi.pacbridge.zap.messages.Field;
import zedi.pacbridge.zap.messages.FieldType;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;

public abstract class BaseEventGenerator {

    private FieldTypeLibrary typeLibrary;
    
    protected BaseEventGenerator(FieldTypeLibrary typeLibrary) {
        this.typeLibrary = typeLibrary;
    }

    protected Field<?> fieldForFieldNameAndValue(String fieldName, Object value) {
        FieldType type = typeLibrary.fieldTypeForName(fieldName);
        if (type == null)
            throw new RuntimeException("Unknown field name: " + fieldName);
        return Field.fieldForFieldTypeAndValue(type, value);
    }

    protected List<Field<?>> deleteFields(Integer ioId) {
        List<Field<?>> fields = new ArrayList<>();
        fields.add(fieldForFieldNameAndValue("CorrelationId",  StaticEventGenerator.nextCorrelationId()));
        fields.add(fieldForFieldNameAndValue("Id", ioId));
        return fields;
    }
    
    protected Action deleteAction(Integer ioId) {
        return new Action(ActionType.DELETE, deleteFields(ioId));
    }

}
