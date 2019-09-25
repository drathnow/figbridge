package zedi.pacbridge.stp.fad;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.DefaultInactivityStrategy;
import zedi.pacbridge.utl.crc.CrcException;


class MessageReceiver {

    private static Logger logger = LoggerFactory.getLogger(MessageReceiver.class);
    
    private PendingMessageTracker pendingMessageTracker;
    private InTransitMessageTracker inTransitMessageTracker;
    private FadMessageFactory messageFactory;
    private FadDataHandler dataHandler;
    private FadMessageHandler messageSender;

    public MessageReceiver(PendingMessageTracker messageTracker, InTransitMessageTracker inTransitMessageTracker, FadDataHandler dataHandler, FadMessageHandler messageSender) {
        this(messageTracker, inTransitMessageTracker, new FadMessageFactory(), dataHandler, messageSender);
    }

    public MessageReceiver(PendingMessageTracker messageTracker, InTransitMessageTracker inTransitMessageTracker, FadMessageFactory messageFactory,  FadDataHandler dataHandler, FadMessageHandler messageSender) {
        this.pendingMessageTracker = messageTracker;
        this.messageFactory = messageFactory;
        this.inTransitMessageTracker = inTransitMessageTracker;
        this.dataHandler = dataHandler;
        this.messageSender = messageSender;
    }
    
    public void handleSegmentMessage(Segment segment) {
        byte[] payload = null;
        try {
            payload = pendingMessageTracker.payloadForSegmentMessageIfComplete(segment);
        } catch (CrcException e) {
            logger.error("CRC error detected for segment", e);
            requestSegment(segment);
        }
        if (payload != null) {
            try {
                dataHandler.handleData(ByteBuffer.wrap(payload));
                AckMessage ackMessage = messageFactory.newAckMessage(segment.getMessageId(), segment.getSegmentId());
                messageSender.handleMessage(ackMessage);
            } catch (Exception e) {
                logger.error("Unexpected exception from upper protocol layer", e);
            }
        }
    }

    public void setTransmitTimeoutSeconds(Integer timeoutSeconds) {
        pendingMessageTracker.setPendingMessageInactivityStrategy(new DefaultInactivityStrategy(timeoutSeconds));
    }
    
    public void setReceiveTimeoutSeconds(Integer timeoutSeconds) {
        inTransitMessageTracker.setReceiveTimeoutSeconds(timeoutSeconds);
    }
    
    public void handleControlMessage(ControlMessage controlMessage) {
        inTransitMessageTracker.handleControlMessage(controlMessage, messageSender);
    }

    public int getPendingMessagesCount() {
        return pendingMessageTracker.getPendingMessagesCount();
    }

    public boolean isIdle() {
        return pendingMessageTracker.isIdle() && inTransitMessageTracker.isIdle();
    }

    public void close() {
        reset();
    }

    private void requestSegment(Segment segment) {
        ResendMessageRequest resendRequest = messageFactory.newResendMessageRequest(segment.getMessageId(), segment.getSegmentId());
        try {
            messageSender.handleMessage(resendRequest);
        } catch (IOException e) {
            logger.error("Unable to send resend message request", e);
        }
    }

    public void reset() {
        pendingMessageTracker.clear();
        inTransitMessageTracker.reset();
    }
}
