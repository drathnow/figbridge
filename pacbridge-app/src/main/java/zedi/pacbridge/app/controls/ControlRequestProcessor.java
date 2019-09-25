package zedi.pacbridge.app.controls;

import zedi.pacbridge.app.net.RequestProgressListener;
import zedi.pacbridge.net.Control;
import zedi.pacbridge.net.Message;
import zedi.pacbridge.net.controls.ControlStatus;
import zedi.pacbridge.utl.ActivityTracker;
import zedi.pacbridge.utl.DefaultActivityTracker;
import zedi.pacbridge.utl.NotificationCenter;

/**
 * The <code>ControlRequestProcessor</code> is an implementation of {@link OutgoingRequestProcessor} that
 * manages the marshalling of messages and acking of a {@link ControlRequest}.  A <code>ControlRequest</code> 
 * generally contains a single control but there may be cases where a <code>ControlRequest</code> may
 * contain more than one control as part of the request.
 * <p>
 * When processing is completed, the {@link RequestProgressListener} is notified of the event.  If everything
 * went well, the <code>RequestCompletionListener</code> is notified with only a successful control status.
 * If a failure was detected, for any control, a failure is returned along with a JSON String containing an array
 * of key:value objects that contain:
 * 
 * <code>
 *  "control":"<control-name>",
 *  "status":"<SUCCESS|FAILURE>",
 *  "message":"<status-message>" (Omitted if the "status" is "SUCCESS").
 * </code>
 */
public class ControlRequestProcessor implements OutgoingRequestProcessor {
    public static final String CONTROL_PROCESSED_NOTIFICATION = ControlRequestProcessor.class.getName() + ".controlProcessedNotification";
    public static final Integer DEFAULT_TIMEOUT_SECONDS = 180; // 3 min.
    
    public static final String FAILURE_MSG = "Control processing failed.";
    public static final String SUCCESS_MSG = "Control processing succeeded.";
    
    private ControlRequest controlRequest;
    private Control control;
    private Control nextControl;
    private ControlResponseStrategy currentResponseStrategy;
    private RequestProgressListener progressListener;
    private ActivityTracker activityTracker;
    private ControlResponseStrategyFactory responseStrategyFactory;
    private NotificationCenter notificationCenter;
    
    public ControlRequestProcessor(ControlRequest controlRequest, 
                                   ControlResponseStrategyFactory responseStrategyFactory, 
                                   RequestProgressListener progressListener, 
                                   NotificationCenter notificationCenter) {
        this.control = controlRequest.getControl();
        this.nextControl = this.control;
        this.progressListener = progressListener;
        this.controlRequest = controlRequest;
        this.responseStrategyFactory = responseStrategyFactory;
        this.notificationCenter = notificationCenter;
    }

    @Override
    public void starting() {
        activityTracker = new DefaultActivityTracker();
        controlRequest.incrementSendAttempts();
        controlRequest.setStatus(ControlStatus.RUNNING);
        progressListener.requestProcessingStarted(controlRequest);
    }
    
    @Override
    public Message nextMessageWithSequenceNumber(Integer sequenceNumber) {
        Control tmpControl = null;
        if (nextControl != null) {
            nextControl.setSequenceNumber(sequenceNumber);
            currentResponseStrategy = responseStrategyFactory.responseStrategyForControl(nextControl, controlRequest.getSiteAddress());
            if (currentResponseStrategy == null)
                throw new RuntimeException("No response strategy availble for control type " + nextControl.messageType());
            tmpControl = nextControl;
            nextControl = null;
        }
        return tmpControl;
    }

    @Override
    public boolean isExpected(Message message) {
        activityTracker.update();
        currentResponseStrategy.handleMessage(message);
        return currentResponseStrategy.isFinished();
    }

    @Override
    public boolean hasMoreMessages() {
        return nextControl != null;
    }

    @Override
    public void forceFinished(ControlStatus status, String statusMessage) {
        if (currentResponseStrategy != null)
            currentResponseStrategy.forceFinished(status, statusMessage);
    }

    @Override
    public void doFinalProcessing() {
        if (currentResponseStrategy != null) {
            progressListener.requestProcessingCompleted(controlRequest, currentResponseStrategy);
            if (currentResponseStrategy.wasSuccessful())
                notificationCenter.postNotificationAsync(CONTROL_PROCESSED_NOTIFICATION, new ProcessedControlAttachement(controlRequest.getSiteAddress(), controlRequest.getControl()));
        }
    }
}
