package zedi.pacbridge.stp.fad;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class BaseTracker {
    private static Logger logger = LoggerFactory.getLogger(BaseTracker.class);
  
    protected void removeAndCancelPendingTimerWithMessageIdFromMap(int messageId, Map<Integer, ScheduledFuture<?>> timerMap) {
        ScheduledFuture<?> future = timerMap.remove(messageId);
        if (future != null) {
            if (future.cancel(false) == false)
                logger.debug("Unable to cancel timer for messageId: "+ messageId);
        }
    }
}
