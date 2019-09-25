package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

import org.json.JSONObject;

import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.ZapMessageType;

public class OtadStatusMessage extends ZapMessage {
	private static final long serialVersionUID = 1001L;

	public static final Integer VERSION1 = 1;
	
	private Integer version;
	private Long eventId;
	private OtadStatus otadStatusType;
	private String optionalData;
	
	public OtadStatusMessage(Long eventId, OtadStatus otadStatusType, String optionalData) {
		super(ZapMessageType.OtadStatusUpdate);
		this.version = VERSION1;
		this.eventId = eventId;
		this.otadStatusType = otadStatusType;
		this.optionalData = optionalData;
	}
	
	public Integer getVersion() {
		return version;
	}

	public Long getEventId() {
		return eventId;
	}

	public OtadStatus getOtadStatusType() {
		return otadStatusType;
	}

	public String getOptionalData() {
		return optionalData;
	}

	@Override
	public void serialize(ByteBuffer byteBuffer) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
	public Integer size() {
		return null;
	}
	
	@Override
	public String toString() {
	    JSONObject json = new JSONObject();
	    json.put("Version", version);
	    json.put("EventId", eventId);
	    json.put("Status", otadStatusType.getName());
	    json.put("OptionalData", optionalData == null ? "<none>" : optionalData);
	    return json.toString();
	}
	
	public static OtadStatusMessage messageFromByteBuffer(ByteBuffer byteBuffer) {
		byteBuffer.get();
		long eventId = byteBuffer.getLong();
		OtadStatus type = OtadStatus.otadStatusForNumber(byteBuffer.get());
		String optionalData = null;
	    if (byteBuffer.remaining() > 0) {
	        int length = Unsigned.getUnsignedByte(byteBuffer);
    	    if (length > 0) {
    	        byte[] bytes = new byte[length];
    	        byteBuffer.get(bytes);
    	        optionalData = new String(bytes);
	        }
	    }
		return new OtadStatusMessage(eventId, type, optionalData);
	}

}
