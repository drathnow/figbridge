package zedi.pacbridge.app.events;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ejb.Stateless;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * Validates an Event XML against an XSD.
 *
 */
@Stateless
public class EventXmlValidator {
    private Validator validator;
    private String lastError;
    
    public EventXmlValidator() {
        this(EventXmlValidator.class.getResourceAsStream("/zedi/pacbridge/app/events/ZiosEvent.xsd"));
    }
    
    public EventXmlValidator(InputStream inputStream) {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Source source = new StreamSource(inputStream);
        try {
            validator = factory.newSchema(source).newValidator();
        } catch (SAXException e) {
            throw new RuntimeException("Unable to create XML validator", e);
        }
    }

    /**
     * Returns the reason for why the last call to {@link isValidXml} returned <code>false</code>
     * @return
     */
    public String getLastError() {
        return lastError;
    }
    
    /**
     * Validates XML event against the XSD.
     * 
     * @param xmlString
     * @return <code>true</code> - everythings is good.  <code>false</code> - the XML is not valid.
     * you can retrive the reason by calling the {@link getLastError()} method. 
     * @throws SAXException - thrown by the underlying validator.  Should be considered fatal.
     * @throws IOException - thrown by the underlying validator. Should be considered fatal.
     */
    public boolean isValidXml(String xmlString) {
        Source source = new StreamSource(new ByteArrayInputStream(xmlString.getBytes()));
        try {
            validator.validate(source);
            return true;
        } catch (SAXException | IOException e) {
            lastError = e.toString();
            return false;
        }
    }
}
