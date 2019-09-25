package zedi.pacbridge.app.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.FigBridgeThreadFactory;
import zedi.pacbridge.utl.FileChangeHandler;
import zedi.pacbridge.utl.FileChangeMonitor;
import zedi.pacbridge.utl.GlobalScheduledExecutor;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.PropertyHelper;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.StringUtilities;
import zedi.pacbridge.zap.messages.FieldType;

@Singleton (name = "BridgeConfiguration")
@Startup
public class XmlBridgeConfiguration implements BridgeConfiguration {
    private static Logger logger = LoggerFactory.getLogger(XmlBridgeConfiguration.class.getName());

    public static final String PROPERTIES_TAG = "Properties";
    public static final String PROPERTY_TAG = "Property";
    public static final String NAME_TAG = "name";
    public static final String VALUE_TAG = "value";
    public static final String FIELD_TYPES_TAG = "FieldTypes";
    public static final String FIELD_TYPE_TAG = "FieldType";
    
    public static final long CONFIG_FILE_MONITOR_INTERVAL_SECONDS = 10L;
    public static final String CONFIG_FILE_NAME = "pacbridge.xml";
    
    private List<NetworkConfig> networkElements;
    private Set<SiteAddress> addressExclusionList;
    private Set<ConfigurationChangeListener> changeListeners;
    private File configFile;
    private FileChangeMonitor changeMonitor;
    private String bridgeName;
    private Integer systemId = 0;
    private GlobalScheduledExecutor scheduledExecutor;
    private List<FieldType> fieldTypes;
    
    @Inject
    public XmlBridgeConfiguration(GlobalScheduledExecutor scheduledExecutor) {
        this.scheduledExecutor = scheduledExecutor;
    }
    
    public XmlBridgeConfiguration() {
    }
    
    public Integer getSystemId() {
        return systemId;
    }
    
    @PostConstruct
    void startup() {
        changeListeners = Collections.synchronizedSet(new HashSet<ConfigurationChangeListener>());
        String configDir = isDomainConfig() 
                ? System.getProperty("jboss.domain.config.dir")
                        : System.getProperty("jboss.server.config.dir");
        configFile = new File(configDir, CONFIG_FILE_NAME);
        if (configFile.exists() == false)
            throw new RuntimeException("Unable to load configuration from file " + configFile.getAbsolutePath(), new FileNotFoundException(configFile.getAbsolutePath()));
        changeMonitor = new FileChangeMonitor(scheduledExecutor, configFile, new MyFileChangeHandler(), CONFIG_FILE_MONITOR_INTERVAL_SECONDS, new FigBridgeThreadFactory());
        try {
            loadConfigFile();
            changeMonitor.start();
        } catch (IOException | JDOMException e) {
            throw new RuntimeException("Unable to load configuration from file " + configFile.getAbsolutePath(), e);
        }
        logger.info("Bridge configuration loading completed");
    }
    
    @PreDestroy
    void shutdown() {
        changeMonitor.shutdown();
    }
        
    public String getBridgeName() {
        return bridgeName;
    }
    
    public Set<SiteAddress> addressExclusionList() {
        return Collections.unmodifiableSet(addressExclusionList);
    }
    
    public List<NetworkConfig> getNetworkConfigurations() {
        return Collections.unmodifiableList(networkElements);
    }

    public void addChangeListener(ConfigurationChangeListener changeListener) {
        changeListeners.add(changeListener);
    }
    
    public void removeChangeListener(ConfigurationChangeListener changeListener) {
        synchronized (changeListener) {
            changeListeners.remove(changeListener);
        }
    }

    @Override
    public List<FieldType> getFieldTypes() {
        return fieldTypes;
    }
    
    private void loadConfigFile() throws IOException, JDOMException {
        logger.info("Bridge configuration being loaded from " + configFile.getAbsolutePath());
        loadConfigFromStream(new FileInputStream(configFile));
    }
    
    void loadConfigFromStream(InputStream inputStream) throws JDOMException {
        Element rootElement = JDomUtilities.elementForInputStream(inputStream);
        defineSystemPropertiesFromRootElement(rootElement);
        bridgeName = rootElement.getAttributeValue(NAME_TAG);
        List<Element> elements = rootElement.getChild("Networks").getChildren(NetworkConfig.ROOT_ELEMENT_NAME);
        networkElements = new ArrayList<NetworkConfig>();
        for (Element element : elements)
            networkElements.add(NetworkConfig.networkConfigForJdomElement(element));
        addressExclusionList = ControlExclusionAddresses.controlExclusionAddressesForJDomElement(rootElement.getChild(ControlExclusionAddresses.ROOT_ELEMENT_NAME));
        loadFieldTypes(rootElement);
    }
    
    private void defineSystemPropertiesFromRootElement(Element rootElement) {
        PropertyHelper propertyHelper = new PropertyHelper();
        Element propertiesElement = rootElement.getChild(PROPERTIES_TAG);
        if (propertiesElement != null) {
            List<Element> properties = propertiesElement.getChildren(PROPERTY_TAG);
            for (Element element : properties) {
                String name = element.getAttributeValue(NAME_TAG);
                String value = element.getAttributeValue(VALUE_TAG);
                if (StringUtilities.isValidInputString(name) && StringUtilities.isValidInputString(value)) 
                    propertyHelper.defineProperty(name, value);
                else
                    logger.warn("Property element has incorrect format: " + JDomUtilities.xmlStringForElement(element));
            }
        }
    }

    private void notifyChangeListeners() {
        ConfigurationChangeListener[] listeners;
        synchronized (changeListeners) {
            listeners = new ConfigurationChangeListener[changeListeners.size()];
            changeListeners.toArray(listeners);
        }
        for (ConfigurationChangeListener listener : listeners)
            try {
                listener.configurationChanged();
            } catch (Throwable e) {
                logger.error("Unhandled exception detected from ConfigurationChangeListener", e);
            }
    }

    private class MyFileChangeHandler implements FileChangeHandler {
        @Override
        public void fileHasBeenModified(File monitoredFile) {
            try {
                loadConfigFile();
                notifyChangeListeners();
            } catch (IOException | JDOMException e) {
                logger.error("Unable to load configuration from file " + configFile.getAbsolutePath(), e);
            }
        }
    }
    
    private boolean isDomainConfig() {
        return System.getProperties().containsKey("jboss.domain.config.dir");
    }
    
    private void loadFieldTypes(Element rootElement) {
        fieldTypes = new ArrayList<FieldType>();
        Element fieldTypesElement = rootElement.getChild(FIELD_TYPES_TAG);
        if (fieldTypesElement != null) {
            List<Element> fieldTypeElements = fieldTypesElement.getChildren(FIELD_TYPE_TAG);
            for (Element element : fieldTypeElements) {
                try {
                    FieldType fieldType = FieldType.fieldTypeForElement(element);
                    fieldTypes.add(fieldType);
                } catch (IllegalArgumentException e) {
                    String elStr = JDomUtilities.xmlStringForElement(element);
                    logger.warn("Unable to parse element " + elStr + ": " + e.toString());
                }
            }
        }
    }

}

