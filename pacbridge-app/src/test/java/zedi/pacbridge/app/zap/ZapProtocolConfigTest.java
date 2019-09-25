package zedi.pacbridge.app.zap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.JDomUtilities;

public class ZapProtocolConfigTest extends BaseTestCase {

    private static final String XML_CONFIG_WITH_AUTH1 = "<Protocol name=\"zap\" >"
                                                      + "    <MaxPacketSize>2048</MaxPacketSize>"
                                                      + "    <Authentication>" 
                                                      + "        <Mode>promiscuous</Mode>"
                                                      + "        <MatchNameRE>TEST*</MatchNameRE>"
                                                      + "    </Authentication>"
                                                      + "</Protocol>";
    
    private static final String XML_CONFIG_WITH_AUTH2 = "<Protocol name=\"zap\" >"
                                                      + "    <MaxPacketSize>2048</MaxPacketSize>"
                                                      + "    <Authentication>" 
                                                      + "        <Mode>promiscuous</Mode>"
                                                      + "    </Authentication>"
                                                      + "</Protocol>";

    private static final String XML_CONFIG_WITH_DEF_AUTH = "<Protocol name=\"zap\" >"
                                                         + "    <Authentication/>" 
                                                         + "</Protocol>";

    private static final String XML_CONFIG_WITH_NOAUTH = "<Protocol name=\"zap\" >"
                                                       + "    <MaxPacketSize>1048</MaxPacketSize>"
                                                       + "</Protocol>";

    private static final String XML_CONFIG_DEFAULT = "<Protocol name=\"zap\" />";
    
    @Test
    public void shouldParseProtocolConfigWithAuthenticationFullElement() throws Exception {
        Element element = JDomUtilities.elementForXmlString(XML_CONFIG_WITH_AUTH1);
        ZapProtocolConfig config = ZapProtocolConfig.protocolConfigForElement(element);
        assertEquals(2048, config.getMaxPacketSize().intValue());
        ZapAuthenticationConfig authConfig = config.getAuthenticationConfig();
        assertNotNull(authConfig);
        assertEquals(ZapAuthenticationMode.Promiscuous, authConfig.getAuthenticationMode());
        assertEquals("TEST*", authConfig.getMatchNameRe());
    }

    @Test
    public void shouldParseProtocolConfigWithPartialAuthenticationElement() throws Exception {
        Element element = JDomUtilities.elementForXmlString(XML_CONFIG_WITH_AUTH2);
        ZapProtocolConfig config = ZapProtocolConfig.protocolConfigForElement(element);
        assertEquals(2048, config.getMaxPacketSize().intValue());
        ZapAuthenticationConfig authConfig = config.getAuthenticationConfig();
        assertNotNull(authConfig);
        assertEquals(ZapAuthenticationMode.Promiscuous, authConfig.getAuthenticationMode());
        assertEquals(null, authConfig.getMatchNameRe());
    }

    @Test
    public void shouldParseProtocolConfigWithDefaultAuthenticationElement() throws Exception {
        Element element = JDomUtilities.elementForXmlString(XML_CONFIG_WITH_DEF_AUTH);
        ZapProtocolConfig config = ZapProtocolConfig.protocolConfigForElement(element);
        assertEquals(2048, config.getMaxPacketSize().intValue());
        ZapAuthenticationConfig authConfig = config.getAuthenticationConfig();
        assertNotNull(authConfig);
        assertEquals(ZapAuthenticationMode.None, authConfig.getAuthenticationMode());
        assertNull(authConfig.getMatchNameRe());
    }
    
    @Test
    public void shouldParseProtocolConfigWithNoAuthenticationElement() throws Exception {
        Element element = JDomUtilities.elementForXmlString(XML_CONFIG_WITH_NOAUTH);
        ZapProtocolConfig config = ZapProtocolConfig.protocolConfigForElement(element);
        assertEquals(1048, config.getMaxPacketSize().intValue());
        ZapAuthenticationConfig authConfig = config.getAuthenticationConfig();
        assertNotNull(authConfig);
        assertEquals(ZapAuthenticationMode.None, authConfig.getAuthenticationMode());
        assertNull(authConfig.getMatchNameRe());
    }

    @Test
    public void shouldParseProtocolConfigWithDefaultsElement() throws Exception {
        Element element = JDomUtilities.elementForXmlString(XML_CONFIG_DEFAULT);
        ZapProtocolConfig config = ZapProtocolConfig.protocolConfigForElement(element);
        assertEquals(ZapProtocolConfig.DEFAULT_PACKET_SIZE.intValue(), config.getMaxPacketSize().intValue());
        ZapAuthenticationConfig authConfig = config.getAuthenticationConfig();
        assertNotNull(authConfig);
        assertEquals(ZapAuthenticationMode.None, authConfig.getAuthenticationMode());
        assertNull(authConfig.getMatchNameRe());
    }

}
