package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.io.Unsigned;


public class DeleteIoPointControl extends GdnMessageBase implements GdnMessage, Control, Serializable {
    static final long serialVersionUID = 1001;
    public static final Integer SIZE = 3;

    private Integer pollSetNumber;
    private Integer index;

    public DeleteIoPointControl(Integer pollSetNumber, Integer index) {
        super(GdnMessageType.DeleteIoPoint);
        this.pollSetNumber = pollSetNumber;
        this.index = index;
    }

    public Integer getPollSetNumber() {
        return pollSetNumber;
    }

    public Integer getIndex() {
        return index;
    }

    public Long getEventId() {
        return 0L;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(pollSetNumber.byteValue());
        byteBuffer.putShort(index.shortValue());
    }

    @Override
    public Integer size() {
        return SIZE;
    }

    public static DeleteIoPointControl deleteIoPointControlFromByteBuffer(ByteBuffer byteBuffer) {
        Integer pollSetNumber = (int)Unsigned.getUnsignedByte(byteBuffer);
        Integer index = Unsigned.getUnsignedShort(byteBuffer);
        return new DeleteIoPointControl(pollSetNumber, index);
    }
}
