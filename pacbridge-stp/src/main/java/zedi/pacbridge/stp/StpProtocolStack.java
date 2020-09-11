package zedi.pacbridge.stp;

import zedi.pacbridge.net.annotations.AsyncRequester;
import zedi.pacbridge.stp.apl.Apl;
import zedi.pacbridge.stp.fad.Fad;
import zedi.pacbridge.stp.fad.FadMessageTracker;
import zedi.pacbridge.utl.ThreadContext;

public class StpProtocolStack {
    private Fad fad = new Fad();
    private Apl apl = new Apl();
    
    public StpProtocolStack() {
        this.fad = new Fad();
        this.apl = new Apl();
//        ProtocolLayer[] layers = new ProtocolLayer[]{fad, apl};
//        setProtocolLayers(layers);
    }
    
    public StpProtocolStack(ThreadContext astRequester) {
        this();
        setAstRequester(astRequester);
    }

    public FadMessageTracker lastMessageTracker() {
        return fad.lastMessageTracker();
    }
    
    public void setTransmitTimeoutSeconds(Integer transmitTimeoutSeconds) {
        fad.setTransmitTimeoutSeconds(transmitTimeoutSeconds);
    }
 
    public void setReceiveTimeoutSeconds(Integer receiveTimeoutSeconds) {
        fad.setReceiveTimeoutSeconds(receiveTimeoutSeconds);
    }
    
    public void setMaxPacketSize(Integer packetSize) {
        fad.setMaxPacketSize(packetSize);
        apl.setMaxPacketSize(packetSize*2);
    }
    
    @AsyncRequester
    public void setAstRequester(ThreadContext requester) {
        fad.setAstRequester(requester);
    }
}
