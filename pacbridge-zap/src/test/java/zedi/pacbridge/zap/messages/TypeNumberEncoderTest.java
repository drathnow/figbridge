package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class TypeNumberEncoderTest extends BaseTestCase {

    @Test
    public void shouldDecodeType() throws Exception {
        short value = TypeNumberEncoder.encodedNumberFor(5, 100);
        assertEquals(5, TypeNumberEncoder.typeNumberFromEncodedValue(value).intValue());
    }
    
    @Test
    public void shouldDecodeTagNumber() throws Exception {
        short value = TypeNumberEncoder.encodedNumberFor(5, 100);
        assertEquals(100, TypeNumberEncoder.tagNumberFromEncodedValue(value).intValue());
    }
    
    @Test
    public void shouldEncodeTypeNumber() throws Exception {
        short value = TypeNumberEncoder.encodedNumberFor(5, 100);
        assertEquals(5, (value & 0xf000) >> 12);
        assertEquals(100, value & 0x0fff);
    }
}
