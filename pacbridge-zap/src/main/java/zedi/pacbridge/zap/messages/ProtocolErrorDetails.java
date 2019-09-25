package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

import org.json.JSONObject;

public class ProtocolErrorDetails extends AckDetails {
    private ProtocolErrorType error;

    public ProtocolErrorDetails(ProtocolErrorType error) {
        super(AckDetailsType.ProtocolError);
        this.error = error;
    }

    public ProtocolErrorType protocolError() {
        return error;
    }
    
    @Override
    public Integer size() {
        return 4;
    }

    @Override
    public byte[] asBytes() {
        byte[] theBytes = new byte[4];
        ByteBuffer byteBuffer = ByteBuffer.wrap(theBytes);
        byteBuffer.putInt(error.getNumber().intValue());
        return theBytes;
    }
    
    @Override
    public JSONObject asJSONObject() {
        JSONObject details = new JSONObject();
        details.put("Type", error.getName());
        JSONObject json = new JSONObject();
        json.put(type().getName().replaceAll("\\s",""), new JSONObject(details));
        return json;
    }
    
    @Override
    public String toString() {
        return asJSONObject().toString();
    }
    
    public static final ProtocolErrorDetails protocolErrorDetailsFromByteBuffer(ByteBuffer byteBuffer) {
        Integer number = byteBuffer.getInt();
        ProtocolErrorType error = ProtocolErrorType.protocolErrorForNumber(number);
        return new ProtocolErrorDetails(error);
    }
}
