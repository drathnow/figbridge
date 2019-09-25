package zedi.pacbridge.gdn.messages.otad;

import java.io.Serializable;

import zedi.pacbridge.gdn.messages.GdnMessage;

public abstract class OtadResponse extends OtadMessage implements GdnMessage, Serializable {

    protected OtadResponse(OtadMessageHeader messageHeader) {
        super(messageHeader);
    }

    public ErrorCode getErrorCode() {
        return messageHeader.getErrorCode();
    }
}