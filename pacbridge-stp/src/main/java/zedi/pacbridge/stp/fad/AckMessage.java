package zedi.pacbridge.stp.fad;



class AckMessage extends ControlMessage {

    AckMessage(FadHeader header) {
        super(header);
    }
    
    AckMessage(int messageId, int segmentId) {
        header.setAsAcknowledgmentHeader(messageId, segmentId);
    }

    public int getCrc() {
        return header.getCrc();
    }
}
