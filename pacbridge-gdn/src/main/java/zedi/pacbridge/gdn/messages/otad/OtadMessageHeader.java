package zedi.pacbridge.gdn.messages.otad;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.utl.io.Unsigned;

/**
 * An OTAD message header is a bit field where sets of indicate specific bits of information
 * @author drathnow.  Bit 7 indicates if the message is a command or response message; bits
 * 4 to 6 indicate the error code; and bits 0 to 3 indicate the message type.
 *<pre>
 *  7     4       0
 * +-+-+-+-+-+-+-+-+
 * | | | | | | | | |
 * +-+-+-+-+-+-+-+-+
 *  ^ <---> <------>
 *  |   ^      ^
 *  |   |      |-MessageType (4 bits)
 *  |   |- ErrorCode (3 bits)
 *  |-Command or Response (1 bit)
 * </pre>
 */
class OtadMessageHeader implements Serializable {
    private static final long serialVersionUID = 1001;

    protected static final int RESPONSE_BITMASK = 0x80;
    protected static final int ERRORCODE_BITMASK = 0x70;
    protected static final int TYPE_BITMASK = 0x0f;
    protected OtadMessageType messageType;
    protected ErrorCode errorCode;
    protected boolean command;

    public OtadMessageHeader(boolean isCommand, OtadMessageType messageType) {
        this(true, ErrorCode.NoError, messageType);
    }
    
    public OtadMessageHeader(boolean isCommand, ErrorCode errorCode, OtadMessageType messageType) {
        this.messageType = messageType;
        this.errorCode = errorCode;
        this.command = isCommand;
    }

    public boolean isCommand() {
        return command;
    }

    public boolean isResponse() {
        return !command;
    }

    public boolean isAck() {
        return errorCode == ErrorCode.NoError;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public OtadMessageType getMessageType() {
        return messageType;
    }

    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put((byte)messageTypeForSerialization());
    }

    private int messageTypeForSerialization() {
        int type = messageType.getTypeNumber();
        type = command ? type : (type | RESPONSE_BITMASK);
        type = (errorCode.getNumber() << 4) | type;
        return type;
    }
    
    public static final OtadMessageHeader messageHeaderFromByteBuffer(ByteBuffer byteBuffer) {
        int theByte = Unsigned.getUnsignedByte(byteBuffer);
        ErrorCode errorCode = ErrorCode.errorCodeForNumber((theByte & ERRORCODE_BITMASK) >> 4);
        boolean command = ((theByte & RESPONSE_BITMASK) > 0) ? false : true;
        OtadMessageType messageType = OtadMessageType.messageTypeForNumber(theByte & TYPE_BITMASK);
        return new OtadMessageHeader(command, errorCode, messageType);
    }
}
