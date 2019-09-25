package zedi.pacbridge.app.config;

import static org.junit.Assert.assertTrue;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.app.zap.ZapProtocolConfig;
import zedi.pacbridge.utl.JDomUtilities;

public class ProtocolConfigTest {

    private static final String STP_PROTOCOL_XML = 
             "<Protocol name='zap' />";
    
    @Test
    public void shouldSpitOutAString() throws Exception {
        Element element = JDomUtilities.elementForXmlString(STP_PROTOCOL_XML);
        ProtocolConfig config = ProtocolConfig.protocolConfigForElement(element);
//        System.out.println(config.toString());
    }

    @Test
    public void shouldReturnForDefinedProperty() throws Exception {
        Element element = JDomUtilities.elementForXmlString(STP_PROTOCOL_XML);
        ProtocolConfig config = ProtocolConfig.protocolConfigForElement(element);
        assertTrue(config instanceof ZapProtocolConfig);
    }

}
