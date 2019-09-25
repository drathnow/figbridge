package zedi.pacbridge.net.logging;

import org.slf4j.Logger;

public class DefaultTrafficLogger implements TrafficLogger {

    private TrafficFormatter formatter;
    private Logger logger;
    
    public DefaultTrafficLogger(Logger logger) {
        super();
        this.formatter = new TrafficFormatter();
        this.logger = logger;
    }

    @Override
    public void logOutgoingData(byte[] bytes, int startPosition, int length) {
        if (logger.isDebugEnabled())
            logger.debug(formatter.formattedOutgoingLine(bytes, startPosition, length));
    }

    @Override
    public void logIncomingData(byte[] bytes, int startPosition, int length) {
        if (logger.isDebugEnabled())
        logger.debug(formatter.formattedIncomingLine(bytes, startPosition, length));
    }

}
