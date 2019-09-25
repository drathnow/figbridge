package zedi.pacbridge.app.events.zios;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import org.junit.Test;

public class XmlValidationTest extends ZiosEventTestCase {

    @Test
    public void shouldValidateWithoutHeader() throws Exception {
        InputStream inputStream = XmlValidationTest.class.getResourceAsStream("/zedi/pacbridge/app/events/examples/zios/Configure.xml");
        assertNotNull(inputStream);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        inputStream.close();
        assertIsValidXml(new String(bytes));
    }

    @Test
    public void shouldValidateWithHeader() throws Exception {
        InputStream inputStream = XmlValidationTest.class.getResourceAsStream("/zedi/pacbridge/app/events/examples/zios/ConfigureWithXmlHeader.xml");
        assertNotNull(inputStream);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        inputStream.close();
        assertIsValidXml(new String(bytes));
    }
}
