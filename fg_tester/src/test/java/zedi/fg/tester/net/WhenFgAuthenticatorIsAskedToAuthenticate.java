package zedi.fg.tester.net;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import zedi.pacbridge.utl.NotificationCenter;

public class WhenFgAuthenticatorIsAskedToAuthenticate
{
	private static final byte[] BYTE_RESPONSE = {0x00, 0x04, 0x01, 0x02, 0x03, 0x04};
	private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	private ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(BYTE_RESPONSE);

	@Mock
	private NotificationCenter notificationCenter;
	
	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldAuthenticateDevice() throws IOException
	{
		FgAuthenticator authenticator = new FgAuthenticator(notificationCenter);
		authenticator.authenticate(byteArrayInputStream, byteArrayOutputStream);
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
		int size = byteBuffer.getShort();
		assertEquals(24, size);
		for (int i = 0; i < size; i++)
			byteBuffer.get();
		
		assertEquals(20, byteBuffer.getShort());
	}
}
