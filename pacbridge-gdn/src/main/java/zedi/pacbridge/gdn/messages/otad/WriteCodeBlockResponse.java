package zedi.pacbridge.gdn.messages.otad;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.utl.io.Unsigned;


public class WriteCodeBlockResponse extends OtadResponse implements Serializable {
    private static final long serialVersionUID = 1001L;
    public static final int FIXED_SIZE = 12;

    private Integer identifier;
    private Integer address;
    private Integer blockType = 0;
    private Integer blockLength;

    private WriteCodeBlockResponse(OtadMessageHeader messageHeader, Integer identifier, Integer address, Integer blockLength) {
        super(messageHeader);
        this.identifier = identifier;
        this.address = address;
        this.blockLength = blockLength;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public Integer getAddress() {
        return address;
    }

    public Integer getBlockType() {
        return blockType;
    }

    public Integer getBlockLength() {
        return blockLength;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        messageHeader.serialize(byteBuffer);
        byteBuffer.putShort((short)FIXED_SIZE);
        byteBuffer.putShort(identifier.shortValue());
        byteBuffer.put(blockType.byteValue());
        byteBuffer.putInt(address);
        byteBuffer.putShort(blockLength.shortValue());
    }

    public static final WriteCodeBlockResponse writeCodeBlockResponseFromByteBuffer(ByteBuffer byteBuffer) {
        OtadMessageHeader messageHeader = OtadMessageHeader.messageHeaderFromByteBuffer(byteBuffer);
        byteBuffer.getShort();
        Integer identifier = Unsigned.getUnsignedShort(byteBuffer);
        byteBuffer.get(); // Skip the block type - not used
        Integer address = (int)Unsigned.getUnsignedInt(byteBuffer);
        Integer blockLength = (int)byteBuffer.getShort();
        return new WriteCodeBlockResponse(messageHeader, identifier, address, blockLength);
    }
}
