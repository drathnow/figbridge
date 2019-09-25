package zedi.pacbridge.app.events.connect;

import static org.junit.Assert.assertEquals;

import org.jdom2.Element;
import org.junit.Test;

import zedi.pacbridge.app.events.InvalidEventFormatException;
import zedi.pacbridge.gdn.messages.AddExtendedIoPointControl;
import zedi.pacbridge.gdn.messages.AddIoPointControl;
import zedi.pacbridge.gdn.messages.GdnIoPointClass;
import zedi.pacbridge.gdn.messages.GdnMessageType;


public class IoPointTest extends EventTestCase {

    public static final Integer F1 = 1;
    public static final Integer F2 = 2;
    public static final Integer F3 = 3;
    public static final Integer F4 = 4;
    public static final Integer RTU_ADDRESS = 1;
    public static final Float FACTOR = 1.2f;
    public static final Float OFFSET = 1.3f;
    public static final GdnIoPointClass IOPOINT_CLASS = GdnIoPointClass.System;

    @Test
    public void testAsAddIoPointMessageForStandardMessage() throws Exception {
        Element element = standardElementForTest();

        IoPoint ioPoint = IoPoint.ioPointForElement(element);
        AddIoPointControl control = (AddIoPointControl)ioPoint.asAddIoPointControl();

        assertEquals(GdnMessageType.AddIoPointMessage, control.messageType());
        assertEquals(DATA_TYPE, control.getDataType());
        assertEquals(INDEX, control.getIndex());
        assertEquals(F1, control.getF1());
        assertEquals(F2, control.getF2());
        assertEquals(F3, control.getF3());
        assertEquals(F4, control.getF4());
        assertEquals(RTU_ADDRESS, control.getRtuAddress());
    }

    @Test
    public void testAsAddIoPointMessageForExtendedMessage() throws Exception {
        Element element = extendedElementForTest();

        IoPoint ioPoint = IoPoint.ioPointForElement(element);
        AddExtendedIoPointControl control = (AddExtendedIoPointControl)ioPoint.asAddIoPointControl();

        assertEquals(GdnMessageType.AddExtendedIoPoint, control.messageType());
        assertEquals(DATA_TYPE, control.getDataType());
        assertEquals(INDEX, control.getIndex());
        assertEquals(F1, control.getF1());
        assertEquals(F2, control.getF2());
        assertEquals(F3, control.getF3());
        assertEquals(F4, control.getF4());
        assertEquals(RTU_ADDRESS, control.getRtuAddress());
        assertEquals(FACTOR.floatValue(), control.getFactor().floatValue(), 0.1f);
        assertEquals(OFFSET.floatValue(), control.getOffset().floatValue(), 0.1f);
        assertEquals(IOPOINT_CLASS, control.getIoPointClass());
    }

    @Test(expected = InvalidEventFormatException.class)
    public void testMissingDataType() throws Exception {
        Element element = extendedElementForTest();
        element.removeChild(IoPoint.TAG_DATATYPE);
        IoPoint.ioPointForElement(element);
    }

    @Test(expected = InvalidEventFormatException.class)
    public void testMissingIndex() throws Exception {
        Element element = extendedElementForTest();
        element.removeChild(IoPoint.TAG_INDEX);
        IoPoint.ioPointForElement(element);
    }

    @Test(expected = InvalidEventFormatException.class)
    public void testMissingF1() throws Exception {
        Element element = extendedElementForTest();
        element.removeChild(IoPoint.TAG_F1);
        IoPoint.ioPointForElement(element);
    }

    @Test(expected = InvalidEventFormatException.class)
    public void testMissingF2() throws Exception {
        Element element = extendedElementForTest();
        element.removeChild(IoPoint.TAG_F2);
        IoPoint.ioPointForElement(element);
    }

    @Test(expected = InvalidEventFormatException.class)
    public void testMissingF3() throws Exception {
        Element element = extendedElementForTest();
        element.removeChild(IoPoint.TAG_F3);
        IoPoint.ioPointForElement(element);
    }

    @Test(expected = InvalidEventFormatException.class)
    public void testMissingF4() throws Exception {
        Element element = extendedElementForTest();
        element.removeChild(IoPoint.TAG_F4);
        IoPoint.ioPointForElement(element);
    }

    @Test(expected = InvalidEventFormatException.class)
    public void testMissingRTUAddress() throws Exception {
        Element element = extendedElementForTest();
        element.removeChild(IoPoint.TAG_RTU_ADDRESS);
        IoPoint.ioPointForElement(element);
    }

    @Test(expected = InvalidEventFormatException.class)
    public void testMissingIoPointClass() throws Exception {
        Element element = extendedElementForTest();
        element.removeChild(IoPoint.TAG_IOPOINT_CLASS);
        IoPoint.ioPointForElement(element);
    }

    private Element standardElementForTest() {
        Element element = new Element(IoPoint.ROOT_ELEMENT_NAME);
        element.addContent(new Element(IoPoint.TAG_DATATYPE).setText(DATA_TYPE.getName()));
        element.addContent(new Element(IoPoint.TAG_INDEX).setText("" + INDEX));
        element.addContent(new Element(IoPoint.TAG_F1).setText("" + F1));
        element.addContent(new Element(IoPoint.TAG_F2).setText("" + F2));
        element.addContent(new Element(IoPoint.TAG_F3).setText("" + F3));
        element.addContent(new Element(IoPoint.TAG_F4).setText("" + F4));
        element.addContent(new Element(IoPoint.TAG_RTU_ADDRESS).setText("" + RTU_ADDRESS));
        element.addContent(new Element(IoPoint.TAG_POLLSET_NUMBER).setText("" + POLLSET_NUMBER));
        element.addContent(new Element(IoPoint.TAG_IOPOINT_CLASS).setText(IOPOINT_CLASS.getName()));
        return element;
    }


    private Element extendedElementForTest() {
        Element element = new Element(IoPoint.ROOT_ELEMENT_NAME);
        element.addContent(new Element(IoPoint.TAG_DATATYPE).setText(DATA_TYPE.getName()));
        element.addContent(new Element(IoPoint.TAG_INDEX).setText("" + INDEX));
        element.addContent(new Element(IoPoint.TAG_F1).setText("" + F1));
        element.addContent(new Element(IoPoint.TAG_F2).setText("" + F2));
        element.addContent(new Element(IoPoint.TAG_F3).setText("" + F3));
        element.addContent(new Element(IoPoint.TAG_F4).setText("" + F4));
        element.addContent(new Element(IoPoint.TAG_RTU_ADDRESS).setText("" + RTU_ADDRESS));
        element.addContent(new Element(IoPoint.TAG_POLLSET_NUMBER).setText("" + POLLSET_NUMBER));
        element.addContent(new Element(IoPoint.TAG_FACTOR).setText(FACTOR.toString()));
        element.addContent(new Element(IoPoint.TAG_OFFSET).setText(OFFSET.toString()));
        element.addContent(new Element(IoPoint.TAG_IOPOINT_CLASS).setText(IOPOINT_CLASS.getName()));
        return element;
    }
}