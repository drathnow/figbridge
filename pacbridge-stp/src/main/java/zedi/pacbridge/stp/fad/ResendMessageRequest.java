package zedi.pacbridge.stp.fad;


class ResendMessageRequest extends ResendRequest {
    
    public ResendMessageRequest(FadHeader header) {
        super(header);
    }
    
    public ResendMessageRequest(int messageId) {
        header.setAsResendRequestForMessage(messageId);
    }
}
