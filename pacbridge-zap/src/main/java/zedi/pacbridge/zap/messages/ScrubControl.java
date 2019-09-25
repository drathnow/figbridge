package zedi.pacbridge.zap.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.net.Control;
import zedi.pacbridge.zap.ZapMessageType;

public class ScrubControl extends ZapMessage implements Control, Serializable {
    private static final long serialVersionUID = 1001L;
    public static final Integer SIZE = 2;
    
    public static final Integer VERSION1 = 1;
    public static final Integer MSG_SCRUB_IO_POINTS = 0x0001;
    public static final Integer MSG_SCRUB_REPORTS = 0x0002;
    public static final Integer MSG_SCRUB_EVENTS = 0x0004;
    public static final Integer MSG_SCRUB_ALL = 0x0008;

    
    private Long eventId;
    private Integer scrubOptions;    
    
    public ScrubControl(Long eventId, Integer scrubOptions) {
        super(ZapMessageType.Scrub);
        this.eventId = eventId;
        this.scrubOptions = scrubOptions;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putShort(scrubOptions.shortValue());
    }

    @Override
    public Integer size() {
        return SIZE;
    }

    @Override
    public Long getEventId() {
        return eventId;
    } 
}
