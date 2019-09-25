package zedi.figbridge.slapper.config;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jdom2.Element;

import zedi.pacbridge.utl.JDomUtilities;

public class Configuration {
    public static final String DEFAULT_CONFIG_FILENAME = "bridgeslapper.xml";
    public static final String DEFAULT_NAME = "Slapper";
    
    public static final String BRIDGE_TAG = "Bridge";
    public static final String ADDRESS_TAG = "Address";
    public static final String PORT_TAG = "Port";
    public static final String FIG_DEVICE_TAG = "FigDevice";
    public static final String JMS_TAG = "Jms";
    public static final String PROPERTY_TAG = "Property";
    public static final String NAME_TAG = "name";
    
    private static String configurationFileName = DEFAULT_CONFIG_FILENAME;
    
    private Properties properties;
    private InetSocketAddress bridgeAddress;
    private List<FigDeviceConfig> deviceConfigs;
    private JmsConfig jmsConfig;
    private String name;
    
    public Configuration() {
    }
    
    public Configuration(String name, InetSocketAddress bridgeAddress, List<FigDeviceConfig> deviceConfigs, JmsConfig jmsConfig, Properties properties) {
        this.bridgeAddress = bridgeAddress;
        this.deviceConfigs = deviceConfigs;
        this.jmsConfig = jmsConfig;
        this.properties = properties;
        this.name = name;
    }

    public List<FigDeviceConfig> getDeviceConfigs() {
        return deviceConfigs;
    }
    
    public InetSocketAddress getBridgeAddress() {
        return bridgeAddress;
    }
    

    public String getJmsQueueManagerName() {
        return jmsConfig.getQueueManagerName();
    }

    public String getJmsHostName() {
        return jmsConfig.getHostName();
    }

    public String getJmsClientId() {
        return jmsConfig.getClientId();
    }

    public String getRawDataTopic() {
        return jmsConfig.getRawDataDestinationName();
    }
    

    public void setPropertiesInProperties(Properties properties) {
        properties.putAll(this.properties);
    }

    public static void setConfigurationFilename(String filename) {
        configurationFileName = filename;
    }
    
    public static String getConfigurationFilename() {
        return configurationFileName;
    }
    
    public static final Configuration configurationFromElement(Element element) {
        String name = element.getAttributeValue(NAME_TAG, DEFAULT_NAME);
        String address = element.getChild(BRIDGE_TAG).getChild(ADDRESS_TAG).getText();
        Integer port = Integer.parseInt(element.getChild(BRIDGE_TAG).getChild(PORT_TAG).getText());
        InetSocketAddress bridgeAddress = new InetSocketAddress(address, port);
        List<FigDeviceConfig> deviceConfigs = new ArrayList<>();
        List<Element> figDeviceElements = element.getChildren(FIG_DEVICE_TAG);
        for (Element e : figDeviceElements)
            deviceConfigs.add(FigDeviceConfig.figDeviceConfigForElement(e));
        JmsConfig jmsConfig = JmsConfig.jmsConfigForElement(element.getChild(JMS_TAG));
        Properties properties = JDomUtilities.propertiesFromElement(element, PROPERTY_TAG);
        return new Configuration(name, bridgeAddress, deviceConfigs, jmsConfig, properties);
    }

    public String getName() {
        return name;
    }
}
