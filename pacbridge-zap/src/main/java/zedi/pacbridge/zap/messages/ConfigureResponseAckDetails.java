package zedi.pacbridge.zap.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import zedi.pacbridge.utl.io.Unsigned;


public class ConfigureResponseAckDetails extends AckDetails implements Serializable {

    static class FieldSummary<T> implements Serializable {
        private Integer tag;
        private T value;

        public Integer getTag() {
            return tag;
        }
        
        public T getValue() {
            return value;
        }
        
        FieldSummary(Integer tag, T value) {
            this.tag = tag;
            this.value = value;
        }
        
        JSONObject asJSONObject() {
            JSONObject obj = new JSONObject();
            obj.put("Tag", tag);
            obj.put("Value", value.toString());
            return obj;
        }
        
        public Field<?> asField(FieldTypeLibrary fieldTypeLibrary) {
            FieldType fieldType = fieldTypeLibrary.fieldTypeForTag(tag);
            return fieldType == null ? null : Field.fieldForFieldTypeAndValue(fieldType, value);
        }
    }
    
    public static class ActionSummary implements Serializable {
        private ActionType actionType;
        private List<FieldSummary<?>> fieldSummaries;
        
        ActionSummary() {
            this.fieldSummaries = new ArrayList<FieldSummary<?>>();
        }
        
        ActionType getActionType() {
            return actionType;
        }
        
        public List<FieldSummary<?>> getFieldSummaries() {
            return fieldSummaries;
        }
        
        public Action asAction(FieldTypeLibrary fieldTypeLibrary) {
            List<Field<?>> fields = new ArrayList<Field<?>>();
            for (FieldSummary<?> fieldSummary : fieldSummaries) {
                Field<?> field = fieldSummary.asField(fieldTypeLibrary);
                if (field != null)
                    fields.add(field);
            }
            return new Action(actionType, fields);
        }
        
        JSONObject asJSONObject() {
            JSONObject obj = new JSONObject();
            obj.put("ActionType", actionType.getName());
            JSONArray array = new JSONArray();
            for (FieldSummary<?> fieldSummary : fieldSummaries)
                array.put(fieldSummary.asJSONObject());
            obj.put("Fields", array);
            return obj;
        }
        
        void deserialize(ByteBuffer byteBuffer) {
            actionType = ActionType.actionTypeForNumber((int)Unsigned.getUnsignedByte(byteBuffer));
            int count = Unsigned.getUnsignedShort(byteBuffer);
            FieldSummary<?> fieldSummery = null;
            
            for (int i = 0; i < count; i++) {
                Integer foo = Unsigned.getUnsignedShort(byteBuffer);
                int tag = TypeNumberEncoder.tagNumberFromEncodedValue(foo.shortValue());
                int type = TypeNumberEncoder.typeNumberFromEncodedValue(foo.shortValue());
                FieldDataType fieldDataType = FieldDataType.fieldTypeForNumber(type);
                switch (type) {
                    case FieldDataType.S8_NUMBER :
                    case FieldDataType.S16_NUMBER :
                    case FieldDataType.S32_NUMBER :
                    case FieldDataType.S48_NUMBER :
                    case FieldDataType.S64_NUMBER :
                        Long number = Field.integerTypeDeserializer.deserialize(byteBuffer, fieldDataType);
                        fieldSummery = new FieldSummary<Long>(tag, number);
                        break;

                    case FieldDataType.F32_NUMBER :
                        Float fNumber = Field.f32TypeDeserializer.deserialize(byteBuffer, fieldDataType);
                        fieldSummery = new FieldSummary<Float>(tag, fNumber.floatValue());
                        break;

                    case FieldDataType.STRING_NUMBER :
                        String str = Field.stringTypeDeserializer.deserialize(byteBuffer, fieldDataType);
                        fieldSummery = new FieldSummary<String>(tag, str);
                        break;
                        
                    default:
                        throw new IllegalArgumentException("Unrecognized fieldDataType: " + fieldDataType.toString());
                }
                fieldSummaries.add(fieldSummery);
                fieldSummery = null;
            }
        }
    }
    
    private ObjectType objectType;
    private Long eventId;
    private List<ActionSummary> actionSummaries;

    private ConfigureResponseAckDetails(Long eventId, 
                                        ObjectType objectType, 
                                        List<ActionSummary> actionSummaries) {
        super(AckDetailsType.ConfigureResponse);
        this.objectType = objectType;
        this.eventId = eventId;
        this.actionSummaries = actionSummaries;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public Long getEventId() {
        return eventId;
    }

    public List<Action> actionsUsingFieldTypeLibarary(FieldTypeLibrary fieldTypeLibrary) {
        List<Action> actions = new ArrayList<Action>();
        for (ActionSummary summary : actionSummaries) 
            actions.add(summary.asAction(fieldTypeLibrary));
        return actions;
    }

    @Override
    public byte[] asBytes() {
        return null;
    }

    @Override
    public JSONObject asJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("EventId", eventId);
        obj.put("ObjecType", objectType.getName());
        JSONArray array = new JSONArray();
        for (ActionSummary actionSummary : actionSummaries)
                array.put(actionSummary.asJSONObject());
        obj.put("actions", array);
        return obj;
    }
    
    @Override
    public String toString() {
        return asJSONObject().toString();
    }

    public static ConfigureResponseAckDetails configureResponseAckDetailsFromByteBuffer(ByteBuffer byteBuffer) {
        List<ActionSummary> actionSummaries = new ArrayList<ConfigureResponseAckDetails.ActionSummary>(); 
        
        Long eventId = byteBuffer.getLong();
        ObjectType objectType = ObjectType.objectTypeForNumber((int)Unsigned.getUnsignedByte(byteBuffer));
        int count = Unsigned.getUnsignedShort(byteBuffer);
     
        while (count-- > 0) {
            ActionSummary actionSummary = new ActionSummary();
            actionSummary.deserialize(byteBuffer);
            actionSummaries.add(actionSummary);
        }
        return new ConfigureResponseAckDetails(eventId, objectType, actionSummaries);
    }
}
