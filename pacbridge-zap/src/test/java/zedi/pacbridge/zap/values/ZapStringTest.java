package zedi.pacbridge.zap.values;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ZapStringTest extends BaseTestCase {
    private static final String VALUE = "herm3";
    
    @Test
    public void shouldReturnCorrectSize() throws Exception {
        ZapString string = new ZapString(VALUE);
        assertEquals(VALUE.length()+Short.SIZE/8, string.serializedSize().intValue());
    }
}
