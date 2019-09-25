package zedi.pacbridge.zap.messages;

import org.json.JSONObject;

import zedi.pacbridge.zap.reporting.ResponseStatus;

public class ConfigureUpdateAckDetails extends AckDetails {

    private ResponseStatus responseStatus;
    
    public ConfigureUpdateAckDetails(ResponseStatus responseStatus) {
        super(AckDetailsType.ConfigureUpdateResponse);
        this.responseStatus = responseStatus;
    }
    
    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    @Override
    public byte[] asBytes() {
        byte bytes[] = new byte[1];
        bytes[0] = responseStatus.getNumber().byteValue();
        return bytes;
    }

    @Override
    public JSONObject asJSONObject() {
        JSONObject json = new JSONObject();
        json.put("Type", type().getName());
        json.put("Status", responseStatus.getName());
        return json;
    }

}
