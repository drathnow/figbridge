package zedi.pacbridge.app.events;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.Utilities;

public class XsdTest extends BaseTestCase {

    @Test
    public void shouldValidateConfigureXmls() throws Exception {
        EventXmlValidator validator = new EventXmlValidator();
        System.out.println("XML: " + Utilities.stringForResourceNamed("/zedi/pacbridge/app/events/examples/zios/Configure.xml"));
        validator.isValidXml(Utilities.stringForResourceNamed("/zedi/pacbridge/app/events/examples/zios/Configure.xml"));
        assertNull(validator.getLastError(), validator.getLastError());
    }

    @Test
    public void shouldValidateConfigureResponseXmls() throws Exception {
        EventXmlValidator validator = new EventXmlValidator();
        System.out.println("XML: " + Utilities.stringForResourceNamed("/zedi/pacbridge/app/events/examples/zios/ConfigureResponse.xml"));
        validator.isValidXml(Utilities.stringForResourceNamed("/zedi/pacbridge/app/events/examples/zios/ConfigureResponse.xml"));
        assertNull(validator.getLastError(), validator.getLastError());
    }
}
