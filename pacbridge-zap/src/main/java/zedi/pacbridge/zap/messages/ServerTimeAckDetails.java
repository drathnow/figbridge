package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;
import java.util.Date;

import org.json.JSONObject;

import zedi.pacbridge.utl.Utilities;

public class ServerTimeAckDetails extends AckDetails {
    public static final Integer SIZE = 2*(Integer.SIZE/Byte.SIZE);
    private Integer deviceTime;
    private Integer serverTime;
    
    public ServerTimeAckDetails(Integer deviceTime, Integer serverTime) {
        super(AckDetailsType.ServerTime);
        this.deviceTime = deviceTime;
        this.serverTime = serverTime;
    }

    public Integer getDeviceTime() {
        return deviceTime;
    }
    
    public Integer getServerTime() {
        return serverTime;
    }
    
    @Override
    public byte[] asBytes() {
        byte[] bytes = new byte[SIZE];
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.putInt(deviceTime);
        byteBuffer.putInt(serverTime);
        return bytes;
    }
    
    @Override
    public JSONObject asJSONObject() {
        JSONObject details = new JSONObject();
        details.put("DeviceTime", Utilities.DateFormatter.format(new Date(deviceTime*1000L)));
        details.put("ServerTime", Utilities.DateFormatter.format(new Date(serverTime*1000L)));
        JSONObject json = new JSONObject();
        json.put(type().getName().replaceAll("\\s",""), new JSONObject(details));
        return json;
    }
    
    @Override
    public String toString() {
        return asJSONObject().toString();
    }
}
