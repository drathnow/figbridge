package zedi.pacbridge.zap;

import java.io.Serializable;

import zedi.pacbridge.net.MessageType;
import zedi.pacbridge.utl.NamedType;

public class ZapMessageType extends NamedType implements MessageType, Serializable {
	private static final long serialVersionUID = 1001L;
	
	public static final int ACK_MESSAGE_NUMBER = 65535;
    public static final int HEART_BEAT_MESSAGE_NUMBER = 65534;
    public static final int HEART_BEAT_RESPONSE_MESSAGE_NUMBER = 65533;
    public static final int REQUEST_TIME_MESSAGE_NUMBER = 65532;
    public static final int SERVER_TIME_MESSAGE_NUMBER = 65531;
    
    public static final int SERVER_CHALLENGE_MESSAGE_NUMBER = 1;
    public static final int CHALLENGE_RESPONSE_MESSAGE_NUMBER = 2;
    public static final int AUTHENTICATION_RESPONSE_MESSAGE_NUMBER = 3;
    public static final int BUNDLED_REPORT_NUMBER = 4;
    public static final int WRITE_IO_POINT_NUMBER = 5;
    public static final int DEMAND_POLL_NUMBER = 6;
    public static final int CONFIGURE_NUMBER = 7;
    public static final int CONFIGURE_RESPONSE_NUMBER = 8;
    public static final int SCRUB_NUMBER = 9;
    public static final int CHALLENGE_RESPONSE_MESSAGE_V2_NUMBER = 10;
    public static final int CONFIGURE_UPDATE_MESSAGE_NUMBER = 11;
    public static final int OTAD_REQUEST_NUMBER = 12;
    public static final int OTAD_STATUS_UPDATE_NUMBER = 13;
    
    public static final ZapMessageType ServerChallenge = new ZapMessageType("Server Challenge", SERVER_CHALLENGE_MESSAGE_NUMBER);
    public static final ZapMessageType ClientChallengeResponse = new ZapMessageType("Client Challenge Response Version 2", CHALLENGE_RESPONSE_MESSAGE_NUMBER);
    public static final ZapMessageType AuthenticationResponse = new ZapMessageType("Authentication Response", AUTHENTICATION_RESPONSE_MESSAGE_NUMBER);
    public static final ZapMessageType HeartBeat = new ZapMessageType("Heartbeat", HEART_BEAT_MESSAGE_NUMBER);
    public static final ZapMessageType HeartBeatResponse = new ZapMessageType("Heartbeat Response", HEART_BEAT_RESPONSE_MESSAGE_NUMBER);
    public static final ZapMessageType RequestTime = new ZapMessageType("Request Time", REQUEST_TIME_MESSAGE_NUMBER);
    public static final ZapMessageType ServerTime = new ZapMessageType("Server Time", SERVER_TIME_MESSAGE_NUMBER);
    public static final ZapMessageType Scrub = new ZapMessageType("Scrub", SCRUB_NUMBER);
    public static final ZapMessageType ChallengeResponseV2 = new ZapMessageType("Client Challenge Response Version 2", CHALLENGE_RESPONSE_MESSAGE_V2_NUMBER);
    public static final ZapMessageType ConfigureUpdate = new ZapMessageType("ConfigureUpdate", CONFIGURE_UPDATE_MESSAGE_NUMBER);
    public static final ZapMessageType OtadRequest = new ZapMessageType("OTAD Request", OTAD_REQUEST_NUMBER);
    public static final ZapMessageType OtadStatusUpdate = new ZapMessageType("OTAD Status Update", OTAD_STATUS_UPDATE_NUMBER);
    
    public static final ZapMessageType Acknowledgement = new ZapMessageType("Acknowledgement", ACK_MESSAGE_NUMBER);
    public static final ZapMessageType BundledReport = new ZapMessageType("Bundled Report", BUNDLED_REPORT_NUMBER);
    public static final ZapMessageType WriteIOPoints= new ZapMessageType("Write IO Points", WRITE_IO_POINT_NUMBER);
    public static final ZapMessageType DemandPoll= new ZapMessageType("Demand Poll", DEMAND_POLL_NUMBER);
    public static final ZapMessageType Configure= new ZapMessageType("Configure", CONFIGURE_NUMBER);
    public static final ZapMessageType ConfigureResponse= new ZapMessageType("ConfigureResponse", CONFIGURE_RESPONSE_NUMBER);

    private ZapMessageType() {
        super(null, null);
    }
    
    private ZapMessageType(String name, Integer number) {
        super(name, number);
    }

    @Override
    public boolean isControl() {
        return false;
    }
        
    public static final ZapMessageType messageTypeForNumber(Integer number) {
        switch (number) {
            case SERVER_CHALLENGE_MESSAGE_NUMBER : return ServerChallenge;
            case CHALLENGE_RESPONSE_MESSAGE_NUMBER : return ClientChallengeResponse;
            case AUTHENTICATION_RESPONSE_MESSAGE_NUMBER : return AuthenticationResponse;
            case HEART_BEAT_MESSAGE_NUMBER : return HeartBeat;
            case HEART_BEAT_RESPONSE_MESSAGE_NUMBER : return HeartBeatResponse;
            case SERVER_TIME_MESSAGE_NUMBER : return ServerTime;
            case ACK_MESSAGE_NUMBER : return Acknowledgement;
            case BUNDLED_REPORT_NUMBER : return BundledReport;
            case WRITE_IO_POINT_NUMBER : return WriteIOPoints;
            case DEMAND_POLL_NUMBER : return DemandPoll;
            case CONFIGURE_NUMBER : return Configure;
            case CONFIGURE_RESPONSE_NUMBER : return ConfigureResponse;
            case SCRUB_NUMBER : return Scrub;
            case CHALLENGE_RESPONSE_MESSAGE_V2_NUMBER : return ChallengeResponseV2;
            case CONFIGURE_UPDATE_MESSAGE_NUMBER : return ConfigureUpdate;
            case OTAD_REQUEST_NUMBER : return OtadRequest;
            case OTAD_STATUS_UPDATE_NUMBER : return OtadStatusUpdate;
        }
        throw new IllegalArgumentException("Invalid message number '" + number + "'");
    }
}
