package zedi.pacbridge.gdn;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.io.Unsigned;


public class GdnUnsignedByteTest extends BaseTestCase {
    protected static final Integer UBYTE_VALUE = new Integer(255);
    protected static final Byte TESTBYTE = new Byte((byte)0xff);
    private GdnUnsignedByte gdnUnsignedByte;
    protected ByteArrayOutputStream byteArrayOutputStream;
    protected DataOutputStream dataOutputStream;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        gdnUnsignedByte = new GdnUnsignedByte(255);
        byteArrayOutputStream = new ByteArrayOutputStream();
        dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    }

    @Test
    public void testSetStringValue() {
        gdnUnsignedByte = new GdnUnsignedByte("2");
        assertEquals(2,(((Integer)gdnUnsignedByte.getValue()).intValue()),0.1);
    }
    
    @Test
    public void shouldSerializeToByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        GdnUnsignedByte gdnUnsignedByte = new GdnUnsignedByte(UBYTE_VALUE);
        
        gdnUnsignedByte.serialize(byteBuffer);
        
        byteBuffer.flip();
        assertEquals(UBYTE_VALUE.intValue(), Unsigned.getUnsignedByte(byteBuffer));
    }

    @Test
    public void shouldDeserilizeFromByteBuffer() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put((byte)UBYTE_VALUE.intValue());
        byteBuffer.flip();
        
        GdnUnsignedByte gdnUnsignedByte = new GdnUnsignedByte();
        gdnUnsignedByte.deserialize(byteBuffer);
        
        assertEquals(UBYTE_VALUE.intValue(), ((Integer)gdnUnsignedByte.getValue()).intValue());
    }
    
    @Test
    public void testGetValue() {
        assertEquals(255, ((Integer)gdnUnsignedByte.getValue()).intValue());
    }

}
