package zedi.figbridge.slapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;

import zedi.figbridge.slapper.utl.DeviceConglomerator;

public class EventListener implements MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(EventListener.class.getName());
    
    public static String THE_RE = ".*<EventId>(\\d+)<\\/EventId>.*<Nuid>(.*)<\\/Nuid>.*";
    private Pattern thePattern = Pattern.compile(THE_RE);
    
    private DeviceConglomerator deviceConglomerator;
    
    public EventListener() throws XmlPullParserException {
    }
    
    @Inject
    public EventListener(DeviceConglomerator deviceConglomerator) {
        this.deviceConglomerator = deviceConglomerator;
    }

    @Override
    public void onMessage(Message message) {
        try {
            String xmlString = ((TextMessage)message).getText();
            Matcher matcher = thePattern.matcher(xmlString);
            if (matcher.matches()) {
                Long eventId = Long.parseLong(matcher.group(1));
                String username = matcher.group(2);
                deviceConglomerator.removeEventIdForDeviceName(eventId, username);
            }
        } catch (Exception e) {
            logger.error("Unable to get XML message: " + e.toString());
        }
    }
}