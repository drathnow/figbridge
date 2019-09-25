package zedi.pacbridge.gdn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;


public class ValueSerializerTest extends BaseTestCase {
    private static final Float FLOAT_VALUE = new Float(1.22);
    private static final Integer DISCRETE_VALUE = new Integer(1);
    private static final Integer BYTE_VALUE = new Integer(-1);
    private static final Integer UBYTE_VALUE = new Integer(255);
    private static final Integer INT_VALUE = new Integer(22);
    private static final Integer UINT_VALUE = new Integer(234);
    private static final Long LONG_VALUE = new Long(2233L);
    private static final Long ULONG_VALUE = new Long(333L);
    private static final String BYTES_VALUE = "CgsMDQ==";

    @Test
    public void testValueObjectForBlobType() throws IOException, IllegalArgumentException {
        ValueSerializer valueSerializer = new ValueSerializer();
        Object object = valueSerializer.valueObjectForType(BYTES_VALUE, GdnDataType.Binary);
        assertTrue(object instanceof byte[]);
        byte[] bytes = (byte[])object;
        assertEquals(4,bytes.length);
        assertEquals(bytes[0], 0x0a);
        assertEquals(bytes[1], 0x0b);
        assertEquals(bytes[2], 0x0c);
        assertEquals(bytes[3], 0x0d);
    }

    @Test
    public void testValueObjectForBlobTypeWithZeroLengthBlob() throws IOException, IllegalArgumentException {
        ValueSerializer valueSerializer = new ValueSerializer();
        assertNull(valueSerializer.valueObjectForType(null, GdnDataType.Binary));
    }

    @Test
    public void testValueObjectForEmptyValue() throws IOException, IllegalArgumentException {
        ValueSerializer valueSerializer = new ValueSerializer();
        assertNull(valueSerializer.valueObjectForType(ULONG_VALUE.toString(), GdnDataType.EmptyValue));
    }

    @Test
    public void testValueObjectForUnsignedLongType() throws IOException, IllegalArgumentException {
        ValueSerializer valueSerializer = new ValueSerializer();
        doTestForNumber(ULONG_VALUE, (Number)valueSerializer.valueObjectForType(ULONG_VALUE.toString(), GdnDataType.Long), Long.class);
    }

    @Test
    public void testValueObjectForLongType() throws IOException, IllegalArgumentException {
        ValueSerializer valueSerializer = new ValueSerializer();
        doTestForNumber(LONG_VALUE, (Number)valueSerializer.valueObjectForType(LONG_VALUE.toString(), GdnDataType.Long), Long.class);
    }

    @Test
    public void testValueObjectForUnsignedIntegerType() throws IOException, IllegalArgumentException {
        ValueSerializer valueSerializer = new ValueSerializer();
        doTestForNumber(UINT_VALUE, (Number)valueSerializer.valueObjectForType(UINT_VALUE.toString(), GdnDataType.UnsignedInteger), Integer.class);
    }

    @Test
    public void testValueObjectForIntegerType() throws IOException, IllegalArgumentException {
        ValueSerializer valueSerializer = new ValueSerializer();
        doTestForNumber(INT_VALUE, (Number)valueSerializer.valueObjectForType(INT_VALUE.toString(), GdnDataType.Integer), Integer.class);
    }

    @Test
    public void testValueObjectForUnsignedByteType() throws IOException, IllegalArgumentException {
        ValueSerializer valueSerializer = new ValueSerializer();
        doTestForNumber(UBYTE_VALUE, (Number)valueSerializer.valueObjectForType(UBYTE_VALUE.toString(), GdnDataType.UnsignedByte), Integer.class);
    }

    @Test
    public void testValueObjectForByteType() throws IOException, IllegalArgumentException {
        ValueSerializer valueSerializer = new ValueSerializer();
        doTestForNumber(BYTE_VALUE, (Number)valueSerializer.valueObjectForType(BYTE_VALUE.toString(), GdnDataType.Byte), Integer.class);
    }

    @Test
    public void testValueObjectForDiscreteType() throws IOException, IllegalArgumentException {
        ValueSerializer valueSerializer = new ValueSerializer();
        doTestForNumber(DISCRETE_VALUE, (Number)valueSerializer.valueObjectForType(DISCRETE_VALUE.toString(), GdnDataType.Discrete), Integer.class);
    }

    @Test
    public void testValueObjectForFloatType() throws IOException, IllegalArgumentException {
        ValueSerializer valueSerializer = new ValueSerializer();
        doTestForNumber(FLOAT_VALUE, (Number)valueSerializer.valueObjectForType(FLOAT_VALUE.toString(), GdnDataType.Float), Float.class);
    }

    protected void doTestForNumber(Number testValue, Number number, Class<?> theClass) {
        assertTrue(number.getClass().equals(theClass));
        assertEquals(testValue, number);
    }
}
