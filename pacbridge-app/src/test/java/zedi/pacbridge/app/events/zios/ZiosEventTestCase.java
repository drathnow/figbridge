package zedi.pacbridge.app.events.zios;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.jdom2.Element;
import org.xml.sax.SAXException;

import zedi.pacbridge.app.events.Event;
import zedi.pacbridge.app.events.EventQualifier;
import zedi.pacbridge.app.events.WriteValueElement;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.values.ZapDataType;

public abstract class ZiosEventTestCase extends BaseTestCase {

    public static final Long EVENT_ID = 12L;
    public static final String NUID = "ZIOS-1234";
    public static final Integer NETWORK_NUMBER = 2;
    public static final Long INDEX = 42L;
    public static final Integer POLLSET_NUMBER = 23;
    public static final ZapDataType DATA_TYPE = ZapDataType.Float;
    public static final Float VALUE = 4.5f;
    public static final String FIRMWARE_VERSION = "100"; 
    public static final SiteAddress SITE_ADDRESS = new NuidSiteAddress(NUID, NETWORK_NUMBER);
    
    private SAXException xmlValidationException;    

    protected Element writeIoPointsElementForTest() {
        Element eventElement = deviceEventRootElement(ZiosEventName.WriteIOPoints, EventQualifier.ZIOS);
        Element element = new Element(ZiosEventName.WriteIOPoints.getName());
        Element writeValueElement = new Element(WriteValueElement.ROOT_ELEMENT_NAME);
        writeValueElement.addContent(new Element(WriteValueElement.DATA_TYPE_TAG).setText(DATA_TYPE.getName()));
        writeValueElement.addContent(new Element(WriteValueElement.INDEX_TAG).setText(""+INDEX));
        writeValueElement.addContent(new Element(WriteValueElement.VALUE_TAG).setText(VALUE.toString()));
        element.addContent(writeValueElement);
        eventElement.addContent(element);
        return eventElement;
    }
    
    protected Element DeviceEventRootElement() {
        Element rootElement = new Element(Event.ROOT_ELEMENT_NAME);
        rootElement.setAttribute(DeviceEvent.NAME_TAG, "foo");
        rootElement.setAttribute(DeviceEvent.QUALIFIER_TAG, "bar");
        rootElement.addContent(new Element(DeviceEvent.EVENT_ID_TAG).setText(EVENT_ID.toString()));
        rootElement.addContent(new Element(DeviceEvent.NUID_TAG).setText(NUID));
        rootElement.addContent(new Element(DeviceEvent.NETWORK_NUMBER_TAG).setText( "" + NETWORK_NUMBER));
        return rootElement;
    }
    
    protected Element deviceEventRootElement(ZiosEventName eventName, EventQualifier eventQualifier) {
        Element rootElement = new Element(ZiosEvent.ROOT_ELEMENT_NAME);
        rootElement.setAttribute(DeviceEvent.NAME_TAG, eventName.getName());
        rootElement.setAttribute(DeviceEvent.QUALIFIER_TAG, eventQualifier.getName());
        
        rootElement.addContent(new Element(DeviceEvent.EVENT_ID_TAG).setText(EVENT_ID.toString()));
        rootElement.addContent(new Element(DeviceEvent.NUID_TAG).setText(NUID));
        rootElement.addContent(new Element(DeviceEvent.NETWORK_NUMBER_TAG).setText( "" + NETWORK_NUMBER));
        
        return rootElement;
    }
    
    protected String xmlValidationException() {
        return xmlValidationException == null ? null : xmlValidationException.toString();
    }
    
    protected void assertIsValidXml(String xmlString) {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        InputStream inputStream = ZiosEventTestCase.class.getResourceAsStream("/zedi/pacbridge/app/events/ZiosEvent.xsd");
        Source source = new StreamSource(inputStream);
        try {
            Schema schema = factory.newSchema(source);
            Validator validator = schema.newValidator();
            source = new StreamSource(new ByteArrayInputStream(xmlString.getBytes()));
            xmlValidationException = null;
            validator.validate(source);
        } catch (Exception ex) {
            fail(ex.toString());
        }
    }
    
    
    protected boolean isValidXml(String xmlString) throws Exception {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        
        InputStream inputStream = ZiosEventTestCase.class.getResourceAsStream("/zedi/pacbridge/app/events/ZiosEvent.xsd");
        Source source = new StreamSource(inputStream);
        Schema schema = factory.newSchema(source);
        
        Validator validator = schema.newValidator();
        source = new StreamSource(new ByteArrayInputStream(xmlString.getBytes()));
        try {
            xmlValidationException = null;
            validator.validate(source);
            return true;
        } catch (SAXException ex) {
            xmlValidationException = ex;
            return false;
        }
        
    }
    
    
}
