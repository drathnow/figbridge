package zedi.pacbridge.utl;

import java.util.Arrays;

import org.mockito.ArgumentMatcher;

public class ByteArrayArgumentMatcher implements ArgumentMatcher<byte[]> {

    private byte[] expectedArray;
    
    public ByteArrayArgumentMatcher(byte[] expectedArray) {
        this.expectedArray = expectedArray;
    }

    public static ByteArrayArgumentMatcher matchesByteArrayArgument(byte[] array) {
        return new ByteArrayArgumentMatcher(array); 
    }

	@Override
	public boolean matches(byte[] otherArray) {
        return Arrays.equals(expectedArray, (byte[])otherArray);
	}
}
