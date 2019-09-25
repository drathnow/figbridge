package zedi.pacbridge.stp.fad;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.HexStringEncoder;
import zedi.pacbridge.utl.crc.CrcException;

public class FadDecoder {
    private static final Logger logger = LoggerFactory.getLogger(FadDecoder.class.getName());
    
    private PendingMessage pendingMessage;

    public byte[] payloadForFadMessage(ByteBuffer byteBuffer) throws IOException, CrcException {
        FadHeader fadHeader = FadHeader.headerFromByteBuffer(byteBuffer);
        logger.trace("(FAD) Header : " + fadHeader.toString());
        if (fadHeader.isLastSegment() == false)
            logger.trace("(FAD) Payload: " + HexStringEncoder.bytesAsHexString(byteBuffer.slice(), '|'));
        if (fadHeader.isControlHeader() == false)
            return messageIfSegmentIsLast(fadHeader, byteBuffer);
        return null;
    }

    private byte[] messageIfSegmentIsLast(FadHeader fadHeader, ByteBuffer byteBuffer) throws CrcException {
        if (pendingMessage == null)
            pendingMessage = new PendingMessage(fadHeader.getMessageId());
        pendingMessage.addSegment(Segment.fadSegmentWithHeaderAndBufferPayload(fadHeader, byteBuffer));
        try {
            byte[] message = pendingMessage.getMessage();
            if (message != null)
                logger.trace("(FAD) Msg: " + HexStringEncoder.bytesAsHexString(message));
            return message;
        } finally {
            if (pendingMessage.isComplete())
                pendingMessage = null;
        }
    }
}
