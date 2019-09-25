package zedi.pacbridge.zap.messages;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.JDomUtilities;


public class TestingFieldTypeLibrary implements FieldTypeLibrary {

    private static final Logger logger = LoggerFactory.getLogger(TestingFieldTypeLibrary.class.getName());

    private Map<Integer, FieldType> tagToFieldTypeMap;
    private Map<String, FieldType> nameToFieldTypeMap;

    public TestingFieldTypeLibrary(InputStream inputStream) {
        this.tagToFieldTypeMap = new TreeMap<Integer, FieldType>();
        this.nameToFieldTypeMap = new TreeMap<String, FieldType>();
        loadFieldTypes(inputStream);
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
        } catch (JDOMException e) {
            throw new RuntimeException("Unable to process FieldType definition file", e);
        }
    }
}
