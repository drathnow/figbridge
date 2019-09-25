package zedi.pacbridge.net.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraceLogger {
    public static final TraceLogger L7 = new TraceLogger("L7");
    public static final TraceLogger L6 = new TraceLogger("L6");
    public static final TraceLogger L5 = new TraceLogger("L5");
    public static final TraceLogger L4 = new TraceLogger("L4");
    public static final TraceLogger L3 = new TraceLogger("L3");
    public static final TraceLogger L2 = new TraceLogger("L2");
    public static final TraceLogger L1 = new TraceLogger("L1");
    
    private String name;
    private Logger logger;
    
    private TraceLogger(String name) {
        this.logger = LoggerFactory.getLogger(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public boolean isEnabled() {
        return logger.isTraceEnabled();
    }
    
    public void trace(String message) {
        logger.trace(name + "> " + message);
    }
}
