package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.test.BaseTestCase;


public class StandardReportItemTest extends BaseTestCase {

    protected static final Byte TEST_DISCRETE = new Byte((byte)0);
    protected static final Byte TEST_UCHAR = new Byte((byte)256);
    protected static final Integer TEST_CHAR = new Integer(-1);
    protected static final Float TEST_FLOAT = new Float(123.4);
    protected static final Integer TEST_INTEGER = new Integer(345);
    protected static final Short TEST_USHORT = new Short((short)890);
    protected static final Short TEST_SHORT = new Short((short)-1);
    protected static final Integer TEST_ULONG = new Integer(88888888);
    protected static final Integer TEST_LONG = new Integer(-1);

    protected static final int INDEX_DISCRETE = 1;
    protected static final int INDEX_CHAR = 2;
    protected static final int INDEX_INT = 3;
    protected static final int INDEX_U_INT = 4;
    protected static final int INDEX_LONG = 5;
    protected static final int INDEX_U_LONG = 6;
    protected static final int INDEX_DOUBLE = 7;
    protected static final int INDEX_FLOAT = 8;
    protected static final int INDEX_UCHAR = 8;

    protected ByteArrayOutputStream discreteByteStream = new ByteArrayOutputStream();
    protected ByteArrayOutputStream charByteStream = new ByteArrayOutputStream();
    protected ByteArrayOutputStream ucharByteStream = new ByteArrayOutputStream();
    protected ByteArrayOutputStream intByteStream = new ByteArrayOutputStream();
    protected ByteArrayOutputStream uintByteStream = new ByteArrayOutputStream();
    protected ByteArrayOutputStream longByteStream = new ByteArrayOutputStream();
    protected ByteArrayOutputStream ulongByteStream = new ByteArrayOutputStream();
    protected ByteArrayOutputStream floatByteStream = new ByteArrayOutputStream();
    protected ByteArrayOutputStream doubleByteStream = new ByteArrayOutputStream();

    protected DataOutputStream discreteDataStream = new DataOutputStream(discreteByteStream);
    protected DataOutputStream ucharDataStream = new DataOutputStream(ucharByteStream);
    protected DataOutputStream charDataStream = new DataOutputStream(charByteStream);
    protected DataOutputStream intDataStream = new DataOutputStream(intByteStream);
    protected DataOutputStream uintDataStream = new DataOutputStream(uintByteStream);
    protected DataOutputStream longDataStream = new DataOutputStream(longByteStream);
    protected DataOutputStream ulongDataStream = new DataOutputStream(ulongByteStream);
    protected DataOutputStream floatDataStream = new DataOutputStream(floatByteStream);
    protected DataOutputStream doubleDataStream = new DataOutputStream(doubleByteStream);

    @Test
    public void testSerializeIndex() throws IOException {
        int bigIndex = 40000;
        addObjectToByteArray(GdnDataType.NUMBER_FOR_TYPE_FLOAT, bigIndex, TEST_FLOAT, floatDataStream);
        ByteBuffer byteBuffer = ByteBuffer.wrap(floatByteStream.toByteArray());
        StandardReportItem pointReportItem = StandardReportItem.standardReportItemFromByteBuffer(byteBuffer);
        assertEquals(pointReportItem.getIndex(), bigIndex);
    }

    @Test
    public void testFloatPointReportItem() throws IOException {
        addObjectToByteArray(GdnDataType.NUMBER_FOR_TYPE_FLOAT, INDEX_FLOAT, TEST_FLOAT, floatDataStream);
        StandardReportItem pointReportItem = StandardReportItem.standardReportItemFromByteBuffer(ByteBuffer.wrap(floatByteStream.toByteArray()));
        assertEquals(pointReportItem.getIndex(), INDEX_FLOAT);
        assertEquals(pointReportItem.getValue().dataType(), GdnDataType.Float);
        assertEquals(Float.class, pointReportItem.getValue().getValue().getClass());
        assertEquals(TEST_FLOAT, pointReportItem.getValue().getValue());
    }

    @Test
    public void testULongPointReportItem() throws IOException {
        addObjectToByteArray(GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_LONG, INDEX_U_LONG, TEST_ULONG, ulongDataStream);
        StandardReportItem pointReportItem = StandardReportItem.standardReportItemFromByteBuffer(ByteBuffer.wrap(ulongByteStream.toByteArray()));
        assertEquals(pointReportItem.getIndex(), INDEX_U_LONG);
        assertEquals(pointReportItem.getValue().dataType(), GdnDataType.UnsignedLong);
        assertEquals(Long.class, pointReportItem.getValue().getValue().getClass());
        assertEquals(TEST_ULONG.longValue(), ((Long)pointReportItem.getValue().getValue()).longValue());
    }

    @Test
    public void testLongPointReportItem() throws IOException {
        addObjectToByteArray(GdnDataType.NUMBER_FOR_TYPE_LONG, INDEX_LONG, TEST_LONG, longDataStream);
        StandardReportItem pointReportItem = StandardReportItem.standardReportItemFromByteBuffer(ByteBuffer.wrap(longByteStream.toByteArray()));
        assertEquals(pointReportItem.getIndex(), INDEX_LONG);
        assertEquals(pointReportItem.getValue().dataType(), GdnDataType.Long);
        assertEquals(Integer.class, pointReportItem.getValue().getValue().getClass());
        assertEquals(TEST_LONG, pointReportItem.getValue().getValue());
    }

    @Test
    public void testUIntPointReportItem() throws IOException {
        addObjectToByteArray(GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_INTEGER, INDEX_U_INT, TEST_USHORT, uintDataStream);
        StandardReportItem pointReportItem = StandardReportItem.standardReportItemFromByteBuffer(ByteBuffer.wrap(uintByteStream.toByteArray()));
        assertEquals(pointReportItem.getIndex(), INDEX_U_INT);
        assertEquals(pointReportItem.getValue().dataType(), GdnDataType.UnsignedInteger);
        assertEquals(Integer.class, pointReportItem.getValue().getValue().getClass());
        assertEquals(TEST_USHORT.intValue(), ((Integer)pointReportItem.getValue().getValue()).intValue());
    }

    @Test
    public void testIntPointReportItem() throws IOException {
        addObjectToByteArray(GdnDataType.NUMBER_FOR_TYPE_INTEGER, INDEX_INT, TEST_SHORT, intDataStream);
        StandardReportItem pointReportItem = StandardReportItem.standardReportItemFromByteBuffer(ByteBuffer.wrap(intByteStream.toByteArray()));
        assertEquals(pointReportItem.getIndex(), INDEX_INT);
        assertEquals(pointReportItem.getValue().dataType(), GdnDataType.Integer);
        assertEquals(Integer.class, pointReportItem.getValue().getValue().getClass());
        assertEquals(TEST_SHORT.intValue(), ((Integer)pointReportItem.getValue().getValue()).intValue());
    }

    @Test
    public void testCharPointReportItem() throws IOException {
        addObjectToByteArray(GdnDataType.NUMBER_FOR_TYPE_BYTE, INDEX_CHAR, TEST_CHAR, charDataStream);
        StandardReportItem pointReportItem = StandardReportItem.standardReportItemFromByteBuffer(ByteBuffer.wrap(charByteStream.toByteArray()));
        assertEquals(pointReportItem.getIndex(), INDEX_CHAR);
        assertEquals(pointReportItem.getValue().dataType(), GdnDataType.Byte);
        assertEquals(Integer.class, pointReportItem.getValue().getValue().getClass());
        assertEquals(TEST_CHAR.intValue(), ((Number)pointReportItem.getValue().getValue()).intValue());
    }

    @Test
    public void testUCharPointReportItem() throws IOException {
        addObjectToByteArray(GdnDataType.NUMBER_FOR_TYPE_UNSIGNED_BYTE, INDEX_UCHAR, TEST_UCHAR, ucharDataStream);
        StandardReportItem pointReportItem = StandardReportItem.standardReportItemFromByteBuffer(ByteBuffer.wrap(ucharByteStream.toByteArray()));
        assertEquals(pointReportItem.getIndex(), INDEX_UCHAR);
        assertEquals(pointReportItem.getValue().dataType(), GdnDataType.UnsignedByte);
        assertEquals(Integer.class, pointReportItem.getValue().getValue().getClass());
        assertEquals(TEST_UCHAR.intValue(), ((Integer)pointReportItem.getValue().getValue()).intValue());
    }

    @Test
    public void testDiscretePointReportItem() throws IOException {
        addObjectToByteArray(GdnDataType.NUMBER_FOR_TYPE_DISCRETE, INDEX_DISCRETE, TEST_DISCRETE, discreteDataStream);
        StandardReportItem pointReportItem = StandardReportItem.standardReportItemFromByteBuffer(ByteBuffer.wrap(discreteByteStream.toByteArray()));
        assertEquals(pointReportItem.getIndex(), INDEX_DISCRETE);
        assertEquals(pointReportItem.getValue().dataType(), GdnDataType.Discrete);
        assertEquals(Integer.class, pointReportItem.getValue().getValue().getClass());
        assertEquals(TEST_DISCRETE.intValue(), ((Integer)pointReportItem.getValue().getValue()).intValue());
    }

    protected void addObjectToByteArray(int type, int index, Object object, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeShort(index);
        dataOutputStream.writeByte(type);
        if (object instanceof Byte) {
            dataOutputStream.writeByte(((Byte)object).byteValue());
        } else if (object instanceof Float) {
            dataOutputStream.writeFloat(((Float)object).floatValue());
        } else if (object instanceof Short) {
            dataOutputStream.writeShort(((Short)object).shortValue());
        } else if (object instanceof Double) {
            dataOutputStream.writeDouble(((Double)object).doubleValue());
        } else if (object instanceof Integer) {
            dataOutputStream.writeInt(((Integer)object).intValue());
        } else if (object instanceof Long) {
            dataOutputStream.writeLong(((Long)object).longValue());
        } else {
            throw new IllegalArgumentException("Invalid object passed: " + object.getClass().getName());
        }
    }
}
