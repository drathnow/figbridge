package zedi.pacbridge.gdn.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.io.Unsigned;


public class AddExtendedIoPointControl extends AddIoPointControl implements GdnMessage, Control, Serializable {
    static final long serialVersionUID = 1001;

    private static final int SIZE = 24;

    private Integer version = 0;
    private Float factor;
    private Float offset;

    private AddExtendedIoPointControl() {
        super(GdnMessageType.AddExtendedIoPoint);
    }

    public AddExtendedIoPointControl(GdnDataType dataType,
                                     Integer index,
                                     Integer pollsetNumber,
                                     Integer rtuAddress,
                                     Integer f1,
                                     Integer f2,
                                     Integer f3,
                                     Integer f4,
                                     Float factor,
                                     Float offset,
                                     GdnIoPointClass ioPointClass) {
        super(GdnMessageType.AddExtendedIoPoint, dataType, index, pollsetNumber, rtuAddress, f1, f2, f3, f4);
        this.factor = factor;
        this.offset = offset;
        setIoPointClass(ioPointClass);
    }

    public GdnIoPointClass getIoPointClass() {
        this.messageType();
        return ioPointClass;
    }

    @Override
    public Integer size() {
        return SIZE;
    }

    public Float getFactor() {
        return factor;
    }

    public Float getOffset() {
        return offset;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(version.byteValue());
        byteBuffer.putShort(index.shortValue());
        byteBuffer.put(dataType.getNumber().byteValue());
        byteBuffer.put((byte)pollsetNumber.intValue());
        byteBuffer.put(ioPointClass.getNumber().byteValue());
        byteBuffer.putShort((short)rtuAddress.intValue());
        byteBuffer.putShort((short)f1.intValue());
        byteBuffer.putShort((short)f2.intValue());
        byteBuffer.putShort((short)f3.intValue());
        byteBuffer.putShort((short)f4.intValue());
        byteBuffer.putFloat(factor.floatValue());
        byteBuffer.putFloat(offset.floatValue());
    }

    private void deserialize(ByteBuffer byteBuffer) {
        byteBuffer.get(); // Version is not used
        index = Unsigned.getUnsignedShort(byteBuffer);
        dataType = GdnDataType.dataTypeForTypeNumber((int)Unsigned.getUnsignedByte(byteBuffer));
        pollsetNumber = new Integer(Unsigned.getUnsignedByte(byteBuffer));

        int classNumber = Unsigned.getUnsignedByte(byteBuffer);
        GdnIoPointClass ioPointClass = GdnIoPointClass.ioPointClassForClassNumber(classNumber);
        if (ioPointClass == null)
            throw new IllegalArgumentException("Unable to parse Add Extended IO Point message. Invalid IO Point class: " + classNumber);
        setIoPointClass(ioPointClass);
        rtuAddress = Unsigned.getUnsignedShort(byteBuffer);
        f1 = Unsigned.getUnsignedShort(byteBuffer);
        f2 = Unsigned.getUnsignedShort(byteBuffer);
        f3 = Unsigned.getUnsignedShort(byteBuffer);
        f4 = Unsigned.getUnsignedShort(byteBuffer);
        factor = byteBuffer.getFloat();
        offset = byteBuffer.getFloat();
    }

    public static AddExtendedIoPointControl addExtendedIoPointControlFromByteBuffer(ByteBuffer byteBuffer) {
        AddExtendedIoPointControl control = new AddExtendedIoPointControl();
        control.deserialize(byteBuffer);
        return control;
    }
}
