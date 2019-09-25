package zedi.pacbridge.gdn;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;


public class GdnIntegerTest extends BaseTestCase {

    protected static final Integer TESTSHORT1 = new Integer(-1);
    protected static final Integer TESTSHORT2 = new Integer(65535);
    private GdnInteger gdnInteger;

    @Test
    public void testSetStringValue() {
        gdnInteger = new GdnInteger("2");
        assertEquals(2,(((Integer)gdnInteger.getValue()).intValue()),0.1);
    }

    @Test
    public void shouldDeserializeMaxValueFromByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.putShort((short)TESTSHORT2.intValue());
        byteBuffer.flip();
        
        GdnInteger gdnInteger = new GdnInteger();
        gdnInteger.deserialize(byteBuffer);
        
        assertEquals(TESTSHORT1.intValue(), ((Number)gdnInteger.getValue()).intValue());
    }
    
    @Test
    public void shouldDeserializeNegativeNumberFromByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.putShort((short)TESTSHORT1.intValue());
        byteBuffer.flip();
        
        GdnInteger gdnInteger = new GdnInteger();
        gdnInteger.deserialize(byteBuffer);
        
        assertEquals(TESTSHORT1.intValue(), ((Number)gdnInteger.getValue()).intValue());
    }
}
