package zedi.pacbridge.app.events.connect;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.jdom2.Element;
import org.junit.Assert;
import org.xml.sax.SAXException;

import zedi.pacbridge.app.events.EventQualifier;
import zedi.pacbridge.gdn.GdnDataType;

public abstract class EventTestCase {
    
    public static final Long EVENT_ID = 12L;
    public static final Integer FIRMWARE_VERSION = 344;
    public static final String ADDRESS = "1.2.3.4";
    public static final String SERIAL_NUMBER = "234234";
    public static final Integer NETWORK_NUMBER = 2;
    public static final Integer INDEX = 42;
    public static final Integer POLLSET_NUMBER = 23;
    public static final GdnDataType DATA_TYPE = GdnDataType.Float;
    public static final Float VALUE = Float.valueOf(4.5f);
    
    protected Element siteEventRootElement() {
        Element rootElement = new Element(SiteEvent.ROOT_ELEMENT_NAME);
        rootElement.setAttribute(SiteEvent.NAME_TAG, "foo");
        rootElement.setAttribute(SiteEvent.QUALIFIER_TAG, "bar");
        rootElement.addContent(new Element(SiteEvent.EVENT_ID_TAG).setText(EVENT_ID.toString()));
        rootElement.addContent(new Element(SiteEvent.IP_ADDRESS_TAG).setText(ADDRESS));
        rootElement.addContent(new Element(SiteEvent.NETWORK_NUMBER_TAG).setText( "" + NETWORK_NUMBER));
        rootElement.addContent(new Element(SiteEvent.SERIAL_NUMBER_TAG).setText(SERIAL_NUMBER));
        rootElement.addContent(new Element(SiteEvent.FIRMWARE_VERSION_TAG).setText("" + FIRMWARE_VERSION));
        return rootElement;
    }
    
    protected Element siteEventRootElement(ConnectEventName eventName, EventQualifier eventQualifier) {
        Element rootElement = new Element(SiteEvent.ROOT_ELEMENT_NAME);
        rootElement.setAttribute(SiteEvent.NAME_TAG, eventName.getName());
        rootElement.setAttribute(SiteEvent.QUALIFIER_TAG, eventQualifier.getName());
        
        rootElement.addContent(new Element(SiteEvent.EVENT_ID_TAG).setText(EVENT_ID.toString()));
        rootElement.addContent(new Element(SiteEvent.IP_ADDRESS_TAG).setText(ADDRESS));
        rootElement.addContent(new Element(SiteEvent.NETWORK_NUMBER_TAG).setText( "" + NETWORK_NUMBER));
        rootElement.addContent(new Element(SiteEvent.SERIAL_NUMBER_TAG).setText(SERIAL_NUMBER));
        rootElement.addContent(new Element(SiteEvent.FIRMWARE_VERSION_TAG).setText("" + FIRMWARE_VERSION));
        
        return rootElement;
    }
    
    protected boolean isValidXml(String xmlString) throws Exception {
        
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        
        InputStream inputStream = EventTestCase.class.getResourceAsStream("/zedi/pacbridge/app/events/ConnectEvent.xsd");
        Source source = new StreamSource(inputStream);
        Schema schema = factory.newSchema(source);
        
        Validator validator = schema.newValidator();
        source = new StreamSource(new ByteArrayInputStream(xmlString.getBytes()));
        try {
            validator.validate(source);
            return true;
        } catch (SAXException ex) {
            Assert.fail(ex.getMessage());
            return false;
        }
        
    }
    
    
}
