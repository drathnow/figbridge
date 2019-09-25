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


public class GdnUnsignedLongTest extends BaseTestCase {
    protected static final Long TESTLONG1 = new Long(Integer.MAX_VALUE);
    protected static final byte[] bytes = new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff};
    protected static final long UPPER_GDN_LONG = 0xffffffffL;
    private GdnUnsignedLong gdnUnsignedLong;
    protected ByteArrayOutputStream byteArrayOutputStream;
    protected DataOutputStream dataOutputStream;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        gdnUnsignedLong = new GdnUnsignedLong(TESTLONG1);
        byteArrayOutputStream = new ByteArrayOutputStream();
        dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    }

    @After
    public void tearDown() throws Exception {
        gdnUnsignedLong = null;
        super.tearDown();
    }

    @Test
    public void testSetStringValue() {
        gdnUnsignedLong = new GdnUnsignedLong("2");
        assertEquals(2,(((Long)gdnUnsignedLong.getValue()).longValue()),0.1);
    }

    @Test
    public void shouldSerializeToByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(20);
        GdnUnsignedLong gdnUnsignedLong = new GdnUnsignedLong(new Long(UPPER_GDN_LONG));

        gdnUnsignedLong.serialize(byteBuffer);

        byteBuffer.flip();
        assertEquals(UPPER_GDN_LONG, Unsigned.getUnsignedInt(byteBuffer));
    }
    
    @Test
    public void shouldDeserializeFromByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(20);
        byteBuffer.putInt((int)UPPER_GDN_LONG);
        byteBuffer.flip();
        
        GdnUnsignedLong gdnUnsignedLong = new GdnUnsignedLong();
        gdnUnsignedLong.deserialize(byteBuffer);
        
        assertEquals(UPPER_GDN_LONG, ((Number)gdnUnsignedLong.getValue()).longValue());        
    }
}
