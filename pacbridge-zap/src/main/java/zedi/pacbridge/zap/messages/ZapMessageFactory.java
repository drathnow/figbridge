package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.MessageFactory;
import zedi.pacbridge.zap.ZapMessageType;

public class ZapMessageFactory implements MessageFactory<ZapMessage> {
    private static final Logger logger = LoggerFactory.getLogger(ZapMessageFactory.class.getName());
    
    private FieldTypeLibrary fieldTypeLibrary;

    public ZapMessageFactory() {
        this(null);
    }

    public ZapMessageFactory(FieldTypeLibrary fieldTypeLibrary) {
        this.fieldTypeLibrary = fieldTypeLibrary;
    }
   
    public ZapMessage messageFromByteBuffer(Integer messageNumber, ByteBuffer byteBuffer) {
        switch (messageNumber) {
            case ZapMessageType.SERVER_CHALLENGE_MESSAGE_NUMBER :
                return ServerChallenge.serverChallengeFromByteBuffer(byteBuffer);
            case ZapMessageType.CHALLENGE_RESPONSE_MESSAGE_NUMBER :
                return ChallengeResponseMessageV1.clientChallengeResponseFromByteBuffer(byteBuffer);
            case ZapMessageType.AUTHENTICATION_RESPONSE_MESSAGE_NUMBER :
                return AuthenticationResponseMessage.authenticationResponseFromByteByffer(byteBuffer);
            case ZapMessageType.HEART_BEAT_MESSAGE_NUMBER :
                return HeartBeatMessage.heartBeatMessageFromByteBuffer(byteBuffer);
            case ZapMessageType.HEART_BEAT_RESPONSE_MESSAGE_NUMBER :
            	return HeartBeatResponseMessage.heartBeatMessageFromByteBuffer(byteBuffer);
            case ZapMessageType.BUNDLED_REPORT_NUMBER :
                return BundledReportMessage.bundledReportMessageFromByteBuffer(byteBuffer);
            case ZapMessageType.ACK_MESSAGE_NUMBER:
                return AckMessage.ackMessageForByteBuffer(byteBuffer);
            case ZapMessageType.WRITE_IO_POINT_NUMBER:
                return WriteIoPointsControl.messageFromByteBuffer(byteBuffer);
            case ZapMessageType.REQUEST_TIME_MESSAGE_NUMBER:
                return RequestTimeMessage.messageFromByteBuffer(byteBuffer);
            case ZapMessageType.CHALLENGE_RESPONSE_MESSAGE_V2_NUMBER :
                return ChallengeResponseMessageV2.clientChallengeResponseFromByteBuffer(byteBuffer);
            case ZapMessageType.OTAD_STATUS_UPDATE_NUMBER :
                return OtadStatusMessage.messageFromByteBuffer(byteBuffer);
            case ZapMessageType.DEMAND_POLL_NUMBER : 
                return DemandPollControl.messageFromByteBuffer(byteBuffer);
            case ZapMessageType.OTAD_REQUEST_NUMBER :
                return OtadRequestControl.fromByteBuffer(byteBuffer);
            case ZapMessageType.SCRUB_NUMBER :
            	return ScrubControl.scrubControlFromByteBuffer(byteBuffer);
            case ZapMessageType.CONFIGURE_NUMBER : 
                if (fieldTypeLibrary == null)
                    logger.error("Unable to decode ConfigureUpdateMessage because no FieldTypeLibrary is available");
                else
                	return ConfigureControl.configureControlFromByteBuffer(byteBuffer, fieldTypeLibrary);
            case ZapMessageType.CONFIGURE_UPDATE_MESSAGE_NUMBER :
                if (fieldTypeLibrary == null)
                    logger.error("Unable to decode ConfigureUpdateMessage because no FieldTypeLibrary is available");
                else
                    return ConfigureUpdateMessage.configureUpdateMessageFromByteBuffer(byteBuffer, fieldTypeLibrary);
        }
        return null;
    }
}
