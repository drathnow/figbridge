package zedi.pacbridge.utl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringToObjectConverterTest {

    @Test
    public void shouldConvertStringToInteger() {
        assertEquals(123, ((Integer)StringToObjectConverter.objectForString("123")).intValue());
        assertEquals(123, ((Integer)StringToObjectConverter.objectForString("+123")).intValue());
        assertEquals(-123, ((Integer)StringToObjectConverter.objectForString("-123")).intValue());
    }

    @Test
    public void shouldConvertStringToFloat() {
        assertEquals(123.1, ((Float)StringToObjectConverter.objectForString("123.1")).floatValue(), 0.01);
        assertEquals(123.1, ((Float)StringToObjectConverter.objectForString("+123.1")).floatValue(), 0.01);
        assertEquals(-123.1, ((Float)StringToObjectConverter.objectForString("-123.1")).floatValue(), 0.01);
    }

    @Test
    public void shouldConvertStringToString() {
        assertEquals("123", StringToObjectConverter.objectForString("123").toString());
    }

}
