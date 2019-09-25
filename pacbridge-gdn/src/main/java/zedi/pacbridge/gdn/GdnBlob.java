package zedi.pacbridge.gdn;

import java.nio.ByteBuffer;

import org.apache.commons.codec.binary.Base64;

import zedi.pacbridge.utl.HexStringDecoder;
import zedi.pacbridge.utl.io.Unsigned;


public class GdnBlob extends GdnValue<byte[]> {
    public static final String TYPE_NAME = "Binary";
    public static final String SHORT_TYPE_NAME = "BLOB";
    public static final int TYPE_SIZE = -1;

    GdnBlob() {
        super(GdnDataType.Binary);
    }

    public GdnBlob(byte[] bytes) {
        this();
        setValue(bytes);
    }
    
    public GdnBlob(String stringValue) {
        this(HexStringDecoder.hexStringAsBytes(stringValue));
    }
    
    public int typeNumber() {
        return GdnDataType.NUMBER_FOR_TYPE_BLOB;
    }

    @Override
    public boolean isNumeric() {
        return false;
    }
    
    @Override
    public Integer serializedSize() {
        if (getValue() != null)
            return ((byte[]) getValue()).length;
        return -1;
    }

    public String toString() {
        if (getValue() != null)
            return new String(Base64.encodeBase64(getValue()));
        return "";
    }
    
    /**
     * Blobs are serialized as a counted byte stream, which means the length of the blob is encoded
     * as a two byte integer at the beginning of the stream. When deserializing, we expect the first 
     * two bytes to be these count bytes.
     */
    @Override
    public void deserialize(ByteBuffer byteBuffer) {
        int length = Unsigned.getUnsignedShort(byteBuffer);
        if (length > 0) {
            setValue(new byte[length]);
            byteBuffer.get(getValue());
        }
    }
    
    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(getValue() != null ? ((byte[]) getValue()).length : 0));
        if (getValue() != null)
            byteBuffer.put(getValue());
    }

    public static GdnValue<?> blobFromByteBuffer(ByteBuffer byteBuffer) {
        byte[] bytes = new byte[Unsigned.getUnsignedShort(byteBuffer)];
        return new GdnBlob(bytes);
    }
}