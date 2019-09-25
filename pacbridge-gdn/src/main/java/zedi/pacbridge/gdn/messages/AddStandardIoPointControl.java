package zedi.pacbridge.gdn.messages;

import java.nio.ByteBuffer;

import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.io.Unsigned;


public class AddStandardIoPointControl extends AddIoPointControl implements GdnMessage, Control {
    static final long serialVersionUID = 1001;
    private static final int SIZE = 14;

    private AddStandardIoPointControl() {
        super(GdnMessageType.AddIoPointMessage);
    }

    public AddStandardIoPointControl(GdnDataType dataType,
                                     Integer index,
                                     Integer pollsetNumber,
                                     Integer rtuAddress,
                                     Integer f1,
                                     Integer f2,
                                     Integer f3,
                                     Integer f4,
                                     GdnIoPointClass ioPointClass) {
        super(GdnMessageType.AddIoPointMessage, dataType, index, pollsetNumber, rtuAddress, f1, f2, f3, f4);
        setIoPointClass(ioPointClass);
    }

    @Override
    public GdnMessageType messageType() {
        return GdnMessageType.AddIoPointMessage;
    }

    @Override
    public Integer size() {
        return SIZE;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put((byte)getPollSetNumber().intValue());
        byteBuffer.putShort(getIndex().shortValue());
        byteBuffer.putShort((short)getRtuAddress().intValue());
        byteBuffer.putShort((short)getF1().intValue());
        byteBuffer.putShort((short)getF2().intValue());
        byteBuffer.putShort((short)getF3().intValue());
        byteBuffer.putShort((short)getF4().intValue());
        byteBuffer.put((byte)typeNumberForSerialization());
    }

    private void deserialize(ByteBuffer byteBuffer) {
        pollsetNumber = new Integer(Unsigned.getUnsignedByte(byteBuffer));
        index = Unsigned.getUnsignedShort(byteBuffer);
        rtuAddress = Unsigned.getUnsignedShort(byteBuffer);
        f1 = Unsigned.getUnsignedShort(byteBuffer);
        f2 = Unsigned.getUnsignedShort(byteBuffer);
        f3 = Unsigned.getUnsignedShort(byteBuffer);
        f4 = Unsigned.getUnsignedShort(byteBuffer);
        deserializeTypeNumber(Unsigned.getUnsignedByte(byteBuffer));
    }

    public static AddStandardIoPointControl addStandardIoPointControlFromByteBuffer(ByteBuffer byteBuffer) {
        AddStandardIoPointControl control = new AddStandardIoPointControl();
        control.deserialize(byteBuffer);
        return control;
    }

}