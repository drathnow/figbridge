package zedi.pacbridge.gdn.messages.otad;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.utl.io.Unsigned;


public class RequestSystemInfoResponse extends OtadResponse implements Serializable {
    private static final long serialVersionUID = 1001L;
    public static final int FIXED_SIZE = 26;

    private Integer platformId;
    private Integer applicationId;
    private Integer applicationVersion;
    private Integer applicationBuild;
    private Integer rtuId;
    private Integer rtuVersion;
    private Integer networkId;
    private Integer networkVersion;
    private Integer flashSize;
    private Integer identifier;
    private byte[] codeMapBytes;

    private RequestSystemInfoResponse(OtadMessageHeader messageHeader) {
        this(messageHeader, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null);
    }
    
    private RequestSystemInfoResponse(OtadMessageHeader messageHeader, 
                              Integer platformId, 
                              Integer applicationId, 
                              Integer applicationVersion, 
                              Integer applicationBuild, 
                              Integer rtuId, 
                              Integer rtuVersion, 
                              Integer networkId, 
                              Integer networkVersion,
                              Integer flashSize, 
                              Integer identifier, 
                              byte[] codeMapBytes) {
        super(messageHeader);
        this.platformId = platformId;
        this.applicationId = applicationId;
        this.applicationVersion = applicationVersion;
        this.applicationBuild = applicationBuild;
        this.rtuId = rtuId;
        this.rtuVersion = rtuVersion;
        this.networkId = networkId;
        this.networkVersion = networkVersion;
        this.flashSize = flashSize;
        this.identifier = identifier;
        this.codeMapBytes = codeMapBytes;
    }

    public Integer getPlatformId() {
        return platformId;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public Integer getApplicationVersion() {
        return applicationVersion;
    }

    public Integer getApplicationBuild() {
        return applicationBuild;
    }

    public Integer getRtuId() {
        return rtuId;
    }

    public Integer getRtuVersion() {
        return rtuVersion;
    }

    public Integer getNetworkId() {
        return networkId;
    }

    public Integer getNetworkVersion() {
        return networkVersion;
    }

    public Integer getFlashSize() {
        return flashSize;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public byte[] getCodeMapBytes() {
        return codeMapBytes;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        // TODO Auto-generated method stub
    }

    public static final RequestSystemInfoResponse requestSystemInfoResponseFromByteBuffer(ByteBuffer byteBuffer) {

        OtadMessageHeader messageHeader = OtadMessageHeader.messageHeaderFromByteBuffer(byteBuffer);
        if (messageHeader.getMessageType() != OtadMessageType.RequestSystemInfo && messageHeader.isResponse())
            throw new IllegalArgumentException("Byte buffer does not contain a RequestSystemInfoResponse");
        int length = Unsigned.getUnsignedShort(byteBuffer);
        if (messageHeader.getErrorCode() == ErrorCode.NoError) {
            Integer platformId = (int)Unsigned.getUnsignedByte(byteBuffer);
            Integer applicationId = (int)Unsigned.getUnsignedByte(byteBuffer);
            Integer applicationVersion = Unsigned.getUnsignedShort(byteBuffer);
            Integer applicationBuild = Unsigned.getUnsignedShort(byteBuffer);
            Integer rtuId = byteBuffer.getInt();
            Integer rtuVersion = Unsigned.getUnsignedShort(byteBuffer);
            Integer networkId = byteBuffer.getInt();
            Integer networkVersion = Unsigned.getUnsignedShort(byteBuffer);
            Integer flashSize = (int)Unsigned.getUnsignedByte(byteBuffer);
            Integer identifier = Unsigned.getUnsignedShort(byteBuffer);
            byteBuffer.get(); // Not sure what this is for
            byteBuffer.get(); // Not sure what this is for
            byte[] codeMapBytes = new byte[length - FIXED_SIZE];
            byteBuffer.get(codeMapBytes);
            return new RequestSystemInfoResponse(messageHeader, 
                    platformId, 
                    applicationId, 
                    applicationVersion, 
                    applicationBuild, 
                    rtuId, 
                    rtuVersion, 
                    networkId, 
                    networkVersion,
                    flashSize, 
                    identifier, 
                    codeMapBytes);
        } else
            return new RequestSystemInfoResponse(messageHeader);
        
    }
}
