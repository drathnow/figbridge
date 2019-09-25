package zedi.pacbridge.net.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.net.core.MonitoringEvent.EventType;
import zedi.pacbridge.utl.SystemTime;
import zedi.pacbridge.utl.annotations.AfterTaskFinishes;
import zedi.pacbridge.utl.annotations.BeforeTaskStarts;
import zedi.pacbridge.utl.concurrent.DetachedTask;

public class RequestQueueMonitor implements DetachedTask {

    private static Logger logger = LoggerFactory.getLogger(RequestQueueMonitor.class);
        
    private List<MonitoringEvent> monitorEvents = new ArrayList<MonitoringEvent>();
    private RequestQueue<?> requestQueue;
    private MonitorState currentState;
    private RequestQueueMonitorHelper monitorHelper;
    private SystemTime systemTime;
    private boolean shutdown;
    
    public RequestQueueMonitor(RequestQueue<?> requestQueue, RequestQueueMonitorHelper monitorHelper) {
        this.requestQueue = requestQueue;
        this.monitorHelper = monitorHelper;
        this.currentState = new MonitoringQueueDepthState();
        this.systemTime = new SystemTime();
    }

    @Override
    public boolean shouldExitAfterMainLoop() {
        currentState.doAction();
        try {
            Thread.sleep(monitorHelper.getScanTimeMilliseconds());
        } catch (InterruptedException e) {
        }
        return shutdown;
    }

    @BeforeTaskStarts
    public void executionStarting() {
        logger.info("Dispatcher request queue monitor starting");
    }
    
    @AfterTaskFinishes
    public void executionTerminating() {
        logger.info("Dispatcher request queue monitor exiting");
    }
    
    public void shutdown() {
        this.shutdown = true;
    }
    
    private void addMonitorEvent(MonitoringEvent.EventType eventType) {
        MonitoringEvent event = new MonitoringEvent(eventType,
                                                        systemTime.getCurrentTime(), 
                                                        monitorHelper.getCoreWorkerCount(), 
                                                        monitorHelper.getCurrentWorkerCount());
        monitorEvents.add(event);
        if (monitorEvents.size() > 1000) {
            List<MonitoringEvent> newList = new ArrayList<MonitoringEvent>(monitorEvents.size());
            Collections.copy(monitorEvents, newList);
            new Thread(new MonitorActivityAnalyzer(newList, monitorHelper)).start();
            monitorEvents.clear();
        }
    }
    
    void setSystemTime(SystemTime systemTime) {
        this.systemTime = systemTime;
    }
        
    void setCurrentState(MonitorState currentState) {
        this.currentState = currentState;
    }
    
    MonitorState getCurrentState() {
        return currentState;
    }
        
    interface MonitorState {
        public void doAction();
    }
    
    private abstract class BaseMonitorState {
        
        protected boolean isQueueDepthAboveCriticalDepth() {
            return requestQueue.size() >= monitorHelper.getCriticalQueueDepthThreshold();
        }
        
        protected boolean isQueueDepthAtOrBelowHysteresis() {
            return requestQueue.size() <= monitorHelper.getQueueDepthHysteresis();
        }
        
        protected boolean elapsedTimeHasExpiredSinceTime(long elapsedTime, long sinceTime) {
            return (systemTime.getCurrentTime() - sinceTime) > elapsedTime;
        }
    }
    
    class MonitoringQueueDepthState extends BaseMonitorState implements MonitorState {

        @Override
        public void doAction() {
            if (isQueueDepthAboveCriticalDepth())
                currentState = new QueueDepthAboveThresholdState();
        }
    }
    
    class QueueDepthAboveThresholdState extends BaseMonitorState implements MonitorState {

        private long detectionTime = systemTime.getCurrentTime();
        
        @Override
        public void doAction() {
            if (isQueueDepthAtOrBelowHysteresis())
                currentState = new MonitoringQueueDepthState();
            else if ((systemTime.getCurrentTime() - detectionTime) > monitorHelper.getQueueDepthSanityTimeMilliseconds())
                currentState = new AddDispatchersState();
        }
    }
    
    class AddDispatchersState extends BaseMonitorState implements MonitorState {

        @Override
        public void doAction() {
            logger.info("Adding dispatcher: current count: " 
                    + monitorHelper.getCurrentWorkerCount() 
                    + ", core count: " 
                    + monitorHelper.getCoreWorkerCount()
                    + " (Current Request Queue Size = "
                    + requestQueue.size()
                    + ", Critical Size = ",
                    + monitorHelper.getQueueDepthHysteresis()
                    + ")");
            addMonitorEvent(EventType.ADD);
            monitorHelper.createNewWorker();
            currentState = new WaitingForNewDispatcherToTakeEffectState();
        }
    }
    
    class WaitingForNewDispatcherToTakeEffectState extends BaseMonitorState implements MonitorState {

        private long startTime = systemTime.getCurrentTime();
        
        @Override
        public void doAction() {
            if (elapsedTimeHasExpiredSinceTime(2000, startTime)) {
                if (isQueueDepthAboveCriticalDepth())
                    currentState = new AddDispatchersState();
                else if (isQueueDepthAtOrBelowHysteresis())
                    currentState = new MonitoringQueueDepthState();
                else 
                    currentState = new AddDispatchersState();
            }
        }
    }
}
