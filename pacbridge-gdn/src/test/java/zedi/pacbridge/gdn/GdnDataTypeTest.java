package zedi.pacbridge.gdn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;

import zedi.pacbridge.net.Value;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.Utilities;


public class GdnDataTypeTest extends BaseTestCase {


    @Test
    public void shouldConvertFloat() throws Exception {
        Value value = GdnDataType.Float.valueForString("1");
        assertNotNull(value);
        assertTrue(value instanceof GdnFloat);
    }
    
    @Test
    public void shouldConvertUnsignedLong() throws Exception {
        Value value = GdnDataType.UnsignedLong.valueForString("1");
        assertNotNull(value);
        assertTrue(value instanceof GdnUnsignedLong);
    }

    @Test
    public void shouldConvertLong() throws Exception {
        Value value = GdnDataType.Long.valueForString("1");
        assertNotNull(value);
        assertTrue(value instanceof GdnLong);
    }
    
    @Test
    public void shouldConvertUnsignedInteger() throws Exception {
        Value value = GdnDataType.UnsignedInteger.valueForString("1");
        assertNotNull(value);
        assertTrue(value instanceof GdnUnsignedInteger);
    }

    @Test
    public void shouldConvertInteger() throws Exception {
        Value value = GdnDataType.Integer.valueForString("1");
        assertNotNull(value);
        assertTrue(value instanceof GdnInteger);
    }

    @Test
    public void shouldConvertUnsignedByte() throws Exception {
        Value value = GdnDataType.UnsignedByte.valueForString("1");
        assertNotNull(value);
        assertTrue(value instanceof GdnUnsignedByte);
    }
    
    @Test
    public void shouldConvertByte() throws Exception {
        Value value = GdnDataType.Byte.valueForString("1");
        assertNotNull(value);
        assertTrue(value instanceof GdnByte);
    }
    
    @Test
    public void shouldConvertDiscrete() throws Exception {
        Value value = GdnDataType.Discrete.valueForString("1");
        assertNotNull(value);
        assertTrue(value instanceof GdnDiscrete);
    }
    
    @Test
    public void shouldSerialize() throws Exception {
        byte[] bytes = Utilities.objectAsByteArrays(GdnDataType.Binary);
        Utilities.byteArrayAsObject(bytes);

        bytes = Utilities.objectAsByteArrays(GdnDataType.Float);
        Utilities.byteArrayAsObject(bytes);
    }
    
    @Test
    public void testDataTypeForTypeName() throws Exception {
        Field[] fields = GdnDataType.class.getFields();
        for (Field f : fields) {
            Class<?> c = f.getType();
            if (c == GdnDataType.class) {
                Method m = c.getMethod("getName", (Class[])null);
                GdnDataType dataType = (GdnDataType)f.get(null);
                String keyName = (String)m.invoke(dataType, (Object[])null);
                assertEquals("Key not found: " 
                        + dataType.getName(), dataType, GdnDataType.dataTypeForName(keyName));
            }
        }
    }

    @Test
    public void testDataTypeForTypeNumber() throws Exception {
        assertEquals(GdnDataType.EmptyValue, GdnDataType.dataTypeForTypeNumber(GdnDataType.EmptyValue.getNumber()));
        assertEquals(GdnDataType.Discrete, GdnDataType.dataTypeForTypeNumber(GdnDataType.Discrete.getNumber()));
        assertEquals(GdnDataType.Byte, GdnDataType.dataTypeForTypeNumber(GdnDataType.Byte.getNumber()));
        assertEquals(GdnDataType.UnsignedByte, GdnDataType.dataTypeForTypeNumber(GdnDataType.UnsignedByte.getNumber()));
        assertEquals(GdnDataType.Integer, GdnDataType.dataTypeForTypeNumber(GdnDataType.Integer.getNumber()));
        assertEquals(GdnDataType.UnsignedInteger, GdnDataType.dataTypeForTypeNumber(GdnDataType.UnsignedInteger.getNumber()));
        assertEquals(GdnDataType.Long, GdnDataType.dataTypeForTypeNumber(GdnDataType.Long.getNumber()));
        assertEquals(GdnDataType.UnsignedLong, GdnDataType.dataTypeForTypeNumber(GdnDataType.UnsignedLong.getNumber()));
        assertEquals(GdnDataType.Float, GdnDataType.dataTypeForTypeNumber(GdnDataType.Float.getNumber()));
        assertEquals(GdnDataType.Binary, GdnDataType.dataTypeForTypeNumber(GdnDataType.Binary.getNumber()));
        assertNull(GdnDataType.dataTypeForTypeNumber(999));
    }
}