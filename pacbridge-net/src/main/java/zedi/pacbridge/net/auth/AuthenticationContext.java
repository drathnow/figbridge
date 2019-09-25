package zedi.pacbridge.net.auth;

import java.net.InetSocketAddress;

import zedi.pacbridge.utl.SiteAddress;

public class AuthenticationContext {
    private SiteAddress siteAddress;
    private EncryptionContext encryptionContext;
    private CompressionContext compressionContext;
    private String firmwareVersion;

    public AuthenticationContext(SiteAddress siteAddress, EncryptionContext encryptionContext, CompressionContext compressionContext, InetSocketAddress remoteAddress, String firmwareVersion) {
        this.siteAddress = siteAddress;
        this.encryptionContext = encryptionContext;
        this.compressionContext = compressionContext;
        this.firmwareVersion = firmwareVersion;
    }

    public SiteAddress getSiteAddress() {
        return siteAddress;
    }
    
    public EncryptionContext getEncryptionContext() {
        return encryptionContext;
    }
    
    public CompressionContext getCompressionContext() {
        return compressionContext;
    }
    
    public String getFirmwareVersion() {
        return firmwareVersion;
    }
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{SiteAddress = '")
                .append(siteAddress.toString())
                .append("', EncryptionType = '")
                .append(encryptionContext.encryptionType().getName())
                .append("', CompressionType ='")
                .append(compressionContext.compressionType().getName());
        
        if (firmwareVersion != null)
            stringBuilder.append("', FirmwareVersion='")
                .append(firmwareVersion);
            stringBuilder.append("'}");
        return stringBuilder.toString();
    }
}
