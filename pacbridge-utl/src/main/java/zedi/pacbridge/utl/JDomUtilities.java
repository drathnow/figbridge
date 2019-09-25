package zedi.pacbridge.utl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDomUtilities {
    private static Logger logger = LoggerFactory.getLogger(JDomUtilities.class.getName());
    public static final String DEFAULT_DATE_FORMAT_STRING = "yyyy-MMM-dd HH:mm:ss";
    public static final String TAG_DATE_FORMAT = "dateFormat";
    public final static String PROPERTY_TAG = "property";
    private static final String TAG_NAME = "name";
    private static final String TAG_VALUE = "value";
    private static final XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
    private static final XMLOutputter rawXmlOutputter = new XMLOutputter(Format.getRawFormat());
    private static SAXBuilder saxBuilder = new SAXBuilder();

    public static void defineSystemPropertiesFromRootElement(Element element) {
        defineSystemPropertiesFromRootElement(element, false);
    }

    public static void defineSystemPropertiesFromRootElement(Element element, boolean override) {
        defineSystemPropertiesFromRootElement(element, override, PROPERTY_TAG);
    }

    public static void defineSystemPropertiesFromRootElement(Element element, boolean override, String propertyTag) {
        List<Element> elements = element.getChildren(propertyTag);
        for (Element propertyElement : elements) {
            String name = propertyElement.getAttributeValue(TAG_NAME);
            if (override || System.getProperty(name) == null)
                System.setProperty(name, propertyElement.getAttributeValue(TAG_VALUE));
        }
    }

    public static Properties propertiesFromElement(Element element, String propertyTag) {
        List<Element> elements = element.getChildren(propertyTag);
        Properties properties = new Properties();
        for (Element propertyElement : elements) {
            String name = propertyElement.getAttributeValue(TAG_NAME);
            properties.setProperty(name, propertyElement.getAttributeValue(TAG_VALUE));
        }
        return properties;
    }
    
    public static Document jdomDocumentForXmlString(String anXmlString) throws JDOMException {
        SAXBuilder documentBulder = new SAXBuilder();
        try {
            return documentBulder.build(new ByteArrayInputStream(anXmlString.getBytes()));
        } catch (IOException e) {
            throw new JDOMException("Unable to build document. " + e.toString());
        }
    }

    public static String documentAsXmlString(Document aDocument) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        synchronized (xmlOutputter) {
            xmlOutputter.output(aDocument, byteArrayOutputStream);
        }
        return byteArrayOutputStream.toString();
    }

    public static String xmlStringForElement(Element anElement) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        synchronized (xmlOutputter) {
            try {
                xmlOutputter.output(anElement, byteArrayOutputStream);
            } catch (IOException e) {
                throw new RuntimeException("Unable to decode element",e);
            }
        }
        return byteArrayOutputStream.toString();
    }

    public static String xmlStringForElement(Element anElement, boolean raw) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        synchronized (xmlOutputter) {
            try {
                if (raw)
                    rawXmlOutputter.output(anElement, byteArrayOutputStream);
                else 
                    xmlOutputter.output(anElement, byteArrayOutputStream);
            } catch (IOException e) {
                throw new RuntimeException("Unable to decode element",e);
            }
        }
        return byteArrayOutputStream.toString();
    }

    public static void writeDocumentToStream(Document aDocument, OutputStream anOutputStream) throws IOException {
        synchronized (xmlOutputter) {
            xmlOutputter.output(aDocument, anOutputStream);
        }
    }

    public static void writeElementToStream(Element element, OutputStream anOutputStream) throws IOException {
        synchronized (xmlOutputter) {
            xmlOutputter.output(element, anOutputStream);
        }
    }

    public static Element elementForInputStream(InputStream anInputStream) throws JDOMException {
        synchronized (saxBuilder) {
            try {
                Document document = saxBuilder.build(anInputStream);
                return document.getRootElement();
            } catch (IOException e) {
                throw new JDOMException("Unable to build document. " + e.toString());
            }
        }
    }

    public static Date dateForTimestampElement(Element aTimestampElement) {
        String formatString = aTimestampElement.getAttributeValue(TAG_DATE_FORMAT);
        try {
            if (formatString == null)
                throw new ParseException("No date format specified", 0);
            SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
            return dateFormat.parse(aTimestampElement.getText());
        } catch (ParseException e) {
            logger.error("Unable to parse date string '" + aTimestampElement.getText() + "' with format '" + formatString + " - " + e.toString());
            return null;
        }
    }

    public static Element elementForXmlString(String anXMLString) throws JDOMException, IOException {
        return JDomUtilities.elementForInputStream(new ByteArrayInputStream(anXMLString.getBytes()));
    }

    public static Element timestampElementForDateWithFormat(String aTagName, Date aDate, String aDateFormatString) {
        Element timestampElement = new Element(aTagName);
        SimpleDateFormat dateFormat = new SimpleDateFormat(aDateFormatString);
        timestampElement.setText(dateFormat.format(aDate));
        timestampElement.setAttribute(TAG_DATE_FORMAT, aDateFormatString);
        return timestampElement;
    }
}
