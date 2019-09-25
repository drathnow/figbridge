package zedi.pacbridge.stp.fad;


abstract class ResendRequest extends ControlMessage {

    protected ResendRequest(FadHeader header) {
        super(header);
    }

    protected ResendRequest() {
    }
    
    public boolean isResendSegmentRequest() {
        return header.isLastSegment() == false;
    }
}
