package zedi.pacbridge.zap.messages;

import java.io.Serializable;

import zedi.pacbridge.utl.NamedType;


public class AckDetailsType extends NamedType implements Serializable {
    private static final long serialVersionUID = 1001L;
    
    static final int BUNDLED_REPORT_ACK_NUMBER = 1;
    static final int PROTOCOL_ERROR_NUMBER = 2;
    static final int WRITE_IO_POINTS_NUMBER = 4;
    static final int DEMAND_POLL_NUMBER = 5;
    static final int SERVER_TIME_NUMBER = 6;
    static final int CONFIGURE_RESPONSE_NUMBER = 7;
    static final int SCRUB_RESULT_NUMBER = 8;
    static final int CONFIGURE_UPDATE_RESPONSE_NUMBER = 9;
    static final int OTAD_REQUEST_DETAILS_NUMBER = 10;

    public static final AckDetailsType BundledReportAck = new AckDetailsType("Bundled Report ACK", BUNDLED_REPORT_ACK_NUMBER);
    public static final AckDetailsType ProtocolError = new AckDetailsType("Protocol Error", PROTOCOL_ERROR_NUMBER);
    public static final AckDetailsType WriteIoPoints = new AckDetailsType("Write IO Points", WRITE_IO_POINTS_NUMBER);
    public static final AckDetailsType DemandPoll = new AckDetailsType("Demand Poll", DEMAND_POLL_NUMBER);
    public static final AckDetailsType ServerTime = new AckDetailsType("Server Time", SERVER_TIME_NUMBER);
    public static final AckDetailsType ConfigureResponse = new AckDetailsType("Configure Response", CONFIGURE_RESPONSE_NUMBER);
    public static final AckDetailsType ScrubResult = new AckDetailsType("Scrub Result", SCRUB_RESULT_NUMBER);
    public static final AckDetailsType ConfigureUpdateResponse = new AckDetailsType("Configure Update Response", CONFIGURE_UPDATE_RESPONSE_NUMBER);
    public static final AckDetailsType OtadRequest = new AckDetailsType("OTAD Request", OTAD_REQUEST_DETAILS_NUMBER);

    private AckDetailsType(String name, Integer number) {
        super(name, number);
    }

    public static AckDetailsType ackDetailsTypeForNumber(Integer number) {
        switch (number) {
            case BUNDLED_REPORT_ACK_NUMBER :
                return BundledReportAck;
            case PROTOCOL_ERROR_NUMBER :
                return ProtocolError;
            case WRITE_IO_POINTS_NUMBER :
                return WriteIoPoints;
            case DEMAND_POLL_NUMBER :
                return DemandPoll;
            case SERVER_TIME_NUMBER :
                return ServerTime;
            case CONFIGURE_RESPONSE_NUMBER :
                return ConfigureResponse;
            case SCRUB_RESULT_NUMBER :
                return ScrubResult;
            case CONFIGURE_UPDATE_RESPONSE_NUMBER :
                return ConfigureUpdateResponse;
            case OTAD_REQUEST_DETAILS_NUMBER :
                return OtadRequest;
        }
        throw new IllegalArgumentException("Unknown ACK Details Type: " + number);
    }
}
