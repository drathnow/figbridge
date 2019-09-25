package zedi.pacbridge.zap.values;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import zedi.pacbridge.net.Value;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.Utilities;

public class ZapDataTypeTest extends BaseTestCase {

    @Test
    public void shouldReturnCorrectDataTypeForName() throws Exception {
        assertEquals(ZapDataType.EmptyValue, ZapDataType.dataTypeForName(ZapDataType.EMPTY_VALUE_NAME));
        assertEquals(ZapDataType.Discrete, ZapDataType.dataTypeForName(ZapDataType.DISCRETE_NAME));
        assertEquals(ZapDataType.Byte, ZapDataType.dataTypeForName(ZapDataType.BYTE_NAME));
        assertEquals(ZapDataType.UnsignedByte, ZapDataType.dataTypeForName(ZapDataType.UNSIGNED_BYTE_NAME));
        assertEquals(ZapDataType.Integer, ZapDataType.dataTypeForName(ZapDataType.SHORT_NAME));
        assertEquals(ZapDataType.UnsignedInteger, ZapDataType.dataTypeForName(ZapDataType.UNSIGNED_SHORT_NAME));
        assertEquals(ZapDataType.Integer, ZapDataType.dataTypeForName(ZapDataType.INTEGER_NAME));
        assertEquals(ZapDataType.UnsignedInteger, ZapDataType.dataTypeForName(ZapDataType.UNSIGNED_INTEGER_NAME));
        assertEquals(ZapDataType.Long, ZapDataType.dataTypeForName(ZapDataType.LONG_NAME));
        assertEquals(ZapDataType.UnsignedLong, ZapDataType.dataTypeForName(ZapDataType.UNSIGNED_LONG_NAME));
        assertEquals(ZapDataType.Float, ZapDataType.dataTypeForName(ZapDataType.FLOAT_NAME)); 
        assertEquals(ZapDataType.Double, ZapDataType.dataTypeForName(ZapDataType.DOUBLE_NAME));
        assertEquals(ZapDataType.Binary, ZapDataType.dataTypeForName(ZapDataType.BINARY_NAME));
        assertEquals(ZapDataType.String, ZapDataType.dataTypeForName(ZapDataType.STRING_NAME));
    }
    
    @Test
    public void shouldReturnCorrectDataTypeForNumber() throws Exception {
        assertEquals(ZapDataType.EmptyValue, ZapDataType.dataTypeForTypeNumber(ZapDataType.EMPTY_VALUE));
        assertEquals(ZapDataType.Discrete, ZapDataType.dataTypeForTypeNumber(ZapDataType.DISCRETE));
        assertEquals(ZapDataType.Byte, ZapDataType.dataTypeForTypeNumber(ZapDataType.BYTE));
        assertEquals(ZapDataType.UnsignedByte, ZapDataType.dataTypeForTypeNumber(ZapDataType.UNSIGNED_BYTE));
        assertEquals(ZapDataType.Integer, ZapDataType.dataTypeForTypeNumber(ZapDataType.INTEGER));
        assertEquals(ZapDataType.UnsignedInteger, ZapDataType.dataTypeForTypeNumber(ZapDataType.UNSIGNED_INTEGER));
        assertEquals(ZapDataType.Long, ZapDataType.dataTypeForTypeNumber(ZapDataType.LONG));
        assertEquals(ZapDataType.UnsignedLong, ZapDataType.dataTypeForTypeNumber(ZapDataType.UNSIGNED_LONG));
        assertEquals(ZapDataType.Float, ZapDataType.dataTypeForTypeNumber(ZapDataType.FLOAT)); 
        assertEquals(ZapDataType.Double, ZapDataType.dataTypeForTypeNumber(ZapDataType.DOUBLE));
        assertEquals(ZapDataType.Binary, ZapDataType.dataTypeForTypeNumber(ZapDataType.BINARY));
        assertEquals(ZapDataType.String, ZapDataType.dataTypeForTypeNumber(ZapDataType.STRING));
    }

    @Test
    public void shouldConvertDouble() throws Exception {
        Value value = ZapDataType.Double.valueForString("1");
        assertNotNull(value);
        assertTrue(value instanceof ZapDouble);
    }
    
    @Test
    public void shouldConvertFloat() throws Exception {
        Value value = ZapDataType.Float.valueForString("1");
        assertNotNull(value);
        assertTrue(value instanceof ZapFloat);
    }
    
    @Test
    public void shouldConvertUnsignedLong() throws Exception {
        Value value = ZapDataType.UnsignedLong.valueForString("1");
        assertNotNull(value);
        assertTrue(value instanceof ZapUnsignedLong);
    }

    @Test
    public void shouldConvertLong() throws Exception {
        Value value = ZapDataType.Long.valueForString("1");
        assertNotNull(value);
        assertTrue(value instanceof ZapLong);
    }
    
    @Test
    public void shouldConvertUnsignedShort() throws Exception {
        Value value = ZapDataType.UnsignedInteger.valueForString("1");
        assertNotNull(value);
        assertTrue(value instanceof ZapUnsignedShort);
    }

    @Test
    public void shouldConvertShort() throws Exception {
        Value value = ZapDataType.Integer.valueForString("1");
        assertNotNull(value);
        assertTrue(value instanceof ZapShort);
    }

    @Test
    public void shouldConvertUnsignedByte() throws Exception {
        Value value = ZapDataType.UnsignedByte.valueForString("1");
        assertNotNull(value);
        assertTrue(value instanceof ZapUnsignedByte);
    }
    
    @Test
    public void shouldConvertByte() throws Exception {
        Value value = ZapDataType.Byte.valueForString("1");
        assertNotNull(value);
        assertTrue(value instanceof ZapByte);
    }
    
    @Test
    public void shouldConvertDiscrete() throws Exception {
        Value value = ZapDataType.Discrete.valueForString("1");
        assertNotNull(value);
        assertTrue(value instanceof ZapDiscrete);
    }
    
    @Test
    public void shouldSerialize() throws Exception {
        Utilities.byteArrayAsObject(Utilities.objectAsByteArrays(ZapDataType.Binary));
        Utilities.byteArrayAsObject(Utilities.objectAsByteArrays(ZapDataType.String));
        Utilities.byteArrayAsObject(Utilities.objectAsByteArrays(ZapDataType.Float));
    }

}
