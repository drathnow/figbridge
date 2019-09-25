package zedi.pacbridge.gdn.messages;

import java.nio.ByteBuffer;

import zedi.pacbridge.net.MessageFactory;


public class GdnMessageFactory implements MessageFactory<GdnMessage>{

    public GdnMessage messageFromByteBuffer(Integer messageNumber, ByteBuffer byteBuffer) {
        switch (messageNumber) {
            case GdnMessageType.NUMBER_FOR_EXTENDED_REPORT_MESSAGE :
                return ExtendedReportMessage.extendedReportMessageFromByteBuffer(byteBuffer);
            case GdnMessageType.NUMBER_FOR_STANDARD_REPORT_MESSAGE :
                return StandardReportMessage.standardReportMessageFromByteBuffer(byteBuffer);
            case GdnMessageType.NUMBER_FOR_WRITE_IOPOINT_MESSAGE :
                return WriteIoPointControl.writeIoPointControlFromByteBuffer(byteBuffer);
        }
        throw new IllegalArgumentException("Message type not implemented: " + messageNumber);
    }
}
