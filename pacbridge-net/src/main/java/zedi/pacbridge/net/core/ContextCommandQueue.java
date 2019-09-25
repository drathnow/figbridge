package zedi.pacbridge.net.core;

import java.text.SimpleDateFormat;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import zedi.pacbridge.utl.FutureTimer;
import zedi.pacbridge.utl.SystemTime;

class ContextCommandQueue {
    private PriorityQueue<ContextCommandContainer> timerQueue;
    private ConcurrentLinkedQueue<ContextCommand> handlerQueue;
    private SystemTime systemTime;

    public ContextCommandQueue(SystemTime systemTime) {
        this.timerQueue = new PriorityQueue<>();
        this.handlerQueue = new ConcurrentLinkedQueue<>();
        this.systemTime = systemTime;
    }
    
    public ContextCommandQueue() {
        this(new SystemTime());
    }
    
    /**
     * Finds the next due command. The timer queue is always checked first.  If the first 
     * element is due it will be removed and returned.  If the first element is cancelled, 
     * it will be removed and the search will be done again.
     * 
     * If nothing comes from the timer queue, then the next element in the handler queue will
     * be returned, if one exists.
     * 
     * @return {@link ContextCommand} - the next command to execute.
     */
    
    public ContextCommand nextDueCommand() {
        ContextCommandContainer container;
        if ((container = timerQueue.peek()) != null) {
            if (container.isDue() || container.isCancelled()) {
                timerQueue.remove(container);
                if (container.isCancelled())
                    return nextDueCommand();
                else
                    return container.getCommand();
            }
        }
        return handlerQueue.poll();
    }

    public void queueCommand(ContextCommand command) {
        handlerQueue.add(command);
    }
    
    public boolean isEmpty() {
        return timerQueue.isEmpty() && handlerQueue.isEmpty();
    }
    
    /**
     * Queues a command for future execution.  Because of the way the dispatcher threads process
     * context commands, execution of a context command is not gaurenteed to be precise.
     * <p>  
     * @param command
     * @param delayTime
     * @param timeUnit
     */
    public FutureTimer queueCommand(ContextCommand command, long delayTime, TimeUnit timeUnit) {
        long dueTime = systemTime.getCurrentTime() + timeUnit.toMillis(delayTime);
        ContextCommandContainer container = new ContextCommandContainer(command, dueTime);
        timerQueue.add(container);
        return new ContextFutureTimer(container);
    }
    
    void setSystemTime(SystemTime systemTime) {
        this.systemTime = systemTime;
    }
    
    private class ContextFutureTimer implements FutureTimer {
        private ContextCommandContainer container;

        public ContextFutureTimer(ContextCommandContainer container) {
            this.container = container;
        }

        @Override
        public void cancel() {
            container.cancel();
        }
    }
    
    
    class ContextCommandContainer implements Comparable<ContextCommandContainer>{
        private ContextCommand command;
        private Long dueTime;
        private boolean cancelled;
        
        SimpleDateFormat df =  new SimpleDateFormat("YY-MM-DD HH:mm:ss.SSS");
        
        ContextCommandContainer(ContextCommand command, Long dueTime) {
            this.command = command;
            this.dueTime = dueTime;
            this.cancelled = false;
        }
                
        boolean isDue() {
            return dueTime < System.currentTimeMillis();
        }

        ContextCommand getCommand() {
            return command;
        }
        
        void cancel() {
            cancelled = true;
        }
        
        boolean isCancelled() {
            return cancelled;
        }
        
        @Override
        public int compareTo(ContextCommandContainer otherContainer) {
            return (otherContainer == this) ? 0 : dueTime.compareTo(otherContainer.dueTime);
        }
    }
}
