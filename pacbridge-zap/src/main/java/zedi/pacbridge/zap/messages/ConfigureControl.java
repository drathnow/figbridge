package zedi.pacbridge.zap.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.ZapMessageType;

public class ConfigureControl extends ZapMessage implements Control, Serializable {
    public static final String ROOT_ELEMENT_NAME = "Configure";
    public static final String OBJECT_TAG = "Object";
    
    private static final Integer FIXED_SIZE = 11;
    
    private Long eventId;
    private ObjectType objectType;
    private List<Action> actions;
    
    public ConfigureControl(Long eventId, ObjectType objectType, List<Action> actions) {
        super(ZapMessageType.Configure);
        this.eventId = eventId;
        this.objectType = objectType;
        this.actions = actions;
    }

    public Long getEventId() {
        return eventId;
    }
    
    /**
     * [objectType] - 1 byte
     * [actionCount] - 2 bytes
     * [action1]...[actionn]
     * 
     * @param byteBuffer
     */
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putLong(eventId.longValue());
        byteBuffer.put(objectType.getNumber().byteValue());
        byteBuffer.putShort((short)actions.size());
        for (Action action : actions)
            action.serialize(byteBuffer);
    }
    
    public List<Action> getActions() {
        return new ArrayList<Action>(actions);
    }
    
    @Override
    public Integer size() {
        int total = FIXED_SIZE;
        for (Action action : actions)
            total += action.size();
        return total;
    }

    public static final ConfigureControl configureControlFromByteBuffer(ByteBuffer byteBuffer, FieldTypeLibrary library) {
        Long eventId = byteBuffer.getLong();
        ObjectType objectType = ObjectType.objectTypeForNumber((int)byteBuffer.get());
        int count = Unsigned.getUnsignedShort(byteBuffer);
        List<Action> actions = new ArrayList<Action>();
        for (int i = 0; i < count; i++)
            actions.add(Action.actionFromByteBuffer(byteBuffer, library));
        return new ConfigureControl(eventId, objectType, actions);
    }
}
