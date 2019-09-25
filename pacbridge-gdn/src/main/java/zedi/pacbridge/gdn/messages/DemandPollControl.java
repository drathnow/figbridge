package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.io.Unsigned;

public class DemandPollControl extends GdnMessageBase implements GdnMessage, Control, Serializable {
    private static final long serialVersionUID = 1001L;
    public static final Integer SIZE = 3;
    
    private Integer index;
    private Integer pollsetNumber;
    
    public DemandPollControl(Integer index, Integer pollsetNumber) {
        super(GdnMessageType.DemandPoll);
        this.index = index;
        this.pollsetNumber = pollsetNumber;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(pollsetNumber.byteValue());
        byteBuffer.putShort(index.shortValue());
    }

    public Long getEventId() {
        return 0L;
    }
    
    public Integer getIndex() {
        return index;
    }
    
    public Integer getPollSetNumber() {
        return pollsetNumber;
    }
    
    @Override
    public Integer size() {
        return SIZE;
    }

    public static DemandPollControl demandPollControlFromByteBuffer(ByteBuffer byteBuffer) {
        Integer pollsetNumber = (int)Unsigned.getUnsignedByte(byteBuffer);
        Integer index = (int)Unsigned.getUnsignedShort(byteBuffer);
        return new DemandPollControl(index, pollsetNumber);
    }

}
