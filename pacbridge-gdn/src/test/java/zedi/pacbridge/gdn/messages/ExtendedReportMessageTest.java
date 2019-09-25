package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import zedi.pacbridge.gdn.GdnAlarmStatus;
import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.gdn.GdnFloat;
import zedi.pacbridge.utl.HexStringDecoder;
import zedi.pacbridge.utl.io.Unsigned;


public class ExtendedReportMessageTest extends PointMessageTest {
    protected static final int IOINDEX = 5;
    public static final String LE_EXTENDED_REPORT_STRING4 = "01 b5 01 b5 40 01 01 01 00 01 01 00";
    public static final String EXTENDED_REPORT_MESSAGE_201 = "01 94 63 b8 41 c9 21 00 01 08 00 00 00 00 00 01 01 08 00 00 00 00 00 02 01 08 00 00 00 00 00 03 01 08 00 00 00 00 00 04 01 08 00 00 00 00 00 05 01 08 00 00 00 00 00 06 01 08 00 00 00 00 00 07 01 08 00 00 00 00 00 08 01 08 00 00 00 00 00 09 01 08 00 00 00 00 00 0a 01 08 00 00 00 00 00 0b 01 08 00 00 00 00 00 0c 01 08 00 00 00 00 00 0d 01 08 00 00 00 00 00 0e 01 08 00 00 00 00 00 0f 01 08 00 00 00 00 00 10 01 08 00 00 00 00 00 11 01 08 00 00 00 00 00 12 01 08 00 00 00 00 00 13 01 08 00 00 00 00 00 14 01 08 00 00 00 00 00 15 01 08 00 00 00 00 00 16 01 08 00 00 00 00 00 17 01 08 00 00 00 00 00 18 01 08 00 00 00 00 00 19 01 08 00 00 00 00 00 1a 01 08 00 00 00 00 00 1b 01 08 00 00 00 00 00 1c 01 08 00 00 00 00 00 1d 01 08 00 00 00 00 00 1e 01 08 00 00 00 00 00 1f 01 08 00 00 00 00 00 20 01 08 00 00 00 00 00 21 01 08 00 00 00 00 00 22 01 08 00 00 00 00 00 23 01 08 00 00 00 00 00 24 01 08 00 00 00 00 00 25 01 08 00 00 00 00 00 26 01 08 00 00 00 00 00 27 01 08 00 00 00 00 00 28 01 08 00 00 00 00 00 29 01 08 00 00 00 00 00 2a 01 08 00 00 00 00 00 2b 01 08 00 00 00 00 00 2c 01 08 00 00 00 00 00 2d 01 08 00 00 00 00 00 2e 01 08 00 00 00 00 00 2f 01 08 00 00 00 00 00 30 01 08 00 00 00 00 00 31 01 08 00 00 00 00 00 32 01 08 00 00 00 00 00 33 01 08 00 00 00 00 00 34 01 08 00 00 00 00 00 35 01 08 00 00 00 00 00 36 01 08 00 00 00 00 00 37 01 08 00 00 00 00 00 38 01 08 00 00 00 00 00 39 01 08 00 00 00 00 00 3a 01 08 00 00 00 00 00 3b 01 08 00 00 00 00 00 3c 01 08 00 00 00 00 00 3d 01 08 00 00 00 00 00 3e 01 08 00 00 00 00 00 3f 01 08 00 00 00 00 00 40 01 08 00 00 00 00 00 41 01 08 00 00 00 00 00 42 01 08 00 00 00 00 00 43 01 08 00 00 00 00 00 44 01 08 00 00 00 00 00 45 01 08 00 00 00 00 00 46 01 08 00 00 00 00 00 47 01 08 00 00 00 00 00 48 01 08 00 00 00 00 00 49 01 08 00 00 00 00 00 4a 01 08 00 00 00 00 00 4b 01 08 00 00 00 00 00 4c 01 08 00 00 00 00 00 4d 01 08 00 00 00 00 00 4e 01 08 00 00 00 00 00 4f 01 08 00 00 00 00 00 50 01 08 00 00 00 00 00 51 01 08 00 00 00 00 00 52 01 08 00 00 00 00 00 53 01 08 00 00 00 00 00 54 01 08 00 00 00 00 00 55 01 08 00 00 00 00 00 56 01 08 00 00 00 00 00 57 01 08 00 00 00 00 00 58 01 08 00 00 00 00 00 59 01 08 00 00 00 00 00 5a 01 08 00 00 00 00 00 5b 01 08 00 00 00 00 00 5c 01 08 00 00 00 00 00 5d 01 08 00 00 00 00 00 5e 01 08 00 00 00 00 00 5f 01 08 00 00 00 00 00 60 01 08 00 00 00 00 00 61 01 08 00 00 00 00 00 62 01 08 00 00 00 00 00 63 01 08 00 00 00 00 00 64 01 08 00 00 00 00 00 65 01 08 00 00 00 00 00 66 01 08 00 00 00 00 00 67 01 08 00 00 00 00 00 68 01 08 00 00 00 00 00 69 01 08 00 00 00 00 00 6a 01 08 00 00 00 00 00 6b 01 08 00 00 00 00 00 6c 01 08 00 00 00 00 00 6d 01 08 00 00 00 00 00 6e 01 08 00 00 00 00 00 6f 01 08 00 00 00 00 00 70 01 08 00 00 00 00 00 71 01 08 00 00 00 00 00 72 01 08 00 00 00 00 00 73 01 08 00 00 00 00 00 74 01 08 00 00 00 00 00 75 01 08 00 00 00 00 00 76 01 08 00 00 00 00 00 77 01 08 00 00 00 00 00 78 01 08 00 00 00 00 00 79 01 08 00 00 00 00 00 7a 01 08 00 00 00 00 00 7b 01 08 00 00 00 00 00 7c 01 08 00 00 00 00 00 7d 01 08 00 00 00 00 00 7e 01 08 00 00 00 00 00 7f 01 08 00 00 00 00 00 80 01 08 00 00 00 00 00 81 01 08 00 00 00 00 00 82 01 08 00 00 00 00 00 83 01 08 00 00 00 00 00 84 01 08 00 00 00 00 00 85 01 08 00 00 00 00 00 86 01 08 00 00 00 00 00 87 01 08 00 00 00 00 00 88 01 08 00 00 00 00 00 89 01 08 00 00 00 00 00 8a 01 08 00 00 00 00 00 8b 01 08 00 00 00 00 00 8c 01 08 00 00 00 00 00 8d 01 08 00 00 00 00 00 8e 01 08 00 00 00 00 00 8f 01 08 00 00 00 00 00 90 01 08 00 00 00 00 00 91 01 08 00 00 00 00 00 92 01 08 00 00 00 00 00 93 01 08 00 00 00 00 00 94 01 08 00 00 00 00 00 95 01 08 00 00 00 00 00 96 01 08 00 00 00 00 00 97 01 08 00 00 00 00 00 98 01 08 00 00 00 00 00 99 01 08 00 00 00 00 00 9a 01 08 00 00 00 00 00 9b 01 08 00 00 00 00 00 9c 01 08 00 00 00 00 00 9d 01 08 00 00 00 00 00 9e 01 08 00 00 00 00 00 9f 01 08 00 00 00 00 00 a0 01 08 00 00 00 00 00 a1 01 08 00 00 00 00 00 a2 01 08 00 00 00 00 00 a3 01 08 00 00 00 00 00 a4 01 08 00 00 00 00 00 a5 01 08 00 00 00 00 00 a6 01 08 00 00 00 00 00 a7 01 08 00 00 00 00 00 a8 01 08 00 00 00 00 00 a9 01 08 00 00 00 00 00 aa 01 08 00 00 00 00 00 ab 01 08 00 00 00 00 00 ac 01 08 00 00 00 00 00 ad 01 08 00 00 00 00 00 ae 01 08 00 00 00 00 00 af 01 08 00 00 00 00 00 b0 01 08 00 00 00 00 00 b1 01 08 00 00 00 00 00 b2 01 08 00 00 00 00 00 b3 01 08 00 00 00 00 00 b4 01 08 00 00 00 00 00 b5 01 08 00 00 00 00 00 b6 01 08 00 00 00 00 00 b7 01 08 00 00 00 00 00 b8 01 08 00 00 00 00 00 b9 01 08 00 00 00 00 00 ba 01 08 00 00 00 00 00 bb 01 08 00 00 00 00 00 bc 01 08 00 00 00 00 00 bd 01 08 00 00 00 00 00 be 01 08 00 00 00 00 00 bf 01 08 00 00 00 00 00 c0 01 08 00 00 00 00 00 c1 01 08 00 00 00 00 00 c2 01 08 00 00 00 00 00 c3 01 08 00 00 00 00 00 c4 01 08 00 00 00 00 00 c5 01 08 00 00 00 00 00 c6 01 08 00 00 00 00 00 c7 01 08 00 00 00 00 00 c8 01 08 00 00 00 00 00";
    
    @Test
    public void testReadingLargeReportCount() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.wrap(HexStringDecoder.hexStringAsBytes(EXTENDED_REPORT_MESSAGE_201)).order(ByteOrder.LITTLE_ENDIAN);
        ExtendedReportMessage extendedPointReport = ExtendedReportMessage.extendedReportMessageFromByteBuffer(byteBuffer);
        assertEquals(201, extendedPointReport.getReportItems().size());
    }
    
    @Test
    public void testSerializeInput2() throws IOException {
        byte bytes[] = new byte[]{0x01, (byte)0xf6, (byte)0xcb, (byte)0xe1, 0x40, 0x01, 0x20, 0x5e, 0x02, 0x08, 0x00, 0x00, 0x00, (byte)0x84, 0x42};
        ExtendedReportMessage.extendedReportMessageFromByteBuffer(ByteBuffer.wrap(bytes));
    }

    @Test
    public void testSerializeInput() throws IOException {
        byte bytes[] = new byte[]{0x01, 0x41, (byte)0xc1, (byte)0xe1, 0x40, 0x01, 0x01, 0x5e, 0x02, 0x08, 0x00, 0x00, 0x00, (byte)0x84, 0x42};
        ExtendedReportMessage.extendedReportMessageFromByteBuffer(ByteBuffer.wrap(bytes));
    }

    @Test
    public void testSerializeLeString4() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(HexStringDecoder.hexStringAsBytes(LE_EXTENDED_REPORT_STRING4)).order(ByteOrder.LITTLE_ENDIAN);
        ExtendedReportMessage.extendedReportMessageFromByteBuffer(byteBuffer);
    }

    @Test
    public void testMessageNumber() {
        ByteBuffer byteBuffer = ByteBuffer.wrap(HexStringDecoder.hexStringAsBytes(LE_EXTENDED_REPORT_STRING4)).order(ByteOrder.LITTLE_ENDIAN);
        ExtendedReportMessage extendedPointReport = ExtendedReportMessage.extendedReportMessageFromByteBuffer(byteBuffer);
        assertEquals(GdnMessageType.ExtendedReport, extendedPointReport.messageType());
    }

    @Test
    public void shouldDeserializeFromByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        Date date = new Date();
        byteBuffer.put((byte)POLLSETID.byteValue());
        byteBuffer.putInt((int)(date.getTime() / 1000));
        byteBuffer.put((byte)1);
        ReportAttributes attributes = new ReportAttributes(GdnReasonCode.AlarmTrigger, ValueType.Extended);
        attributes.serialize(byteBuffer);
        byteBuffer.putShort((short)INDEX.shortValue());
        byteBuffer.put(GdnDataType.Float.getNumber().byteValue());
        byteBuffer.put(GdnAlarmStatus.Low.getTypeNumber().byteValue());
        byteBuffer.putFloat(TEST_FLOAT_VALUE);
        byteBuffer.flip();
        
        ExtendedReportMessage extendedPointReport = ExtendedReportMessage.extendedReportMessageFromByteBuffer(byteBuffer);
        
        assertEquals(POLLSETID.intValue(), extendedPointReport.getPollSetNumber());
        assertEquals(date.getTime() / 1000, extendedPointReport.getTimeStamp().getTime() / 1000);
        assertEquals(GdnReasonCode.AlarmTrigger, extendedPointReport.getReasonCode());
        assertEquals(ValueType.Extended, extendedPointReport.getValueType());
        assertEquals(1, extendedPointReport.getReportItems().size());
        assertTrue(extendedPointReport.getReportItems().get(0) instanceof ExtendedReportItem);
        ExtendedReportItem extendedPointReportItem = (ExtendedReportItem)extendedPointReport.getReportItems().get(0);
        assertEquals(INDEX.intValue(), extendedPointReportItem.getIndex());
        assertEquals(GdnDataType.Float, extendedPointReportItem.getValue().dataType());
        assertEquals(TEST_FLOAT_VALUE, ((Float)extendedPointReportItem.getValue().getValue()).floatValue(), 0.01);
        assertEquals(GdnAlarmStatus.Low, extendedPointReportItem.getAlarmStatus());
    }
    
    @Test
    public void shouldSerializeToByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        Date date = new Date();

        ExtendedReportItem pointReportItem = new ExtendedReportItem(IOINDEX, new GdnFloat(TEST_FLOAT_VALUE), GdnAlarmStatus.HighHigh);
        pointReportItem.setIndex(IOINDEX);
        pointReportItem.setValue(new GdnFloat(TEST_FLOAT_VALUE));
        pointReportItem.setAlarmStatus(GdnAlarmStatus.HighHigh);
        
        List<ExtendedReportItem> reportItems = Arrays.asList(pointReportItem);
        
        ExtendedReportMessage extendedPointReport = new ExtendedReportMessage(reportItems, GdnReasonCode.AlarmTrigger, POLLSETID, date);

        extendedPointReport.serialize(byteBuffer);
        
        byteBuffer.flip();
        assertEquals(POLLSETID.intValue(), Unsigned.getUnsignedByte(byteBuffer));
        assertEquals((int)(date.getTime() / 1000), new Date(Unsigned.getUnsignedInt(byteBuffer)).getTime());
        assertEquals(extendedPointReport.getReportItems().size(), Unsigned.getUnsignedByte(byteBuffer));
        ReportAttributes reportAttributes = new ReportAttributes(byteBuffer.get());
        assertEquals(GdnReasonCode.AlarmTrigger, reportAttributes.getReasonCode());
        assertEquals(ValueType.Extended, reportAttributes.getValueType());
        assertEquals(IOINDEX, Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(GdnDataType.NUMBER_FOR_TYPE_FLOAT, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(GdnAlarmStatus.HighHigh.getTypeNumber().byteValue(), Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(TEST_FLOAT_VALUE, byteBuffer.getFloat(), 0.001);
        
    }
}
