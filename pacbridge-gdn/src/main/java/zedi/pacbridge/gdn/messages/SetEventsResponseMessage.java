package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import zedi.pacbridge.gdn.PacEventStatus;
import zedi.pacbridge.utl.io.Unsigned;


public class SetEventsResponseMessage extends GdnMessageBase implements GdnMessage, Serializable {
    static final long serialVersionUID = 1001;

    private Map<Integer, PacEventStatus> eventStatusMap = new TreeMap<Integer, PacEventStatus>();
    private boolean containsFailures;

    private SetEventsResponseMessage() { 
        super(GdnMessageType.SetEventsResponse);
    }
    
    public SetEventsResponseMessage(Map<Integer, PacEventStatus> eventStatuses) {
        this();
        for (PacEventStatus eventStatus: eventStatuses.values())
            containsFailures |= eventStatus.isSuccess() == false;
    }
    
    @Override
    public GdnMessageType messageType() {
        return GdnMessageType.SetEventsResponse;
    }
    
    public boolean containsFailures() {
        return containsFailures;
    }

    public List<Integer> getIndexes() {
        return new ArrayList<Integer>(eventStatusMap.keySet());
    }

    public PacEventStatus eventStatusForIndex(int index) {
        return eventStatusMap.get(index);
    }
    
    public List<Integer> failedIndexes() {
        List<Integer> failures = new ArrayList<Integer>();
        for (int index : eventStatusMap.keySet()) {
            PacEventStatus eventStatus = eventStatusMap.get(index);
            if (eventStatus.isSuccess() == false)
                failures.add(index);
        }
        return failures;
    }

    public List<Integer> successfulIndexes() {
        List<Integer> successes = new ArrayList<Integer>();
        for (int index : eventStatusMap.keySet()) {
            PacEventStatus eventStatus = eventStatusMap.get(index);
            if (eventStatus.isSuccess())
                successes.add(index);
        }
        return successes;
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('{');
        if (eventStatusMap.size() > 0) {
            for (int index : eventStatusMap.keySet()) {
                PacEventStatus eventStatus = eventStatusMap.get(index);
                stringBuffer.append('(');
                stringBuffer.append(index).append(", ");
                stringBuffer.append(eventStatus.toString()).append("), ");
            }
            stringBuffer.setLength(stringBuffer.length() - 2);
        }
        stringBuffer.append('}');
        return stringBuffer.toString();
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)eventStatusMap.size());
        for (int index : eventStatusMap.keySet()) {
            PacEventStatus eventStatus = eventStatusMap.get(index);
            byteBuffer.putShort((short)index);
            byteBuffer.put((byte)eventStatus.getStatusNumber());
        }
    }

    private void deserialize(ByteBuffer byteBuffer) {
        byteBuffer.get(); // Version is not used
        int count = Unsigned.getUnsignedShort(byteBuffer);
        for (int i = 0; i < count; i++) {
            int index = Unsigned.getUnsignedShort(byteBuffer);
            int status = Unsigned.getUnsignedByte(byteBuffer);
            PacEventStatus eventStatus = PacEventStatus.eventStatusForStatusNumber(status);
            eventStatusMap.put(index, eventStatus);
            containsFailures |= eventStatus.isSuccess() == false;
        }
    }
    
    public static SetEventsResponseMessage setEventsResponseMessageFromByteBuffer(ByteBuffer byteBuffer) {
        SetEventsResponseMessage message = new SetEventsResponseMessage();
        message.deserialize(byteBuffer);
        return message;
    }
}
