package zedi.pacbridge.utl;

import java.util.Arrays;

import org.mockito.ArgumentMatcher;

public class ByteArrayMatcher implements ArgumentMatcher<byte[]> {

    private byte[] expectedArray;
    
    public ByteArrayMatcher(byte[] expectedArray) {
        this.expectedArray = expectedArray;
    }
    
    @Override
    public boolean matches(byte[] otherArray) {
        return Arrays.equals(expectedArray, (byte[])otherArray);
    }

    public static ByteArrayMatcher matchesArrayOfBytes(byte[] array) {
        return new ByteArrayMatcher(array); 
    }
}
