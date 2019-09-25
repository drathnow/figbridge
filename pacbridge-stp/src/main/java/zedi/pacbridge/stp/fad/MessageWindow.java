package zedi.pacbridge.stp.fad;



class MessageWindow {

    private InTransitMessage[] inTransitMessageWindow;

    public MessageWindow(int windowSize) {
        inTransitMessageWindow = new InTransitMessage[windowSize];
    }

    public void trackMessageAndAssignMessageId(InTransitMessage inTransitMessage) {
        int messageId = nextMessageId();
        inTransitMessage.setMessageId(messageId);
        inTransitMessageWindow[messageId] = inTransitMessage;
    }
    
    public void stopTrackingMessageWithId(int messageId) {
        inTransitMessageWindow[messageId] = null;
    }
    
    public boolean hasRoom() {
        return nextMessageId() != -1;
    }
    
    public boolean isEmpty() {
        for (int i = 0; i < inTransitMessageWindow.length; i++)
            if (inTransitMessageWindow[i] != null)
                return false;
        return true;
    }
    
    public int getInTransitCount() {
        int count = 0;
        for (int i = 0; i < inTransitMessageWindow.length; i++)
            if (inTransitMessageWindow[i] != null)
                count++;
        return count;
    }
    
    private int nextMessageId() {
        for (int i = 0; i < inTransitMessageWindow.length; i++)
            if (inTransitMessageWindow[i] == null)
                return i;
        return -1;
    }

    public InTransitMessage inTransitMessageForMessageId(int messageId) {
        return inTransitMessageWindow[messageId];
    }

    public void clear() {
        for (int i = 0; i < inTransitMessageWindow.length; i++)
            inTransitMessageWindow[i] = null;
    }
}
