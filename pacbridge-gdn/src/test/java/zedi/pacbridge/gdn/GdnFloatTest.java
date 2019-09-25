package zedi.pacbridge.gdn;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.Utilities;


public class GdnFloatTest extends BaseTestCase {

    private GdnFloat gdnFloat;
    protected static final Float TESTLONG1 = new Float(Float.MAX_VALUE);
    protected static final byte[] LONG_BYTES = new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff};
    protected ByteArrayOutputStream byteArrayOutputStream;
    protected DataOutputStream dataOutputStream;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        gdnFloat = new GdnFloat(TESTLONG1);
        byteArrayOutputStream = new ByteArrayOutputStream();
        dataOutputStream = new DataOutputStream(byteArrayOutputStream);
    }

    @After
    public void tearDown() throws Exception {
        gdnFloat = null;
        super.tearDown();
    }

    @Test
    public void shouldSerializable() throws Exception {
        GdnFloat gdnFloat = new GdnFloat(1.3F);
        byte[] bytes = Utilities.objectAsByteArrays(gdnFloat);
        gdnFloat = (GdnFloat)Utilities.byteArrayAsObject(bytes);
    }
    
    @Test
    public void testSetStringValue() {
        GdnFloat gdnFloat = new GdnFloat("2.3");
        assertEquals((float)2.3,(((Float)gdnFloat.getValue()).floatValue()),0.1);
    }

    @Test
    public void shouldSerializeOutput() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        GdnFloat gdnFloat = new GdnFloat(2.3f);
        
        gdnFloat.serialize(byteBuffer);
        
        byteBuffer.flip();
        assertEquals(2.3f, byteBuffer.getFloat(), 0.001);
    }

    @Test
    public void shouldSerializeInput() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        GdnFloat gdnFloat = new GdnFloat(2.3f);
        
        gdnFloat.serialize(byteBuffer);
        
        byteBuffer.flip();
        gdnFloat.serialize(byteBuffer);
        assertEquals(2.3F, ((Number)gdnFloat.getValue()).floatValue(), 0.001);
    }
    
}
