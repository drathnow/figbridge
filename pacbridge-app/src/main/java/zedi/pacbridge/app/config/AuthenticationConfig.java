package zedi.pacbridge.app.config;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.jdom2.Element;

import zedi.pacbridge.utl.StringToObjectConverter;

public class AuthenticationConfig {
    public static final String ROOT_ELEMENT_NAME = "Authentication";
    public static final String NAME_ATTRIBUTE = "name";
    
    private Map<String, Object> properties;
    private String typeName;
    
    private AuthenticationConfig() {
        properties = new TreeMap<String, Object>();
    }
    
    public String getTypeName() {
        return typeName;
    }

    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    static AuthenticationConfig authenticationConfigForElement(Element rootElement) {
        AuthenticationConfig config = new AuthenticationConfig();
        config.typeName = rootElement.getAttributeValue(NAME_ATTRIBUTE);
        for (Element element : rootElement.getChildren()) {
            String key = element.getName();
            String value = element.getText();
            config.properties.put(key, StringToObjectConverter.objectForString(value));
        }
        return config;
    }
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Authentication: name = '")
                     .append(typeName)
                     .append("'\n")
                     .append("   Properties:\n");
        for (String key : properties.keySet())
            stringBuilder.append("        ")
                         .append(key)
                         .append("=")
                         .append(properties.get(key))
                         .append("\n");
        return stringBuilder.toString();
    }
}
