package zedi.pacbridge.gdn.messages;

import java.io.Serializable;

public abstract class ByteMessage extends GdnMessageBase implements Serializable {
    static final long serialVersionUID = 1001;

    protected byte[] byteData;

    protected ByteMessage(GdnMessageType messageType, byte[] bytes) {
        super(messageType);
        if (bytes == null)
            throw new NullPointerException();
        byteData = new byte[bytes.length];
        System.arraycopy(bytes, 0, byteData, 0, bytes.length);
    }

    public byte[] asByteArray() {
        return byteData;
    }
}