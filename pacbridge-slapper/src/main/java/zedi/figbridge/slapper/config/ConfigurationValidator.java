package zedi.figbridge.slapper.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;


public class ConfigurationValidator {

    public void validateXmlConfig(String xmlConfig) throws Exception {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        InputStream inputStream = getClass().getResourceAsStream("/zedi/figbridge/slapper/config/Configuration.xsd");
        Source source = new StreamSource(inputStream);
        Schema schema = factory.newSchema(source);
        Validator validator = schema.newValidator();
        source = new StreamSource(new ByteArrayInputStream(xmlConfig.getBytes()));
        validator.validate(source);
    }
}
