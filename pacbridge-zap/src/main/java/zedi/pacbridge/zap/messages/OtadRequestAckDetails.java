package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

import org.json.JSONObject;

import zedi.pacbridge.utl.io.Unsigned;


public class OtadRequestAckDetails extends AckDetails {
    public static final String SUCCESS = "Success";
    public static final String FAILURE = "Failed";

    private String statusMessage;
    private boolean successful;

    private OtadRequestAckDetails(boolean successful, String statusMessage) {
        super(AckDetailsType.DemandPoll);
        this.statusMessage = statusMessage;
        this.successful = successful;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public boolean isSuccessful() {
        return successful;
    }
    
    @Override
    public JSONObject asJSONObject() {
        JSONObject details = new JSONObject();
        details.put("Status", successful ? SUCCESS : FAILURE);
        if (statusMessage != null)
            details.put("Message", statusMessage);
        JSONObject json = new JSONObject();
        json.put(type().getName().replaceAll("\\s",""), new JSONObject(details));
        return json;
    }
    
    @Override
    public String toString() {
        return asJSONObject().toString();
    }

    public static OtadRequestAckDetails detailsFromByteBuffer(ByteBuffer byteBuffer) {
        String statusMessage = null;
        int status = byteBuffer.get();
        int size = Unsigned.getUnsignedShort(byteBuffer);
        if (size > 0) {
            byte[] bytes = new byte[size];
            byteBuffer.get(bytes);
            statusMessage = new String(bytes);
        }
        return new OtadRequestAckDetails(status == 0, statusMessage);
    }

    @Override
    public byte[] asBytes() {
        return null;
    }

}
