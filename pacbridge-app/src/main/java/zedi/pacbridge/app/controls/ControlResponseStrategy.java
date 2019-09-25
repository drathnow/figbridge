package zedi.pacbridge.app.controls;

import zedi.pacbridge.net.Message;
import zedi.pacbridge.net.controls.ControlStatus;

public interface ControlResponseStrategy extends RequestCompletionStrategy {
    public void handleMessage(Message message);
    public boolean isFinished();
    public void forceFinished(ControlStatus eventStatus, String statusMessage);
}
