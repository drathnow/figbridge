package zedi.figbridge.monitor.utl;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.JDomUtilities;

public class ReportMessageListener implements MessageListener, LastErrorable {
    private static final Logger logger = LoggerFactory.getLogger(ReportMessageListener.class.getName());
    
    public static final String ROOT_ELEMENT_NAME = "SiteReport";
    public static final String NUID_TAG = "Nuid";
    public static final String TIMESTAMP_TAG = "Timestamp";
    public static final String REPORT_ITEM_TAG = "ReportItem";
    public static final String INDEX_ATTR = "index";
    
    private String lastError;
    private String username;
    private Integer timestampSeconds;
    private Integer ioId;
    private boolean done;

    
    public ReportMessageListener(String username, Integer timestampSeconds, Integer ioId) {
        this.username = username;
        this.timestampSeconds = timestampSeconds;
        this.ioId = ioId;
        this.done = false;
    }

    public boolean isDone() {
        return done;
    }
    
    @Override
    public void onMessage(Message message) {
        if (done == false) {
            try {
                TextMessage textMessage = (TextMessage)message;
                String xmlMessage = textMessage.getText();
                Element element = JDomUtilities.elementForXmlString(xmlMessage);
                if (element.getName().equals(ROOT_ELEMENT_NAME))
                    done = isOurSiteReport(element);
            } catch (Exception e) {
                lastError = "Unable to process incoming message: " + e.toString();
                logger.debug(lastError, e);
            }
        }
    }

    @Override
    public String getLastErrorText() {
        return lastError;
    }
    
    private boolean isOurSiteReport(Element reportElement) {
        String nuid = null;
        String timestampString = null;
        String indexString = null;
        
        if (reportElement.getChildren(REPORT_ITEM_TAG).size() != 1)
            return false;

        if (((nuid = reportElement.getChildText(NUID_TAG)) == null) 
            || ((timestampString = reportElement.getChildText(TIMESTAMP_TAG)) == null)
            || ((indexString = reportElement.getChild(REPORT_ITEM_TAG).getAttributeValue(INDEX_ATTR)) == null)) {
            return false;
        }

        return nuid.equals(username) 
                && Long.parseLong(timestampString)/1000L == timestampSeconds
                && Integer.parseInt(indexString) == ioId;
    }
}
