package zedi.pacbridge.zap.messages;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.JDomUtilities;

public class FieldTypeClassGenerator {
    private static final Logger logger = LoggerFactory.getLogger(FieldTypeClassGenerator.class.getName());
    private static final String FMT = "<xs:element name=\"{0}\" type=\"{1}\" minOccurs=\"0\" maxOccurs=\"1\" />\n";
    
    public static void main(String[] args) {
        loadFieldTypes();
    }
    
    static void loadFieldTypes() {
        InputStream inputStream = FieldTypeLibrary.class.getResourceAsStream("/zedi/pacbridge/zap/messages/FieldTypes.xml");
        loadFieldTypes(inputStream);
    }

    static String xsTypeStringForType(String type) {
        if (type.equalsIgnoreCase("string"))
            return "xs:string";
        if (type.equalsIgnoreCase("S48") || type.equalsIgnoreCase("S64"))
            return "xs:unsignedLong";
        if (type.equalsIgnoreCase("S32") || type.equalsIgnoreCase("S48") || type.equalsIgnoreCase("S64"))
            return "xs:unsignedInt";
        if (type.equalsIgnoreCase("S16"))
            return "xs:unsignedShort";
        if (type.equalsIgnoreCase("S8"))
            return "xs:unsignedByte";
        throw new RuntimeException("Unknow type string: " + type);
    }
        
    static String generateXSD(List<Element> fieldTypes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<xs:element name=\"CorrelationId\" type=\"xs:unsignedLong\" minOccurs=\"0\" maxOccurs=\"1\" />\n");
        for (Element element : fieldTypes) {
            if (element.getText().equals("CorrelationId") == false)
                stringBuilder.append(MessageFormat.format(FMT, element.getText(), xsTypeStringForType(element.getAttribute("type").getValue())));
        }
        return stringBuilder.toString();
    }
    
    static String generateClass(List<Element> fieldTypes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("class FieldType {\n");
        stringBuilder.append("public:\n");
        stringBuilder.append("    enum FieldId {\n");
        for (Element element : fieldTypes) {
            try {
                FieldType fieldType = FieldType.fieldTypeForElement(element);
                stringBuilder.append("        ");
                stringBuilder.append(fieldType.getName());
                stringBuilder.append(" = ");
                stringBuilder.append(fieldType.getTag());
                stringBuilder.append(",\n");
            } catch (IllegalArgumentException e) {
                logger.error("Unable to process FieldType element:\n" + JDomUtilities.xmlStringForElement(element) + "\n" + e.toString());
            }
        }
        stringBuilder.setLength(stringBuilder.length()-2);
        stringBuilder.append("\n    };\n\n");
        stringBuilder.append("    static bool isValidFieldNumber(int fieldNumber);\n");
        stringBuilder.append("};\n");
        return stringBuilder.toString();
    }
    
    static String generateTable(List<Element> fieldTypes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Element element : fieldTypes) {
            stringBuilder.append(element.getText())
                         .append(',')
                         .append(element.getAttributeValue("type"))
                         .append("\n");
        }
        return stringBuilder.toString();
    }
    
    static String generateSwitch(List<Element> fieldTypes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("bool FieldType::isValidFieldNumber(int fieldNumber) {\n");
        stringBuilder.append("    switch(fieldNumber) {\n");
        for (Element element : fieldTypes) {
            stringBuilder.append("        case ")
                         .append(element.getText())
                         .append(" :\n");
        }
        stringBuilder.append("            return true;\n");
        stringBuilder.append("    }\n");
        stringBuilder.append("    return false;\n");
        stringBuilder.append("}\n");
        
        return stringBuilder.toString();
    }
    
    static void loadFieldTypes(InputStream inputStream) {
        try {
            Element rootElement = JDomUtilities.elementForInputStream(inputStream);
            List<Element> fieldTypes = rootElement.getChildren("FieldType");
            System.out.println(generateClass(fieldTypes));
            System.out.println(generateSwitch(fieldTypes));
            System.out.println(generateXSD(fieldTypes));
            System.out.println(generateTable(fieldTypes));
        } catch (JDOMException e) {
            throw new RuntimeException("Unable to process FieldType definition file", e);
        }
    }
}
