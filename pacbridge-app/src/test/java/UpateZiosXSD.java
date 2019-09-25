import java.io.InputStream;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.JDOMException;

import zedi.pacbridge.app.events.EventXmlValidator;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;


public class UpateZiosXSD {

    interface ElementMatcher {
        public boolean matchesElement(Element element);
    }
    
    private static Element findElement(Element root, ElementMatcher elementMatcher) {
        if (root != null && elementMatcher.matchesElement(root)) {
            return root;
        } else {
            List<Element> children = root.getChildren();
            for (Element element : children) {
                Element r;
                if ((r = findElement(element, elementMatcher)) != null)
                    return r;
            }
        }
        return null;
    }
    
    static String xsTypeStringForType(String type) {
        if (type.equals("string"))
            return "xs:string";
        if (type.equals("S48") || type.equals("S64"))
            return "xs:unsignedLong";
        if (type.equals("S32") || type.equals("S48") || type.equals("S64"))
            return "xs:unsignedInt";
        if (type.equals("S16"))
            return "xs:unsignedShort";
        if (type.equals("S8"))
            return "xs:unsignedByte";
        throw new RuntimeException("Unknow type string: " + type);
    }

    static void replaceAllChildren(Element allElement) throws JDOMException {
        InputStream inputStream = FieldTypeLibrary.class.getResourceAsStream("/zedi/pacbridge/zap/messages/FieldTypes.xml");
        Element fileTypes = JDomUtilities.elementForInputStream(inputStream);
        allElement.removeChildren("element", allElement.getNamespace());
        Element corrIdElement = new Element("element", allElement.getNamespace())
                                    .setAttribute("name", "CorrelationId")
                                    .setAttribute("type", "xs:unsignedLong")
                                    .setAttribute("minOccurs", "1")
                                    .setAttribute("maxOccurs", "1");
        allElement.addContent(corrIdElement);
        for (Element fileTypeElement : fileTypes.getChildren()) {
            if (fileTypeElement.getText().equals("CorrelationId") == false) {
                String typeString = xsTypeStringForType(fileTypeElement.getAttributeValue("type"));
                Element newElement = new Element("element", allElement.getNamespace())
                                            .setAttribute("name", fileTypeElement.getText())
                                            .setAttribute("type", typeString)
                                            .setAttribute("minOccurs", "0")
                                            .setAttribute("maxOccurs", "1");
                allElement.addContent(newElement);
            }
        }
    }
    
    public static void main(String[] args) {
        InputStream inputStream = EventXmlValidator.class.getResourceAsStream("/zedi/pacbridge/app/events/ZiosEvent.xsd");
        try {
            Element rootElement = JDomUtilities.elementForInputStream(inputStream);
            Element configureElement = findElement(rootElement, new ElementMatcher() {
                @Override
                public boolean matchesElement(Element element) {
                    return element.getName().equals("element")
                    && element.getAttributeValue("name") != null 
                    && element.getAttributeValue("name").equals("Configure");
                }
            });
            Element allElement = findElement(configureElement, new ElementMatcher() {
                @Override
                public boolean matchesElement(Element element) {
                    return element.getName().equals("all");
                }
            });
            replaceAllChildren(allElement);
            System.out.println("Element: " + JDomUtilities.xmlStringForElement(rootElement));
        } catch (JDOMException e) {
            e.printStackTrace();
        }
    }
}
