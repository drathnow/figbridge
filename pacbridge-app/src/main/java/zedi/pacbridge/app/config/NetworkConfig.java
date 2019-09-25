package zedi.pacbridge.app.config;

import java.util.Properties;

import org.jdom2.Element;

import zedi.pacbridge.app.net.IdentityType;
import zedi.pacbridge.utl.DeltaTime;

public class NetworkConfig {
    public static final String ROOT_ELEMENT_NAME = "Network";
    public static final String PROPERTIES_TAG = "Property";
    private static final String VALUE_TAG = "value";
    private static final String NAME_TAG = "name";
    private static final String NUMBER_TAG = "number";
    private static final String TYPE_TAG = "type";
    private static final String IDENTITY_TYPE_TAG = "identityType";
    private static final String INCOMING_ONLY_TAG = "incomingOnly";
    private static final String PERSISTENT_CONNECTION_TAG = "persistentConnections";
    private static final String INACTIVE_TIMEOUT_TAG = "InactiveTimeout";
    
    private Integer networkNumber;
    private String typeName;
    private AuthenticationConfig authenticationConfig;
    private TransportConfig transportConfig;
    private ProtocolConfig protocolConfig;
    private Properties properties;
    private IdentityType identityType;
    private boolean incomingOnly;
    private boolean persistentConnections;
    private Integer inactiveTimeoutSeconds;

    private NetworkConfig() {
        this.properties = new Properties();
        this.identityType = IdentityType.SiteProvided;
        this.persistentConnections = false;
        this.incomingOnly = true;
        this.inactiveTimeoutSeconds = 0;
    }
    
    public TransportConfig getTransportConfig() {
        return transportConfig;
    }

    public ProtocolConfig getProtocolConfig() {
        return protocolConfig;
    }
    
    public AuthenticationConfig getAuthenticationConfig() {
        return authenticationConfig;
    }
    
    public Properties getProperties() {
        return properties;
    }
    
    public Integer getNetworkNumber() {
        return networkNumber;
    }
    
    public String getTypeName() {
        return typeName;
    }

    public boolean hasAuthentication() {
        return authenticationConfig != null;
    }

    public IdentityType getIdentityType() {
        return identityType;
    }

    public boolean isIncomingOnly() {
        return incomingOnly;
    }
    
    public boolean isAuthenticated() {
        return authenticationConfig != null;
    }

    public boolean supportsPersistentConnections() {
        return persistentConnections;
    }

    public Integer getInactiveTimeoutSeconds() {
        return inactiveTimeoutSeconds;
    }

    static NetworkConfig networkConfigForJdomElement(Element rootElement) {
        NetworkConfig networkConfig = new NetworkConfig();
        if (rootElement.getChildText(INACTIVE_TIMEOUT_TAG) != null)
            networkConfig.inactiveTimeoutSeconds = DeltaTime.deltaTimeStringToSeconds(rootElement.getChildText(INACTIVE_TIMEOUT_TAG));
        networkConfig.networkNumber = Integer.valueOf(rootElement.getAttributeValue(NUMBER_TAG));
        if (rootElement.getAttributeValue(IDENTITY_TYPE_TAG) != null)
            networkConfig.identityType = IdentityType.identityTypeForString(rootElement.getAttributeValue(IDENTITY_TYPE_TAG));
        if (rootElement.getAttributeValue(INCOMING_ONLY_TAG) != null)
            networkConfig.incomingOnly = Boolean.parseBoolean(rootElement.getAttributeValue(INCOMING_ONLY_TAG));
        if (rootElement.getAttributeValue(PERSISTENT_CONNECTION_TAG) != null)
            networkConfig.persistentConnections = Boolean.parseBoolean(rootElement.getAttributeValue(PERSISTENT_CONNECTION_TAG));
        networkConfig.typeName = rootElement.getAttributeValue(TYPE_TAG);
        Element element = rootElement.getChild(TcpTransportConfig.ROOT_ELEMENT_NAME);
        networkConfig.transportConfig = TcpTransportConfig.transportConfigForElement(element);
        
        element = rootElement.getChild(ProtocolConfig.ROOT_ELEMENT_NAME);
        networkConfig.protocolConfig = ProtocolConfig.protocolConfigForElement(element);
        Element authElement = rootElement.getChild(AuthenticationConfig.ROOT_ELEMENT_NAME);
        if (authElement != null)
            networkConfig.authenticationConfig = AuthenticationConfig.authenticationConfigForElement(authElement);
        for (Element el : rootElement.getChildren(PROPERTIES_TAG))
            networkConfig.properties.put(el.getAttributeValue(NAME_TAG), el.getAttributeValue(VALUE_TAG));
        return networkConfig;
    }

}
