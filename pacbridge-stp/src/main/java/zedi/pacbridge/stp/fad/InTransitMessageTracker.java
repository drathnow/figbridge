package zedi.pacbridge.stp.fad;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.CollectionFactory;
import zedi.pacbridge.utl.DefaultInactivityStrategy;
import zedi.pacbridge.utl.InactivityStrategy;


class InTransitMessageTracker extends BaseTracker {

    private static Logger logger = LoggerFactory.getLogger(InTransitMessageTracker.class.getName());

    private LinkedList<InTransitMessage> messageQueue = new LinkedList<InTransitMessage>();

    private MessageWindow messageWindow;
    private Map<Integer, ScheduledFuture<?>> inTransmitMessagesTimerMap;

    private InactivityStrategy inactivityStrategy;
    private TimeoutContainerFactory timeoutContainerFactory;
    private ResendMessageStrategy resendMessageStrategy;
    private RetransmitEventHandler retransmitEventHandler;

    public InTransitMessageTracker(InactivityStrategy inactivityStrategy, 
                                    ResendMessageStrategy resendMessageStrategy, 
                                    MessageWindow messageWindow,
                                    RetransmitEventHandler retransmitEventHandler) {
        this(inactivityStrategy, resendMessageStrategy, messageWindow, new CollectionFactory(), retransmitEventHandler);
    }

    @SuppressWarnings("unchecked")
    public InTransitMessageTracker(InactivityStrategy inactivityStrategy, 
                                    ResendMessageStrategy resendMessageStrategy, 
                                    MessageWindow messageWindow, 
                                    CollectionFactory collectionFactory,
                                    RetransmitEventHandler retransmitEventHandler) {
        this.messageWindow = messageWindow;
        this.inactivityStrategy = inactivityStrategy;
        this.resendMessageStrategy = resendMessageStrategy;
        this.messageQueue = collectionFactory.newLinkedList(new LinkedList<InTransitMessage>());
        this.inTransmitMessagesTimerMap = collectionFactory.newTreeMap(new TreeMap<Integer, ScheduledFuture<?>>());
        this.timeoutContainerFactory = new TimeoutContainerFactory();
        this.retransmitEventHandler = retransmitEventHandler;
    }

    public boolean isIdle() {
        return messageWindow.isEmpty();
    }

    public int getInTransitMessagesCount() {
        return messageWindow.getInTransitCount();
    }

    public int getQueuedMessageCount() {
        return messageQueue.size();
    }

    public void setTimeoutContainerFactory(TimeoutContainerFactory timeoutContainerFactory) {
        this.timeoutContainerFactory = timeoutContainerFactory;
    }

    public void setReceiveTimeoutSeconds(Integer timeoutSeconds) {
        inactivityStrategy = new DefaultInactivityStrategy(timeoutSeconds);
    }
    
    public void sendAndTrackInTransitMessage(InTransitMessage inTransitMessage, FadMessageHandler messageSender) throws IOException {
        boolean canSend = false;
        canSend = canSendMessageOrQueueIfNot(inTransitMessage, messageSender);
        if (canSend)
            messageSender.handleMessage(inTransitMessage);
    }

    public void handleResendRequestForMessageWithMessageId(int messageId, FadMessageHandler messageSender) {
        InTransitMessage inTransitMessage = null;
        inTransitMessage = messageWindow.inTransitMessageForMessageId(messageId);
        if (inTransitMessage != null) {
            if (resendMessageStrategy.canResendMessage(inTransitMessage)) {
                logger.debug("Resending message: " + messageId);
                scheduleTimerForInTansitMessageWithMessageId(inTransitMessage.getMessageId());
            } else {
                inTransitMessage.setMessageStatus(FadMessageStatus.FAILED);
                handleMaxRetriesExceededForMessageWithMessageId(messageId);
                if ((inTransitMessage = nextMessageToSend()) != null)
                    trackMessage(inTransitMessage);
            }
        }
        sendMessageWithMessageSender(inTransitMessage, messageSender);
    }

    public void handleControlMessage(ControlMessage controlMessage, FadMessageHandler messageSender) {
        if (controlMessage.isAcknowledgement())
            handleAckMessageAndSendNextQueuedMessageIfRequired((AckMessage)controlMessage, messageSender);
        else if (controlMessage.isResendRequest())
            handleResendRequestWithProtocolLayer((ResendRequest)controlMessage, messageSender);
    }

    public void reset() {
        for (ScheduledFuture<?> future : inTransmitMessagesTimerMap.values())
            future.cancel(false);
        inTransmitMessagesTimerMap.clear();
        messageWindow.clear();
    }

    private void sendMessageWithMessageSender(InTransitMessage inTransitMessage, FadMessageHandler messageSender) {
        if (inTransitMessage != null) {
            try {
                inTransitMessage.setMessageStatus(FadMessageStatus.INTRANSIT);
                messageSender.handleMessage(inTransitMessage);
            } catch (IOException e) {
                logger.error("Unable to resend message", e);
            }
        }
    }

    private void handleAckMessageAndSendNextQueuedMessageIfRequired(AckMessage ackMessage, FadMessageHandler messageSender) {
        InTransitMessage nextMessageToSend = null;
        handleAckMessage(ackMessage);
        if ((nextMessageToSend = nextMessageToSend()) != null)
            trackMessage(nextMessageToSend);
        sendMessageWithMessageSender(nextMessageToSend, messageSender);
    }

    private void handleResendRequestWithProtocolLayer(ResendRequest resendRequest, FadMessageHandler messageSender) {
        InTransitMessage inTransitMessage = null;
        inTransitMessage = messageWindow.inTransitMessageForMessageId(resendRequest.getMessageId());
        if (inTransitMessage != null) {
            if (resendMessageStrategy.canResendMessage(inTransitMessage) == false) {
                if (logger.isDebugEnabled())
                    logger.debug("Maximum number or retries exceeded for message Id: " + resendRequest.getMessageId());
                inTransitMessage.setMessageStatus(FadMessageStatus.FAILED);
                messageWindow.stopTrackingMessageWithId(resendRequest.getMessageId());
                removeAndCancelPendingTimerWithMessageIdFromMap(resendRequest.getMessageId(), inTransmitMessagesTimerMap);
                inTransitMessage = null;
            }
        }

        resendMessageOrSegment(resendRequest, messageSender, inTransitMessage);
    }

    private boolean canSendMessageOrQueueIfNot(InTransitMessage inTransitMessage, FadMessageHandler messageSender) {
        if (messageWindow.hasRoom()) {
            trackMessage(inTransitMessage);
            return true;
        } else {
            messageQueue.addLast(inTransitMessage);
            return false;
        }
    }

    private InTransitMessage nextMessageToSend() {
        return (messageQueue.isEmpty() == false && messageWindow.hasRoom()) ? messageQueue.removeFirst() : null;
    }

    private void trackMessage(InTransitMessage nextMessageToSend) {
        messageWindow.trackMessageAndAssignMessageId(nextMessageToSend);
        scheduleTimerForInTansitMessageWithMessageId(nextMessageToSend.getMessageId());
    }

    private void handleMaxRetriesExceededForMessageWithMessageId(int messageId) {
        logger.debug("Max message retries exceeded. Message delivery failed for message ID: " + messageId);
        messageWindow.stopTrackingMessageWithId(messageId);
        removeAndCancelPendingTimerWithMessageIdFromMap(messageId, inTransmitMessagesTimerMap);
    }

    private void resendMessageOrSegment(ResendRequest resendRequest, FadMessageHandler messageSender, InTransitMessage inTransitMessage) {
        if (inTransitMessage != null) {
            try {
                if (resendRequest.isResendSegmentRequest()) {
                    if (logger.isDebugEnabled())
                        logger.debug("Resending segment Id: " + resendRequest.getSegmentId() + " for message Id: " + resendRequest.getMessageId());
                    inTransitMessage.resendSegment(resendRequest.getSegmentId(), messageSender);
                } else {
                    if (logger.isDebugEnabled())
                        logger.debug("Resending messsage Id: " + resendRequest.getMessageId());
                    messageSender.handleMessage(inTransitMessage);
                }
            } catch (Exception e) {
                logger.error("Unable to process resend request for message", e);
            }
        }
    }

    private void scheduleTimerForInTansitMessageWithMessageId(int messageId) {
        RetransmitRunner runner = timeoutContainerFactory.retransmitContainerForMessage(retransmitEventHandler, messageId);
        ScheduledFuture<?> future = inactivityStrategy.scheduleInactivityRunner(runner);
        inTransmitMessagesTimerMap.put(messageId, future);
    }

    private void handleAckMessage(AckMessage ackMessage) {
        int messageId = ackMessage.getMessageId();
        InTransitMessage inTransitMessage = messageWindow.inTransitMessageForMessageId(messageId);
        if (inTransitMessage != null) {
            logger.trace("Message ID '" + inTransitMessage.getMessageId() + "' acknowledged");
            inTransitMessage.setMessageStatus(FadMessageStatus.ACKNOWLEDGED);
            if (inTransitMessage.hasBeenAcknowledged()) {
                messageWindow.stopTrackingMessageWithId(messageId);
                removeAndCancelPendingTimerWithMessageIdFromMap(messageId, inTransmitMessagesTimerMap);
            }
        }
    }
}
