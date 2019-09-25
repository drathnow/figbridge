package zedi.pacbridge.app.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.ejb.Stateless;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

@Stateless
public class EventDocumentFactory {
    
    private SAXBuilder saxBuilder;

    public EventDocumentFactory() {
        saxBuilder = new SAXBuilder();
    }
    
    public Document documentForXmlString(String xmlString) throws JDOMException, IOException {
        return saxBuilder.build(new ByteArrayInputStream(xmlString.getBytes()));
    }
}
