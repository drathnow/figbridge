package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.io.Unsigned;
import zedi.pacbridge.zap.ZapMessageType;

public class OtadRequestControlTest extends BaseTestCase {
	private static final Long EVENT_ID = 2345L;
	private static final String URL = "http://foo.man.choo/file.zip";
	private static final String MD5_HASH = "01234567ABCDEF";
	private static final Integer RETRIES = 2;
    private static final Integer RETRY_INTERVAL = 30;
	private static final Integer TIMEOUT = 100;
	
	@Test
    public void shouldSerializeOtadFlags() throws Exception {
	    ByteBuffer byteBuffer = ByteBuffer.allocate(1);

	    OtadRequestControl.OtadFlags flags = new OtadRequestControl.OtadFlags();
        flags.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals(0, byteBuffer.get());
        
        flags.setForceRestart(true);
        byteBuffer.flip();
        flags.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals(1, byteBuffer.get());
        
        flags.setForceRestart(false);
        flags.setUseAuthentication(true);
        byteBuffer.flip();
        flags.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals(2, byteBuffer.get());

        flags.setForceRestart(true);
        flags.setUseAuthentication(true);
        byteBuffer.flip();
        flags.serialize(byteBuffer);
        byteBuffer.flip();
        assertEquals(3, byteBuffer.get());
	}
	
	@Test
	public void shouldReturnCorrectMessageType() throws Exception {
        OtadRequestControl.OtadFlags flags = new OtadRequestControl.OtadFlags();
		OtadRequestControl request = new OtadRequestControl(EVENT_ID, flags, URL, MD5_HASH, RETRIES, RETRY_INTERVAL, TIMEOUT);
		assertEquals(EVENT_ID, request.getEventId());
		assertEquals(ZapMessageType.OtadRequest, request.messageType());
		assertEquals(URL, request.getOtadFileUrl());
		assertEquals(MD5_HASH, request.getMd5Hash());
		assertEquals(RETRIES, request.getRetries());
		assertEquals(RETRY_INTERVAL, request.getRetryIntervalSeconds());
		assertEquals(TIMEOUT, request.getTimeoutSeconds());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldDetectInvalidRetries() throws Exception {
        OtadRequestControl.OtadFlags flags = new OtadRequestControl.OtadFlags();
		new OtadRequestControl(EVENT_ID, flags, URL, MD5_HASH, 65555, RETRY_INTERVAL, TIMEOUT);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldDetectInvalidTImeout() throws Exception {
        OtadRequestControl.OtadFlags flags = new OtadRequestControl.OtadFlags();
		new OtadRequestControl(EVENT_ID, flags, URL, MD5_HASH, RETRIES, RETRY_INTERVAL, 65555);
	}

	@Test
	public void shouldCalculateCorrectSize() throws Exception {
        OtadRequestControl.OtadFlags flags = new OtadRequestControl.OtadFlags();
		OtadRequestControl request = new OtadRequestControl(EVENT_ID, flags, URL, MD5_HASH, RETRIES, RETRY_INTERVAL, TIMEOUT);
		int expectedSize = 20 + MD5_HASH.length() + URL.length();
		assertEquals(expectedSize, request.size().intValue());
	}
	
	@Test
	public void shouldSerializeRequestWithNoForceRestart() throws Exception {
		ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        OtadRequestControl.OtadFlags flags = new OtadRequestControl.OtadFlags();
        flags.setUseAuthentication(true);
		OtadRequestControl request = new OtadRequestControl(EVENT_ID, flags, URL, MD5_HASH, RETRIES, RETRY_INTERVAL, TIMEOUT);
		request.serialize(byteBuffer);
		byteBuffer.flip();
		
		assertEquals(OtadRequestControl.VERSION1.byteValue(), byteBuffer.get());
		assertEquals(EVENT_ID.longValue(), byteBuffer.getLong());
		assertEquals((int)OtadRequestControl.OTAD_FLAGS_USE_AUTHENTICATION_MASK, byteBuffer.get());
		assertEquals(TIMEOUT.shortValue(), Unsigned.getUnsignedShort(byteBuffer));
		assertEquals(RETRIES.shortValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(RETRY_INTERVAL.shortValue(), Unsigned.getUnsignedShort(byteBuffer));
		
		assertEquals(URL.length(), Unsigned.getUnsignedShort(byteBuffer));
		byte[] bytes = new byte[URL.length()];
		byteBuffer.get(bytes);
		assertEquals(URL, new String(bytes));
		
		assertEquals(MD5_HASH.length(), Unsigned.getUnsignedShort(byteBuffer));
		bytes = new byte[MD5_HASH.length()];
		byteBuffer.get(bytes);		
		assertEquals(MD5_HASH, new String(bytes));
	}

    @Test
    public void shouldSerializeRequestWithForceRestart() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        OtadRequestControl.OtadFlags flags = new OtadRequestControl.OtadFlags();
        flags.setForceRestart(true);
        OtadRequestControl request = new OtadRequestControl(EVENT_ID, flags, URL, MD5_HASH, RETRIES, RETRY_INTERVAL, TIMEOUT);
        request.serialize(byteBuffer);
        byteBuffer.flip();
        
        assertEquals(OtadRequestControl.VERSION1.byteValue(), byteBuffer.get());
        assertEquals(EVENT_ID.longValue(), byteBuffer.getLong());
        int flagByte = byteBuffer.get();
        
        assertEquals(1, flagByte);
        assertTrue((flagByte & OtadRequestControl.OTAD_FLAGS_FORCE_RESTART_MASK) != 0);
        assertTrue((flagByte & OtadRequestControl.OTAD_FLAGS_USE_AUTHENTICATION_MASK) == 0);
        
        assertEquals(TIMEOUT.shortValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(RETRIES.shortValue(), Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(RETRY_INTERVAL.shortValue(), Unsigned.getUnsignedShort(byteBuffer));
        
        assertEquals(URL.length(), Unsigned.getUnsignedShort(byteBuffer));
        byte[] bytes = new byte[URL.length()];
        byteBuffer.get(bytes);
        assertEquals(URL, new String(bytes));
        
        assertEquals(MD5_HASH.length(), Unsigned.getUnsignedShort(byteBuffer));
        bytes = new byte[MD5_HASH.length()];
        byteBuffer.get(bytes);      
        assertEquals(MD5_HASH, new String(bytes));
    }
}
