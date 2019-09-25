package zedi.pacbridge.gdn.messages.otad;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.net.Control;

public class RequestSystemInfoCommand extends OtadMessage implements Control, Serializable {
    private static final long serialVersionUID = 1001L;
    public static final Integer SIZE = 3;
    
    public RequestSystemInfoCommand() {
        super(new OtadMessageHeader(true, OtadMessageType.RequestSystemInfo));
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        messageHeader.serialize(byteBuffer);
        byteBuffer.putShort(SIZE.shortValue());
    }

    @Override
    public Integer size() {
        // TODO Auto-generated method stub
        return 0;
    }

    public static RequestSystemInfoCommand requestSystemInfoCommandFromByteBuffer(ByteBuffer byteBuffer) {
        return new RequestSystemInfoCommand();
    }

    public Long getEventId() {
        return 0L;
    }
    
}
