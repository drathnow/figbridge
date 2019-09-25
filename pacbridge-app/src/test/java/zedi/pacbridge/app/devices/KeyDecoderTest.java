package zedi.pacbridge.app.devices;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.HexStringEncoder;

@RunWith(PowerMockRunner.class)
@PrepareForTest({KeyDecoder.class, DefaultSecretKeyDecoder.class})
public class KeyDecoderTest extends BaseTestCase {

    private static final byte[] KEY_BYTES = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, (byte)0xFF};
    private static final String BASE64_KEY_STRING = "AAAAAAECAwQFBgcICQr/"; // Encoding with u32(0) at the front
    private static final String BASE64_KEY_STRING_WITH_UNKNOWN_TYPE = "Dw/gAAECAwQFBgcICQr/";
    private static final String BASE64_KEY_STRING_TOO_SHORT = "Dw8=";

    class ByteBufferArgumentMatcher implements ArgumentMatcher<ByteBuffer> {

    	private byte[] byteArray;
    	
    	public ByteBufferArgumentMatcher(byte[] byteArray) {
    		this.byteArray = byteArray;
		}
    	
		@Override
		public boolean matches(ByteBuffer argument) {
			if (argument.limit() - argument.position() != KEY_BYTES.length)
				return false;
			
			int j = 0;
			for (int i = argument.position(); i < argument.limit(); i++) {
				if (argument.array()[i] != KEY_BYTES[j++])
					return false;
			}
			return false;
		}
    	
    }
    
    @Test
    public void shouldDecodeBase64StringWithDefaultEncoder() throws Exception {
        DefaultSecretKeyDecoder defaultDecoder = mock(DefaultSecretKeyDecoder.class);
        whenNew(DefaultSecretKeyDecoder.class).withNoArguments().thenReturn(defaultDecoder);
        given(defaultDecoder.secretKeyFromByteBuffer(argThat(matchesByteBufferArgContainingBytes(KEY_BYTES)))).willReturn(KEY_BYTES);
        
        KeyDecoder decoder = new KeyDecoder();
        byte[] result = decoder.decodedBytesForBase64EncodedBytes(BASE64_KEY_STRING.getBytes());
        System.out.println("Result: " + HexStringEncoder.bytesAsHexString(result));
        System.out.println("Key String: " + HexStringEncoder.bytesAsHexString(KEY_BYTES));
        assertTrue(Arrays.equals(result, KEY_BYTES));
        verifyNew(DefaultSecretKeyDecoder.class).withNoArguments();
    }

	@Test (expected = SecretKeyDecoderException.class)
    public void shouldThrowExceptionIfNoDecoderTypeFound() throws Exception {
        KeyDecoder decoder = new KeyDecoder();
        decoder.decodedBytesForBase64EncodedBytes(BASE64_KEY_STRING_WITH_UNKNOWN_TYPE.getBytes());
    }
    
    @Test (expected = SecretKeyDecoderException.class)
    public void shouldThrowExceptionIsBase64StringHasTooFewBytes() throws Exception {
        KeyDecoder decoder = new KeyDecoder();
        decoder.decodedBytesForBase64EncodedBytes(BASE64_KEY_STRING_TOO_SHORT.getBytes());
    }


    private ArgumentMatcher<ByteBuffer> matchesByteBufferArgContainingBytes(byte[] keyBytes) {
		return new ByteBufferArgumentMatcher(KEY_BYTES);
	}
}

