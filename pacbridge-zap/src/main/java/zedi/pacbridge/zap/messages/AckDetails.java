package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

import org.json.JSONObject;

import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.ZapSerializable;

public abstract class AckDetails implements ZapSerializable {
    
    private AckDetailsType type;
    
    protected AckDetails(AckDetailsType type) {
        this.type = type;
    }

    public abstract byte[] asBytes();
    public abstract JSONObject asJSONObject();

    @Override
    public Integer size() {
        return asBytes().length + 1;
    }
    
    public AckDetailsType type() {
        return type;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byte[] bytes = asBytes();
        byteBuffer.putShort((short)(bytes.length+1));
        byteBuffer.put(type.getNumber().byteValue());
        byteBuffer.put(asBytes());
    }

    public static final AckDetails ackDetailsFromByteBuffer(ByteBuffer byteBuffer) {
        Unsigned.getUnsignedShort(byteBuffer);
        int type = Unsigned.getUnsignedByte(byteBuffer);
        switch (type) {
            case AckDetailsType.BUNDLED_REPORT_ACK_NUMBER : 
                return BundledReportAckDetails.bundledReportAckFromByteBuffer(byteBuffer);
            case AckDetailsType.PROTOCOL_ERROR_NUMBER : 
                return ProtocolErrorDetails.protocolErrorDetailsFromByteBuffer(byteBuffer);
            case AckDetailsType.WRITE_IO_POINTS_NUMBER : 
                return WriteIoPointsControlAckDetails.writeIoPointsMessageAckDetailsFromByteBuffer(byteBuffer);
            case AckDetailsType.DEMAND_POLL_NUMBER : 
                return DemandPollControlAckDetails.demandPollControlAckDetailsFromByteBuffer(byteBuffer);
            case AckDetailsType.CONFIGURE_RESPONSE_NUMBER :
                return ConfigureResponseAckDetails.configureResponseAckDetailsFromByteBuffer(byteBuffer);
            case AckDetailsType.SCRUB_RESULT_NUMBER :
                return ScrubControlAckDetails.scrubResultsFromByteBuffer(byteBuffer);
            case AckDetailsType.OTAD_REQUEST_DETAILS_NUMBER:
                return OtadRequestAckDetails.detailsFromByteBuffer(byteBuffer);
        }
        throw new IllegalAccessError("Unsupported ACK Details type specified: " + type);
    }
}
