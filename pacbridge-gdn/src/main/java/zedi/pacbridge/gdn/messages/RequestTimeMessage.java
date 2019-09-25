package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Date;

import zedi.pacbridge.utl.io.Unsigned;

public class RequestTimeMessage extends GdnMessageBase implements GdnMessage, Serializable{
    private static final long serialVersionUID = 1001L;

    public static final int COMMAND_REQUEST = 0;
    public static final int COMMAND_RESPONSE = 1;
    
    private boolean request;
    private Date clientTimestamp;
    private Date serverTimestamp;
    
    private RequestTimeMessage(boolean isRequest, Date clientTimeStamp, Date serverTimestamp) {
        super(GdnMessageType.RequestTime);
        this.clientTimestamp = clientTimeStamp;
        this.serverTimestamp = serverTimestamp;
        this.request = true;
    }

    public RequestTimeMessage(Date clientTimeStamp, Date serverTimestamp) {
        super(GdnMessageType.RequestTime);
        this.clientTimestamp = clientTimeStamp;
        this.serverTimestamp = serverTimestamp;
        this.request = false;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put((byte)(request ? COMMAND_REQUEST : COMMAND_RESPONSE));
        byteBuffer.putInt((int)(clientTimestamp.getTime()/1000));
        byteBuffer.putInt((int)(serverTimestamp.getTime()/1000));
    }

    public boolean isRequest() {
        return request;
    }

    public Date getClientTimestamp() {
        return clientTimestamp;
    }

    public Date getServerTimestamp() {
        return serverTimestamp;
    }
    
    public static final RequestTimeMessage RequestTimeMessageFromByteBuffer(ByteBuffer byteBuffer) {
        boolean request = byteBuffer.get() == COMMAND_REQUEST ? true : false; 
        Date clientTimestamp = new Date((long)Unsigned.getUnsignedInt(byteBuffer)*1000);
        Date serverTimestamp = new Date((long)Unsigned.getUnsignedInt(byteBuffer)*1000);
        return new RequestTimeMessage(request, clientTimestamp, serverTimestamp);
    }
}
