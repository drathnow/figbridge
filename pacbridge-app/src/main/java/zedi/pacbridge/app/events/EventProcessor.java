package zedi.pacbridge.app.events;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class EventProcessor {
    private static Logger logger = LoggerFactory.getLogger(EventProcessor.class.getName());

    private BridgeContext bridgeContext;
    
    @Inject
    public EventProcessor(BridgeContext bridgeContext) {
        this.bridgeContext = bridgeContext;
    }
    
    public EventProcessor() {
    }
    
    public void processEvent(HandleableEvent event) {
        try {
            event.handle(bridgeContext);
        } catch (Exception e) {
            logger.error("Unexpected exception while processing event", e);
        }
    }
}
