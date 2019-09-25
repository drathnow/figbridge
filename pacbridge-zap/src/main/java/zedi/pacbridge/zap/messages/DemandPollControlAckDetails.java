package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

import org.json.JSONObject;


public class DemandPollControlAckDetails extends AckDetails {
    public static final String SUCCESS = "Success";
    public static final String INVALID_IO_POINT = "Invalid IOID";
    public static final String INVALID_POLLSET_NUMBER = "Invalid Pollset Number";

    private String statusMessage;
    private boolean successful;

    private DemandPollControlAckDetails(String statusMessage, boolean successful) {
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
        details.put("Status", successful ? "Success" : "Failed");
        details.put("Message", statusMessage);
        JSONObject json = new JSONObject();
        json.put(type().getName().replaceAll("\\s",""), new JSONObject(details));
        return json;
    }
    
    @Override
    public String toString() {
        return asJSONObject().toString();
    }

    public static DemandPollControlAckDetails demandPollControlAckDetailsFromByteBuffer(ByteBuffer byteBuffer) {
        int status = byteBuffer.get();
        switch (status) {
            case 0 :
                return new DemandPollControlAckDetails(SUCCESS, true);
            case 1 :
                return new DemandPollControlAckDetails(INVALID_IO_POINT, false);
            case 2 :
                return new DemandPollControlAckDetails(INVALID_POLLSET_NUMBER, false);
        }
        return new DemandPollControlAckDetails("Unknow status code: " + status, false);
    }

    @Override
    public byte[] asBytes() {
        return null;
    }

}
