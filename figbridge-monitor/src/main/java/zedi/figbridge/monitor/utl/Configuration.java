package zedi.figbridge.monitor.utl;

import java.io.IOException;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.JDOMException;

import zedi.pacbridge.utl.JDomUtilities;


public class Configuration {
    private static final String JMS_TAG = "Jms";
    private static final String QUEUE_MANAGER_TAG = "QueueManagerName";
    private static final String HOST_NAME_TAG = "HostName";
    private static final String EVENT_TOPIC_TAG = "EventTopicName";
    private static final String BRIDGE_TAG = "Bridge";
    private static final String ADDRESS_TAG = "Address";
    private static final String PORT_TAG = "Port";
    private static final String AUTHENTICATION_TAG = "Authentication";
    private static final String NUID_TAG = "Nuid";
    private static final String SECRET_KEY_TAG = "SecretKey";
    private static final String PROPERTY_TAG = "Property";
    
    private String jmsQueueManagerName;
    private String jmsHostName;
    private String jmsEventTopicName;
    private String bridgeAddress;
    private Integer bridgePortNumber;
    private String nuid;
    private String base64SecretKey;

    public Configuration(String xmlConfiguration) throws JDOMException, IOException {
        Element rootElement = JDomUtilities.elementForXmlString(xmlConfiguration);
        JDomUtilities.defineSystemPropertiesFromRootElement(rootElement, true, PROPERTY_TAG);
        Element nextElement = rootElement.getChild(JMS_TAG);
        jmsQueueManagerName = nextElement.getChildText(QUEUE_MANAGER_TAG);
        jmsHostName  = nextElement.getChildText(HOST_NAME_TAG);
        jmsEventTopicName  = nextElement.getChildText(EVENT_TOPIC_TAG);
        nextElement = rootElement.getChild(BRIDGE_TAG);
        bridgeAddress = nextElement.getChildText(ADDRESS_TAG);
        bridgePortNumber = Integer.parseInt(nextElement.getChildText(PORT_TAG));
        nextElement = rootElement.getChild(AUTHENTICATION_TAG);
        nuid = nextElement.getChildText(NUID_TAG);
        base64SecretKey = nextElement.getChildText(SECRET_KEY_TAG);
    }
    
    public String getJmsQueueManagerName() {
        return jmsQueueManagerName;
    }

    public String getJmsHostName() {
        return jmsHostName;
    }

    public String getJmsEventTopicName() {
        return jmsEventTopicName;
    }

    public String getBridgeAddress() {
        return bridgeAddress;
    }

    public Integer getBridgePortNumber() {
        return bridgePortNumber;
    }

    public String getNuid() {
        return nuid;
    }

    public String getBase64SecretKey() {
        return base64SecretKey;
    }

}
