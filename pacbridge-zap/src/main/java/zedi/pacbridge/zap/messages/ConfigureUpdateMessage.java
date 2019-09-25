package zedi.pacbridge.zap.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.ZapMessageType;

public class ConfigureUpdateMessage  extends ZapMessage implements Control, Serializable {
	private static final long serialVersionUID = 1001L;

    private static final int FIXED_SIZE = 3;

	private ObjectType objectType;
	private List<Action> actions;
	
	public ConfigureUpdateMessage(ObjectType objectType, List<Action> actions) {
	    super(ZapMessageType.ConfigureUpdate);
	    this.objectType = objectType;
	    this.actions = actions;
    }
	
	public ObjectType getObjectTyp() {
	    return objectType;
    }
	
	public List<Action> getActions() {
        return actions;
    }
	
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putInt(1);
        byteBuffer.put(objectType.getNumber().byteValue());
        byteBuffer.putShort((short)actions.size());
        for (Action action : actions)
            action.serialize(byteBuffer);
    }

    @Override
    public Integer size() {
        int total = FIXED_SIZE;
        for (Action action : actions)
            total += action.size();
        return total;
    }

    @Override
    public Long getEventId() {
        return null;
    }

    public static final ConfigureUpdateMessage configureUpdateMessageFromByteBuffer(ByteBuffer byteBuffer, FieldTypeLibrary library) {
        int version = byteBuffer.get();
        ObjectType objectType = ObjectType.objectTypeForNumber((int)byteBuffer.get());
        int count = Unsigned.getUnsignedShort(byteBuffer);
        List<Action> actions = new ArrayList<Action>();
        for (int i = 0; i < count; i++)
            actions.add(Action.actionFromByteBuffer(byteBuffer, library));
        return new ConfigureUpdateMessage(objectType, actions);
    }
}
