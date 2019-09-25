package zedi.pacbridge.gdn;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.io.Unsigned;


public class GdnUnsignedIntegerTest extends BaseTestCase {
    protected static final Integer TESTSHORT1 = new Integer(65535);
    protected static final byte[] bytes = new byte[]{(byte)0xff, (byte)0xff};
    private GdnUnsignedInteger gdnUnsignedInteger;
    protected ByteArrayOutputStream byteArrayOutputStream;
    protected DataOutputStream dataOutputStream;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        gdnUnsignedInteger = new GdnUnsignedInteger(TESTSHORT1);
        byteArrayOutputStream = new ByteArrayOutputStream();
        dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    }

    @After
    public void testSetStringValue() {
        gdnUnsignedInteger = new GdnUnsignedInteger("2");
        assertEquals(2,(((Integer)gdnUnsignedInteger.getValue()).intValue()),0.1);
    }
    
    @Test
    public void shouldSerializeToByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(20);
        gdnUnsignedInteger = new GdnUnsignedInteger(TESTSHORT1);
        
        gdnUnsignedInteger.serialize(byteBuffer);
        
        byteBuffer.flip();
        assertEquals(TESTSHORT1.intValue(), Unsigned.getUnsignedShort(byteBuffer));
    }

    @Test
    public void shouldDeserializeFromByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(20);
        byteBuffer.putShort((short)TESTSHORT1.intValue());
        byteBuffer.flip();
        
        GdnUnsignedInteger gdnUnsignedInteger = new GdnUnsignedInteger();
        
        gdnUnsignedInteger.deserialize(byteBuffer);
        assertEquals(TESTSHORT1.intValue(), ((Number)gdnUnsignedInteger.getValue()).intValue());
    }
    
    @Test
    public void testToString() {
        gdnUnsignedInteger = new GdnUnsignedInteger(0x0000FFFF);
        assertEquals(Integer.toString(0x0000FFFF), gdnUnsignedInteger.toString());
    }
}
