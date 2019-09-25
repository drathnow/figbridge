package zedi.pacbridge.stp.fad;

import java.io.IOException;
import java.nio.ByteBuffer;



/**
 * An STPControlMessage is exchanged between STP objects to request control operations such as
 * message acknowledgement and resend requests.  The control message is esentially a 4 byte FAD 
 * header with the CRC being a CRC16 calculation done on the first two bytes of the header.
 * 
 */
abstract class ControlMessage extends FadMessage {

    protected FadHeader header;
    
    protected ControlMessage(FadHeader header) {
        this.header = header;
    }
    
    public ControlMessage() {
        header = new FadHeader();
    }
    
    public void serialize(ByteBuffer byteBuffer) {
        header.serialize(byteBuffer);
    }
    
    @Override
    public int size() {
        return 4;
    }
    
    @Override
    public boolean isControlMessage() {
        return true;
    }
    
    public int getSegmentId() {
        return header.getSegmentId();
    }
    
    public int getMessageId() {
        return header.getMessageId();
    }

    public boolean isAcknowledgement() {
        return header.isAcknowledgement();
    }    

    public boolean isResendRequest() {
        return header.isResendRequest();
    }

    public boolean isLastSegment() {
        return header.isLastSegment();
    }

    @Override
    public void transmitThroughMessageTransmitter(FadMessageTransmitter messageTransmitter, ByteBuffer byteBuffer) throws IOException {
        header.serialize(byteBuffer);
        byteBuffer.flip();
        messageTransmitter.transmitByteBuffer(byteBuffer);
    }
    
    public static ControlMessage ackMessageWithFadHeader(FadHeader fadHeader) {
        return new AckMessage(fadHeader);
    }

    public static ResendRequest resendRequestMessageWithFadHeader(FadHeader fadHeader) {
        return (fadHeader.isLastSegment()) 
                ? new ResendMessageRequest(fadHeader) 
                : new ResendSegmentRequest(fadHeader);
    }
}

