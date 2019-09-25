package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

import org.json.JSONArray;
import org.json.JSONObject;

import zedi.pacbridge.utl.io.Unsigned;

public class ScrubControlAckDetails extends AckDetails {
    public static final Integer SIZE = 4;
    public static final Integer VERSION1 = 1;
    
    public static final Integer MSG_ERR_SCRUB_IO_POINTS = 0x0001;    // Scrub IO Points failed
    public static final Integer MSG_ERR_SCRUB_REPORTS = 0x0002;      // Scrub stored reports failed
    public static final Integer MSG_ERR_SCRUB_EVENTS = 0x0004;       // Scrub scheduled events
    public static final Integer MSG_ERR_SCRUB_PORTS = 0x0008;        // Scrub PortManager failed
    public static final Integer MSG_ERR_SCRUB_DEVICE = 0x0010;       // Scrub Device Manager failed;

    private Integer status;
    private Integer scrubFailures;
    
    public ScrubControlAckDetails(Integer status, Integer scrubFailures) {
        super(AckDetailsType.ScrubResult);
        this.status = status;
        this.scrubFailures = scrubFailures;
    }

    public boolean isSuccessful() {
        return status == 0;
    }
    
    public boolean didIoPointsFail() {
        return (MSG_ERR_SCRUB_IO_POINTS.intValue() & scrubFailures.intValue()) != 0;
    }
    
    public boolean didEventsFail() {
        return (MSG_ERR_SCRUB_EVENTS.intValue() & scrubFailures.intValue()) != 0;
    }

    public boolean didPortsFail() {
        return (MSG_ERR_SCRUB_PORTS.intValue() & scrubFailures.intValue()) != 0;
    }

    public boolean didDeviceFail() {
        return (MSG_ERR_SCRUB_DEVICE.intValue() & scrubFailures.intValue()) != 0;
    }

    public boolean didReportsFail() {
        return (MSG_ERR_SCRUB_REPORTS.intValue() & scrubFailures.intValue()) != 0;
    }

    public String getStatusMessage() {
        if (isSuccessful())
            return "Success";
        else {
            StringBuilder stringBuilder = new StringBuilder();
            if (didIoPointsFail())
                stringBuilder.append("IOPoints, ");
            if (didEventsFail())
                stringBuilder.append("Events, ");
            if (didPortsFail())
                stringBuilder.append("Ports, ");
            if (didDeviceFail())
                stringBuilder.append("Devices, ");
            if (didReportsFail())
                stringBuilder.append("Reports, ");
            stringBuilder.setLength(stringBuilder.length()-2);
            return stringBuilder.toString();
        }
    }
    
    @Override
    public byte[] asBytes() {
        byte[] bytes = new byte[SIZE];
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.put((byte)AckDetailsType.SCRUB_RESULT_NUMBER);
        byteBuffer.put(status.byteValue());
        byteBuffer.putShort(scrubFailures.shortValue());
        return bytes;
    }

    @Override
    public JSONObject asJSONObject() {
        JSONObject details = new JSONObject();
        details.put("Status", status == 0 ? "Success" : "Failure");
        if (status > 0) {
            JSONArray failures = new JSONArray(); 
            if (didIoPointsFail())
                failures.put("IOPoints");
            if (didEventsFail())
                failures.put("Events");
            if (didPortsFail())
                failures.put("Ports");
            if (didReportsFail())
                failures.put("Reports");
            if (didDeviceFail())
                failures.put("Devices");
            details.put("Failures", failures);
        }
        JSONObject json = new JSONObject();
        json.put(type().getName().replaceAll("\\s",""), details);
        return json;
    }

    public static AckDetails scrubResultsFromByteBuffer(ByteBuffer byteBuffer) {
        Integer status = (int)Unsigned.getUnsignedByte(byteBuffer);
        Integer failResults = Unsigned.getUnsignedShort(byteBuffer);
        return new ScrubControlAckDetails(status, failResults);
    }

}
