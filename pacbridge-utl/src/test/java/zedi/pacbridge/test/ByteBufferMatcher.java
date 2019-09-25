package zedi.pacbridge.test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class ByteBufferMatcher extends BaseMatcher<ByteBuffer> {

    private byte[] expectedBytes;
    
    public ByteBufferMatcher(byte[] expectedBytes) {
        this.expectedBytes = expectedBytes;
    }

    public boolean matches(Object arg0) {
        ByteBuffer buffer = (ByteBuffer)arg0;
        if (buffer.position() != 0)
            buffer.flip();
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        return Arrays.equals(expectedBytes, bytes);
    }

    public void describeTo(Description arg0) {
    }
    
}
