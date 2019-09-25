package zedi.pacbridge.app.zap;

import org.jdom2.Element;

import zedi.pacbridge.app.config.ProtocolConfig;

public class ZapProtocolConfig extends ProtocolConfig {
    public static final Integer DEFAULT_PACKET_SIZE = 2048;
    
    public static final String PROTOCOL_NAME = "zap";
    public static final String MAX_PACKET_SIZE_TAG = "MaxPacketSize";

    private Integer maxPacketSize;
    private ZapAuthenticationConfig authenticationConfig;
    
    private ZapProtocolConfig(Integer maxPacketSize, ZapAuthenticationConfig authenticationConfig) {
        super(PROTOCOL_NAME);
        this.maxPacketSize = maxPacketSize;
        this.authenticationConfig = authenticationConfig;
    }

    public Integer getMaxPacketSize() {
        return maxPacketSize;
    }

    public ZapAuthenticationConfig getAuthenticationConfig() {
        return authenticationConfig;
    }

    public static ZapProtocolConfig protocolConfigForElement(Element element) {
        Integer maxPacketSize = DEFAULT_PACKET_SIZE;
        String maxPacketSizeString = element.getChildText(MAX_PACKET_SIZE_TAG);
        if (maxPacketSizeString != null)
            maxPacketSize = Integer.valueOf(maxPacketSizeString);
        Element authElement = element.getChild(ZapAuthenticationConfig.ROOT_ELEMENT_TAG);
        ZapAuthenticationConfig authenticationConfig = ZapAuthenticationConfig.authenticationConfigForElement(authElement);
        return new ZapProtocolConfig(maxPacketSize, authenticationConfig);
    }
}
