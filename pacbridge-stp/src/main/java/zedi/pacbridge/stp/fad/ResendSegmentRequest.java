package zedi.pacbridge.stp.fad;


class ResendSegmentRequest extends ResendRequest {

    public ResendSegmentRequest(FadHeader header) {
        super(header);
    }
    
    public ResendSegmentRequest(int messageId, int segmentId) {
        header.setAsResendRequestForSegment(messageId, segmentId);
    }
}
