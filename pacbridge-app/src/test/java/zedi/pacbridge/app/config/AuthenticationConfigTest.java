package zedi.pacbridge.app.config;

import static org.junit.Assert.assertEquals;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.JDomUtilities;

public class AuthenticationConfigTest extends BaseTestCase {

    private static final String XML_CONFIG1 = "<Authentication name=\"zap\" />";
    private static final String XML_CONFIG2 = "<Authentication name=\"zap\" >"
                                            + "    <Mode>promiscuous</Mode>"
                                            + "    <MatchNameRE>TEST*</MatchNameRE>"
                                            + "</Authentication>";
    @Test
    public void shouldParseAuthenticationElementWithOtherElements() throws Exception {
        Element element = JDomUtilities.elementForXmlString(XML_CONFIG2);
        AuthenticationConfig config = AuthenticationConfig.authenticationConfigForElement(element );
        assertEquals(2, config.getProperties().size());
        assertEquals("promiscuous", config.getProperties().get("Mode"));
        assertEquals("TEST*", config.getProperties().get("MatchNameRE"));
    }

    @Test
    public void shouldParsePlainAuthenticationElement() throws Exception {
        Element element = JDomUtilities.elementForXmlString(XML_CONFIG1);
        AuthenticationConfig config = AuthenticationConfig.authenticationConfigForElement(element );
        assertEquals(0, config.getProperties().size());
    }
}
