package zedi.pacbridge.app.events.zios;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.config.BridgeConfiguration;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.zap.messages.FieldType;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;

@Startup
@Singleton(name = "FieldTypeLibrary")
public class ZiosFieldTypeLibrary implements FieldTypeLibrary, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ZiosFieldTypeLibrary.class.getName());

    private Map<Integer, FieldType> tagToFieldTypeMap;
    private Map<String, FieldType> nameToFieldTypeMap;
    private BridgeConfiguration bridgeConfiguration;

    public ZiosFieldTypeLibrary(InputStream inputStream) {
        this();
        loadFieldTypes(inputStream);
    }
    
    @Inject
    public ZiosFieldTypeLibrary(BridgeConfiguration bridgeConfiguration) {
        this.bridgeConfiguration = bridgeConfiguration;
        this.tagToFieldTypeMap = new TreeMap<Integer, FieldType>();
        this.nameToFieldTypeMap = new TreeMap<String, FieldType>();
    }
    
    public ZiosFieldTypeLibrary() {
        this.tagToFieldTypeMap = new TreeMap<Integer, FieldType>();
        this.nameToFieldTypeMap = new TreeMap<String, FieldType>();
    }
    
    @Override
    public FieldType fieldTypeForName(String name) {
        return nameToFieldTypeMap.get(name);
    }

    @Override
    public FieldType fieldTypeForTag(Integer tag) {
        return tagToFieldTypeMap.get(tag);
    }
    
    @PostConstruct
    void loadFieldTypes() {
        InputStream inputStream = FieldTypeLibrary.class.getResourceAsStream("/zedi/pacbridge/zap/messages/FieldTypes.xml");
        loadFieldTypes(inputStream);
    }
    
    @Override
    public Collection<FieldType> getFieldTypes() {
        return new TreeSet<FieldType>(tagToFieldTypeMap.values());
    }

    void loadFieldTypes(InputStream inputStream) {
        try {
            Element rootElement = JDomUtilities.elementForInputStream(inputStream);
            List<Element> fieldTypes = rootElement.getChildren("FieldType");
            for (Element element : fieldTypes) {
                try {
                    FieldType fieldType = FieldType.fieldTypeForElement(element);
                    tagToFieldTypeMap.put(fieldType.getTag(), fieldType);
                    nameToFieldTypeMap.put(fieldType.getName(), fieldType);
                } catch (IllegalArgumentException e) {
                    logger.error("Unable to process FieldType element:\n" + JDomUtilities.xmlStringForElement(element) + "\n" + e.toString());
                }
            }
            mergeWithConfigurationFieldTypes();
        } catch (JDOMException e) {
            throw new RuntimeException("Unable to process FieldType definition file", e);
        }
    }
    
    private void mergeWithConfigurationFieldTypes() {
        if (bridgeConfiguration != null) {
            List<FieldType> fieldTypes = bridgeConfiguration.getFieldTypes();
            for (FieldType fieldType : fieldTypes) {
                if (tagToFieldTypeMap.get(fieldType.getTag()) != null)
                    logger.warn("Unable to merge field type named '" + fieldType.getName() + ". Tag collides with existing Field Type");
                else if (nameToFieldTypeMap.get(fieldType.getName()) != null)
                    logger.warn("Unable to merge field type named '" + fieldType.getName() + ". Name collides with existing Field Type");
                else {
                    tagToFieldTypeMap.put(fieldType.getTag(), fieldType);
                    nameToFieldTypeMap.put(fieldType.getName(), fieldType);
                }
            }
        }
    }
}
