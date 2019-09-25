package zedi.pacbridge.gdn.messages.otad;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.io.Unsigned;


public class WriteCodeBlockCommand extends OtadCommand implements Control, Serializable {
    private static final long serialVersionUID = 1001L;
    public static final int FIXED_SIZE = 12;

    private Integer identifier;
    private Integer address;
    private Integer blockType = 0;
    private byte[] codeBlock;

    private WriteCodeBlockCommand(OtadMessageHeader messageHeader, Integer identifier, Integer address, byte[] codeBlock) { 
        super(messageHeader);
        this.identifier = identifier;
        this.address = address;
        this.codeBlock = codeBlock;
    }
    
    public WriteCodeBlockCommand(Integer identifier, Integer address, byte[] codeBlock) {
        this(new OtadMessageHeader(true, OtadMessageType.WriteCodeBlock), identifier, address, codeBlock);
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

    public byte[] getCodeBlock() {
        return codeBlock;
    }

    public Long getEventId() {
        return 0L;
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        messageHeader.serialize(byteBuffer);
        byteBuffer.putShort(size().shortValue());
        byteBuffer.putShort(identifier.shortValue());
        byteBuffer.put(blockType.byteValue());
        byteBuffer.putInt(address);
        byteBuffer.putShort((short)codeBlock.length);
        byteBuffer.put(codeBlock);
    }

    @Override
    public Integer size() {
        return FIXED_SIZE + codeBlock.length;
    }

    public static final WriteCodeBlockCommand writeCodeBlockCommandFromByteBuffer(ByteBuffer byteBuffer) {
        OtadMessageHeader messageHeader = OtadMessageHeader.messageHeaderFromByteBuffer(byteBuffer);
        byteBuffer.getShort(); // Don't need the length
        Integer identifier = Unsigned.getUnsignedShort(byteBuffer);
        byteBuffer.get(); // Don't need the block type.
        Integer address = (int)Unsigned.getUnsignedInt(byteBuffer);
        Integer codeBlockLen = Unsigned.getUnsignedShort(byteBuffer);
        byte[] bytes = new byte[codeBlockLen];
        byteBuffer.get(bytes);
        return new WriteCodeBlockCommand(messageHeader, identifier, address, bytes);
    }
}
