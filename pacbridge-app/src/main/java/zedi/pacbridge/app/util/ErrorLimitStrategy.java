package zedi.pacbridge.app.util;

import java.io.Serializable;

import zedi.pacbridge.utl.IntegerSystemProperty;
import zedi.pacbridge.utl.SystemTime;

class ErrorLimitStrategy implements Serializable {
    private static final long serialVersionUID = 1001L;
    
    public static final String ERROR_LIMIT_TIME_PROPERTY_NAME = "pacbridge.errorLimitThresholdSeconds";
    public static final String ERROR_LIMIT_COUNT_PROPERTY_NAME = "pacbridge.errorLimitCount";
    public static final String ERROR_LIMIT_SANTITY_TIME_PROPERTY_NAME ="pacbridge.errorLimitSanitySeconds";
    
    public static final Integer DEFAULT_ERROR_SANITY_SECONDS = 10;
    public static final Integer DEFAULT_ERROR_LIMIT_SECONDS = 3;
    public static final Integer DEFAULT_ERROR_LIMIT_COUNT = 5;
    
    private static final IntegerSystemProperty limitTimeProperty = new IntegerSystemProperty(ERROR_LIMIT_TIME_PROPERTY_NAME, DEFAULT_ERROR_LIMIT_SECONDS);
    private static final IntegerSystemProperty limitCountProperty = new IntegerSystemProperty(ERROR_LIMIT_SANTITY_TIME_PROPERTY_NAME, DEFAULT_ERROR_LIMIT_COUNT);
    private static final IntegerSystemProperty sanityTimeProperty = new IntegerSystemProperty(ERROR_LIMIT_SANTITY_TIME_PROPERTY_NAME, DEFAULT_ERROR_SANITY_SECONDS);
    
    private Integer errorTimeLimitSeconds;
    private Integer errorCountLimit;
    private SystemTime systemTime;
    private State currentState;
    private Integer sanityTimeSeconds;
    
    ErrorLimitStrategy(Integer errorTimeLimitSeconds, Integer errorCountLimit, Integer sanityTimeSeconds, SystemTime systemTime) {
        this.errorTimeLimitSeconds = errorTimeLimitSeconds;
        this.errorCountLimit = errorCountLimit;
        this.systemTime = systemTime;
        this.sanityTimeSeconds = sanityTimeSeconds;
        reset();
    }

    public ErrorLimitStrategy(Integer errorTimeLimit, Integer errorCountLimit, Integer sanityTimeSeconds) {
        this(errorTimeLimit, errorCountLimit, sanityTimeSeconds, new SystemTime());
    }

    public ErrorLimitStrategy() {
        this(limitTimeProperty.currentValue(), limitCountProperty.currentValue(), sanityTimeProperty.currentValue());
    }
    
    public void reset() {
        this.currentState = new HappyState();
    }
    
    public void incrementErrorCount() {
        currentState.incrementErrorCount();
    }
    
    public boolean canLogAnotherError() {
        return currentState.canLogAnotherError();
    } 
    
    private interface State {
        public void incrementErrorCount();
        public boolean canLogAnotherError();
    }
    
    private class ErrorState implements State {

        private long now = systemTime.getCurrentTime();
        
        @Override
        public void incrementErrorCount() {
            long diff = systemTime.getCurrentTime() - now;
            if (diff < (sanityTimeSeconds*1000L))
                now = systemTime.getCurrentTime();
            else
                currentState = new HappyState();
        }

        @Override
        public boolean canLogAnotherError() {
            if (systemTime.getCurrentTime() - now > (sanityTimeSeconds*1000L)) {
                currentState = new HappyState();
                return true;
            }
            return false;
        }
    }
    
    private class LoggedErrorState implements State {

        private long now = systemTime.getCurrentTime();
        private int errorCount = 1;
        
        @Override
        public void incrementErrorCount() {
            if (systemTime.getCurrentTime() - now < (errorTimeLimitSeconds*1000L)) {
                if (errorCount++ >= errorCountLimit)
                    currentState = new ErrorState();
            } else
                currentState = new HappyState();
        }

        @Override
        public boolean canLogAnotherError() {
            return true;
        }
    }
    
    private class HappyState implements State {

        @Override
        public void incrementErrorCount() {
            currentState = new LoggedErrorState();
        }

        @Override
        public boolean canLogAnotherError() {
            return true;
        }
    }
}
