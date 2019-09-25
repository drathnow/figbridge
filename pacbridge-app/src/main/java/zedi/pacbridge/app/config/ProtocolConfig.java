package zedi.pacbridge.app.config;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.jdom2.Element;

import zedi.pacbridge.app.zap.ZapProtocolConfig;

public class ProtocolConfig {
    public static final String ROOT_ELEMENT_NAME = "Protocol";
    public static final String NAME_ATTRIBUTE = "name";
    
    private String name;
    protected Map<String, Object> properties;
    
    protected ProtocolConfig(String name) {
        this.name = name;
        this.properties = new TreeMap<String, Object>();
    }
    
    public String getName() {
        return name;
    }

    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @SuppressWarnings("unchecked")
    public <T> T valueForPropertyName(String propertyName, T defaultValue) {
        T value = (T)properties.get(propertyName);
        return value == null ? defaultValue : value;
    }
    
    static ProtocolConfig protocolConfigForElement(Element rootElement) {
        String name = rootElement.getAttributeValue(NAME_ATTRIBUTE);
        if (name.equalsIgnoreCase("zap"))
            return ZapProtocolConfig.protocolConfigForElement(rootElement);
        throw new IllegalArgumentException("Unknown protocol name '" + name + "'");
    }
}
