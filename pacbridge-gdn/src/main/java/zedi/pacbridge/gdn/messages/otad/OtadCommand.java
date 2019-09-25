package zedi.pacbridge.gdn.messages.otad;

import java.io.Serializable;

import zedi.pacbridge.gdn.messages.GdnMessage;
import zedi.pacbridge.net.Control;

public abstract class OtadCommand extends OtadMessage implements GdnMessage, Control, Serializable {
    private static final long serialVersionUID = 1001L;
    
    protected OtadCommand(OtadMessageHeader messageHeader) {
        super(messageHeader);
    }
    
    protected OtadCommand(OtadMessageType messageType) {
        super(new OtadMessageHeader(true, messageType));
    }
}
