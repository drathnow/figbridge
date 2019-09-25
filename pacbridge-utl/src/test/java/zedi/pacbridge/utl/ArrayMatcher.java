package zedi.pacbridge.utl;

import java.util.Arrays;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class ArrayMatcher<TArray> extends BaseMatcher<TArray> {

    private TArray[] expectedArray;
    
    public ArrayMatcher(TArray[] expectedArray) {
        this.expectedArray = expectedArray;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean matches(Object otherArray) {
        return Arrays.equals(expectedArray, (TArray[])otherArray);
    }

    @Override
    public void describeTo(Description description) {
    }

    public static <V> ArrayMatcher<V> matchesArrayOfTypes(V[] array) {
        return new ArrayMatcher<V>(array); 
    }
}
