package zedi.pacbridge.stp.fad;

import java.io.IOException;
import java.nio.ByteBuffer;

import zedi.pacbridge.utl.crc.CrcException;

public class MessageDeserializer {

    public FadMessage fadMessageFromByteBuffer(ByteBuffer byteBuffer) throws IOException, FadException, CrcException {
        FadHeader fadHeader = FadHeader.headerFromByteBuffer(byteBuffer);
        if (fadHeader.isControlHeader()) {
            if (fadHeader.isAcknowledgement())
                return new AckMessage(fadHeader);
            else if (fadHeader.isResendRequest())
                return resendRequestForFadHeader(fadHeader);
        } else {
            return Segment.fadSegmentWithHeaderAndBufferPayload(fadHeader, byteBuffer);
        }
        throw new FadException("Unknown message type");
    }

    private FadMessage resendRequestForFadHeader(FadHeader fadHeader) {
        return fadHeader.isResendMessageRequest() ? new ResendMessageRequest(fadHeader) : new ResendSegmentRequest(fadHeader);
    }
}
