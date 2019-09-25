package zedi.pacbridge.gdn.otad;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;


/**
 * A CodeBlock is a block of data that contains a portion of a download image.  Each block is
 * 1024 bytes in length but may not be completely full.  The reason for this is because the
 * way the image is laid out in memory may not result in eactly full blocks of data, due to address
 * boundaries.  If a code block spans two address boundaries, and the second one does not
 * contain a full block of data, it must be padded with 0xFF
 * 
 */
public class CodeBlock implements Serializable {
    static final long serialVersionUID = 1001;
    
    public static final int MAX_CODEBLOCK_SIZE = 1024;
    public static final int CODEMAP_SIZE = 0x2000;
    
    private CodeBlockType codeBlockType;
    protected int startAddressOfData;
    protected ByteArrayOutputStream byteArrayOutputStream;

    public CodeBlock(int startAddressOfData) {
        this.startAddressOfData = startAddressOfData;
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.codeBlockType = CodeBlockType.codeBlockTypeForStartingAddress(startAddressOfData);
    }

    public CodeBlockType getCodeBlockType() {
        return CodeBlockType.codeBlockTypeForStartingAddress(startAddressOfData);
    }
    
    public boolean isFlashBlock() {
        return codeBlockType == CodeBlockType.Flash;
    }

    public int[] codeMaps() {
        if (byteArrayOutputStream.size() == 0)
            return new int[] { 0 };
        int normalizedFirstAddress = normalizedAddress(startAddressOfData());
        int normalizedLastAddress = normalizedAddress(lastAddressOfData());
        if (normalizedLastAddress >= CODEMAP_SIZE)
            return new int[] { 0x80, 0x01 };
        return new int[] {
                   (int)Math.pow(2, (normalizedFirstAddress / MAX_CODEBLOCK_SIZE)) | (int)Math.pow(2, (normalizedLastAddress / MAX_CODEBLOCK_SIZE))
               };
    }

    protected int baseAddress() {
        return codeBlockType.getBaseAddress();
    }

    protected int normalizedAddress(int anAddress) {
        int normalizedAddress = anAddress - baseAddress();
        normalizedAddress -= (CODEMAP_SIZE * startingByteOfCodeMap());
        return normalizedAddress;
    }

    public int startingByteOfCodeMap() {
        return (startAddressOfData() - baseAddress()) / CODEMAP_SIZE;
    }

    public int startAddressOfData() {
        return startAddressOfData;
    }

    public int getLength() {
        return byteArrayOutputStream.size();
    }

    public byte[] codeData() {
        return byteArrayOutputStream.toByteArray();
    }

    public int lastAddressOfData() {
        return (startAddressOfData + byteArrayOutputStream.size()) - 1;
    }

    public int lastAddressOfBlock() {
        return startAddressOfData() | 0x0003ff;
    }

    public int addCodeData(int address, byte[] someCodeData) {
        return addCodeData(address, someCodeData, 0, someCodeData.length);
    }

    public int addCodeData(byte[] someCodeData) throws IOException {
        return addCodeData(someCodeData, 0, someCodeData.length);
    }

    public int addCodeData(byte[] someCodeData, int offset, int length) throws IOException {
        return addCodeData(lastAddressOfData() + 1, someCodeData, offset, length);
    }

    public int addCodeData(int address, byte[] someCodeData, int offset, int length) {
        fillByteArrayWithFFToAddress(address);
        int readLength = (length < availableSpace()) ? length : availableSpace();
        byteArrayOutputStream.write(someCodeData, offset, readLength);
        return readLength;
    }

    protected void fillByteArrayWithFFToAddress(int address) {
        int amountTofill = address - lastAddressOfData() - 1;
        for (int i = 0; i < amountTofill; i++)
            byteArrayOutputStream.write(0xff);
    }

    protected int availableSpace() {
        return lastAddressOfBlock() - lastAddressOfData();
    }
}
