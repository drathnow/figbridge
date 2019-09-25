package zedi.pacbridge.utl;

import java.util.Date;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class DateMatcher extends BaseMatcher<Date>{
    private Date expectedDate;
    
    public DateMatcher(Date expectedDate) {
        this.expectedDate = expectedDate;
    }

    @Override
    public boolean matches(Object item) {
        return ((Date)item).compareTo(expectedDate) == 0;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Date should be ").appendValue(expectedDate.toString());
    }

    public static Matcher<Date> matchesDate(Date expectedDate) {    
        return new DateMatcher(expectedDate);
    }
}