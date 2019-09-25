package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import zedi.pacbridge.net.Control;

public class SetEventsControl extends GdnMessageBase implements GdnMessage, Control, Serializable{
    public static final Integer FIXED_SIZE = 3;
    
    private Integer version = 0;
    private List<DeviceEvent> events = new ArrayList<DeviceEvent>();

    public SetEventsControl(List<DeviceEvent> events) {
        super(GdnMessageType.SetEvents);
        this.events = events;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(version.byteValue());
        byteBuffer.putShort((short)events.size());
        for (DeviceEvent event : events)
            event.serialize(byteBuffer);
    }

    @Override
    public Integer size() {
        return DeviceEvent.SIZE * events.size() + FIXED_SIZE;
    }

    public List<DeviceEvent> getEvents() {
        return new ArrayList<DeviceEvent>(events);
    }
    
    public Long getEventId() {
        return 0L;
    }
    
    public static SetEventsControl setEventsControlFromByteBuffer(ByteBuffer byteBuffer) {
        byteBuffer.get(); // Version is not used
        int count = byteBuffer.getShort();
        List<DeviceEvent> events = new ArrayList<DeviceEvent>();
        while (count-- > 0)
            events.add(DeviceEvent.deviceEventFromByteBuffer(byteBuffer));
        return new SetEventsControl(events);
    }

}
