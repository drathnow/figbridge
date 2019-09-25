package zedi.pacbridge.app.config;

import org.jdom2.Element;

import zedi.pacbridge.net.TransportType;

public class TcpTransportConfig implements TransportConfig {
    public static final String ROOT_ELEMENT_NAME = "TcpTransport";
    public static final String INCOMING_ONLY_TAG = "incomingOnly";
    public static final String LISTENING_PORT_TAG = "ListeningPort";
    public static final String REMOTE_PORT_TAG = "RemotePort";
    public static final String MAX_CONNECTION_ATTEMPTS_TAG = "MaxConnectionAttempts";
    public static final String CONNECTION_QUEUE_LIMIT_TAG = "ConnectionQueueLimit";
    public static final String LISTENING_ADDRESS_TAG = "ListeningAddress";

    private boolean incomingOnly;
    private Integer listeningPort;
    private Integer remotePort;
    private Integer connectionQueueLimit;
    private Integer maxConnectionAttempts;
    private String listeningAddress;
    
    private TcpTransportConfig() {
    }
    
    @Override
    public TransportType getTransportType() {
        return TransportType.TCP;
    }
    
    public boolean isIncomingOnly() {
        return incomingOnly;
    }

    @Override
    public Integer getListeningPort() {
        return listeningPort;
    }

    @Override
    public Integer getRemotePort() {
        throwIfIncomingOnly(REMOTE_PORT_TAG);
        return remotePort;
    }

    public Integer getMaxConnectionAttempts() {
        throwIfIncomingOnly(CONNECTION_QUEUE_LIMIT_TAG);
        return maxConnectionAttempts;
    }

    @Override
    public Integer getConnectionQueueLimit() {
        return connectionQueueLimit;
    }

    @Override
    public String getListeningAddress() {
        return listeningAddress;
    }
    
    private void throwIfIncomingOnly(String value) {
        if (isIncomingOnly())
            throw new UnsupportedOperationException("Value '" + value + "' is not set for incoming only transport.");
    }

	static TcpTransportConfig transportConfigForElement(Element rootElement) {
        TcpTransportConfig config = new TcpTransportConfig();
        if (rootElement.getAttributeValue(INCOMING_ONLY_TAG) == null)
            config.incomingOnly = Boolean.TRUE;
        else
            config.incomingOnly = Boolean.valueOf(rootElement.getAttributeValue(INCOMING_ONLY_TAG));
        config.listeningPort = Integer.valueOf(rootElement.getChildText(LISTENING_PORT_TAG));
        config.connectionQueueLimit= Integer.valueOf(rootElement.getChildText(CONNECTION_QUEUE_LIMIT_TAG));
        config.listeningAddress = rootElement.getChildText(LISTENING_ADDRESS_TAG);
        if (config.isIncomingOnly() == false) {
            config.remotePort = Integer.valueOf(rootElement.getChildText(REMOTE_PORT_TAG));
            config.maxConnectionAttempts = Integer.valueOf(rootElement.getChildText(MAX_CONNECTION_ATTEMPTS_TAG));
        }
        return config;
    }
}
