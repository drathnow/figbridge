package zedi.pacbridge.test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class StartsWithStringMatcher  extends BaseMatcher<String> {

    private String startsWithMatcher;
    
    public StartsWithStringMatcher(String startsWithMatcher) {
        this.startsWithMatcher = startsWithMatcher;
    }
    
    @Override
    public boolean matches(Object item) {
        return ((String) item).startsWith(startsWithMatcher);
    }

    @Override
    public void describeTo(Description description) {
    }
    
}