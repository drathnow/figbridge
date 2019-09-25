package zedi.pacbridge.utl;

import java.util.Arrays;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class ByteArrayMatcher extends BaseMatcher<byte[]> {

    private byte[] expectedArray;
    
    public ByteArrayMatcher(byte[] expectedArray) {
        this.expectedArray = expectedArray;
    }
    
    @Override
    public boolean matches(Object otherArray) {
        return Arrays.equals(expectedArray, (byte[])otherArray);
    }

    @Override
    public void describeTo(Description description) {
    }

    public static ByteArrayMatcher matchesArrayOfBytes(byte[] array) {
        return new ByteArrayMatcher(array); 
    }
}
