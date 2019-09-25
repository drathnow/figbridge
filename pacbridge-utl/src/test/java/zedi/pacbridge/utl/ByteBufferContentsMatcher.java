package zedi.pacbridge.utl;

import java.nio.ByteBuffer;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class ByteBufferContentsMatcher extends BaseMatcher<ByteBuffer> {
    
    private byte[] bytes;
    
    public ByteBufferContentsMatcher(byte[] bytes) {
        this.bytes = bytes;
    }
    
    @Override
    public boolean matches(Object item) {
        ByteBuffer byteBuffer = (ByteBuffer)item;
        if (byteBuffer.remaining() != bytes.length)
            return false;
        ByteBuffer sliceBuffer = byteBuffer.slice();
        int idx = 0;
        while (sliceBuffer.hasRemaining()) {
            if (bytes[idx++] != sliceBuffer.get())
                return false;
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
    }

    public static BaseMatcher<ByteBuffer> matchesByteBufferContainingBytes(byte[] bytes) {
        return new ByteBufferContentsMatcher(bytes);
    }
}