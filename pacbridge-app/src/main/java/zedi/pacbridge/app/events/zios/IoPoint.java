package zedi.pacbridge.app.events.zios;

import org.jdom2.Element;

import zedi.pacbridge.app.events.EventElement;
import zedi.pacbridge.app.events.InvalidEventFormatException;
import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.gdn.messages.AddExtendedIoPointControl;
import zedi.pacbridge.gdn.messages.AddIoPointControl;
import zedi.pacbridge.gdn.messages.AddStandardIoPointControl;
import zedi.pacbridge.gdn.messages.GdnIoPointClass;


public class IoPoint extends EventElement {

    public static final String ROOT_ELEMENT_NAME = "IoPoint";
    public static final String TAG_DATATYPE = "DataType";
    public static final String TAG_INDEX = "Index";
    public static final String TAG_F1 = "F1";
    public static final String TAG_F2 = "F2";
    public static final String TAG_F3 = "F3";
    public static final String TAG_F4 = "F4";
    public static final String TAG_RTU_ADDRESS = "RtuAddress";
    public static final String TAG_POLLSET_NUMBER = "PollsetNumber";
    public static final String TAG_FACTOR = "Factor";
    public static final String TAG_OFFSET = "Offset";
    public static final String TAG_IOPOINT_CLASS = "IoPointClass";

    private GdnDataType dataType;
    private Integer index;
    private Integer f1;
    private Integer f2;
    private Integer f3;
    private Integer f4;
    private Integer rtuAddress;
    private Integer pollsetNumber;
    private Float offset;
    private Float factor;
    private GdnIoPointClass ioPointClass;

    public IoPoint(GdnDataType dataType, Integer index, Integer f1, Integer f2, Integer f3, Integer f4, Integer rtuAddress, Integer pollsetNumber, Float offset, Float factor, GdnIoPointClass ioPointClass) {
        this.dataType = dataType;
        this.index = index;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.f4 = f4;
        this.rtuAddress = rtuAddress;
        this.pollsetNumber = pollsetNumber;
        this.offset = offset;
        this.factor = factor;
        this.ioPointClass = ioPointClass;
    }

    public IoPoint(GdnDataType dataType, Integer index, Integer f1, Integer f2, Integer f3, Integer f4, Integer rtuAddress, Integer pollsetNumber, GdnIoPointClass ioPointClass) {
        this.dataType = dataType;
        this.index = index;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.f4 = f4;
        this.rtuAddress = rtuAddress;
        this.pollsetNumber = pollsetNumber;
        this.ioPointClass = ioPointClass;
    }

    public boolean isExtended() {
        return factor != null && offset != null;
    }

    public AddIoPointControl asAddIoPointControl() {
        return isExtended() ? new AddExtendedIoPointControl(dataType, index, pollsetNumber, rtuAddress, f1, f2, f3, f4, factor, offset, ioPointClass)
                    : new AddStandardIoPointControl(dataType, index, pollsetNumber, rtuAddress, f1, f2, f3, f4, ioPointClass);
    }

    public Element asElement() {
        Element element =  new Element(ROOT_ELEMENT_NAME)
                            .addContent(new Element(TAG_DATATYPE).setText(dataType.getName()))
                            .addContent(new Element(TAG_INDEX).setText(index.toString()))
                            .addContent(new Element(TAG_POLLSET_NUMBER).setText(pollsetNumber.toString()))
                            .addContent(new Element(TAG_F1).setText(f1.toString()))
                            .addContent(new Element(TAG_F2).setText(f2.toString()))
                            .addContent(new Element(TAG_F3).setText(f3.toString()))
                            .addContent(new Element(TAG_F4).setText(f4.toString()))
                            .addContent(new Element(TAG_RTU_ADDRESS).setText(rtuAddress.toString()))
                            .addContent(new Element(TAG_IOPOINT_CLASS).setText(ioPointClass.getName()));
        if (isExtended()) {
            element.addContent(new Element(TAG_OFFSET).setText(offset.toString()))
                    .addContent(new Element(TAG_FACTOR).setText(factor.toString()));
        }
        return element;
    }

    public static IoPoint ioPointForElement(Element element) throws InvalidEventFormatException {
        String dataTypeString = requiredValueFromElement(TAG_DATATYPE, element);
        GdnDataType dataType = GdnDataType.dataTypeForName(dataTypeString);
        if (dataType == null)
            throw new InvalidEventFormatException("Unknown data type name in IOPoint element: " + dataTypeString);
        Integer index = Integer.valueOf(requiredValueFromElement(TAG_INDEX, element));
        Integer f1 = Integer.valueOf(requiredValueFromElement(TAG_F1, element));
        Integer f2 = Integer.valueOf(requiredValueFromElement(TAG_F2, element));
        Integer f3 = Integer.valueOf(requiredValueFromElement(TAG_F3, element));
        Integer f4 = Integer.valueOf(requiredValueFromElement(TAG_F4, element));
        Integer rtuAddress = Integer.valueOf(requiredValueFromElement(TAG_RTU_ADDRESS, element));
        Integer pollsetNumber = Integer.valueOf(requiredValueFromElement(TAG_POLLSET_NUMBER, element));
        String ioPointClassString = requiredValueFromElement(TAG_IOPOINT_CLASS, element);
        GdnIoPointClass ioPointClass = GdnIoPointClass.ioPointClassForName(ioPointClassString);
        if (ioPointClass == null)
            throw new InvalidEventFormatException("Unknown IO point class name in IOPoint element: " + ioPointClassString);

        String text;
        Float offset = null;
        Float factor = null;
        if ((text = element.getChildText(TAG_OFFSET)) != null)
            offset = Float.valueOf(text);
        if ((text = element.getChildText(TAG_FACTOR)) != null)
            factor= Float.valueOf(text);
        return new IoPoint(dataType, index, f1, f2, f3, f4, rtuAddress, pollsetNumber, offset, factor, ioPointClass);
    }
}
