package zedi.pacbridge.stp.fad;

public class RetransmitRunner implements Runnable {

    private RetransmitEventHandler retransmitEventHandler;
    private int messageId;
    
    public RetransmitRunner(RetransmitEventHandler retransmitEventHandler, int messageId) {
        this.retransmitEventHandler = retransmitEventHandler;
        this.messageId = messageId;
    }
    
    public void run() {
        retransmitEventHandler.retransmitMessageWithMessageId(messageId);
    }
    
}
