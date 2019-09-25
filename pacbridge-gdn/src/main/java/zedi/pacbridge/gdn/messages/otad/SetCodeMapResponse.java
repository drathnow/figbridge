package zedi.pacbridge.gdn.messages.otad;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.gdn.otad.CodeMap;
import zedi.pacbridge.utl.io.Unsigned;

public class SetCodeMapResponse  extends OtadResponse implements Serializable {
    private static final long serialVersionUID = 1001L;
    public static final Integer FIXED_SIZE = 7;
    
    private Integer identifier;
    private CodeMap codeMap;
    
    private SetCodeMapResponse(OtadMessageHeader messageHeader, Integer identifier, CodeMap codeMap) {
        super(messageHeader);
        this.identifier = identifier;
        this.codeMap = codeMap;
    }

    public Integer getIdentifier() {
        return identifier;
    }
    
    public CodeMap getCodeMap() {
        return codeMap;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        messageHeader.serialize(byteBuffer);
        byteBuffer.putShort((byte)(FIXED_SIZE + codeMap.getLength()));
        byteBuffer.putShort(identifier.shortValue());
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)0);
        for (byte b : codeMap.getMapBytes())
            byteBuffer.put(b);
    }

    public static final SetCodeMapResponse setCodeMapResponsedFromByteBuffer(ByteBuffer byteBuffer) {
        OtadMessageHeader messageHeader = OtadMessageHeader.messageHeaderFromByteBuffer(byteBuffer);
        if (messageHeader.isResponse() && messageHeader.getMessageType() != OtadMessageType.SetCodeMap)
            throw new IllegalArgumentException("ByteBuffer does not contain SetCodeMapCommand");
        byteBuffer.getShort(); // Ignore the size
        Integer identifier = Unsigned.getUnsignedShort(byteBuffer);
        byteBuffer.getShort(); // Don't use these bytes
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        return new SetCodeMapResponse(messageHeader, identifier, new CodeMap(bytes));
    }
}
