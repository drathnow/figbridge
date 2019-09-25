package zedi.figbridge.slapper.config;

import org.jdom2.Element;

public class JmsConfig {
    public static final String QUEUE_MANAGER_NAME_TAG = "QueueManagerName";
    public static final String HOST_NAME_TAG = "HostName";
    public static final String CLIENT_ID_TAG = "ClientId";
    public static final String DESTINATION_NAME_TAG = "RawDataDestinationName";

    private String queueManagerName;
    private String hostName;
    private String clientId;
    private String rawDataDestinationName;
    
    public JmsConfig(String queueManagerName, String hostName, String clientId, String rawDataDestinationName) {
        this.queueManagerName = queueManagerName;
        this.hostName = hostName;
        this.clientId = clientId;
        this.rawDataDestinationName = rawDataDestinationName;
    }

    public String getQueueManagerName() {
        return queueManagerName;
    }
    
    public String getHostName() {
        return hostName;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public String getRawDataDestinationName() {
        return rawDataDestinationName;
    }
    
    public static JmsConfig jmsConfigForElement(Element element) {
        String queueManagerName = element.getChildText(QUEUE_MANAGER_NAME_TAG);
        String hostName = element.getChildText(HOST_NAME_TAG);
        String clientId = element.getChildText(CLIENT_ID_TAG);
        String destName = element.getChildText(DESTINATION_NAME_TAG);
        return new JmsConfig(queueManagerName, hostName, clientId, destName);
    }
}
