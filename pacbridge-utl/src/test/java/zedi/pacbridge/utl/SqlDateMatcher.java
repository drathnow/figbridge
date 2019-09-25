package zedi.pacbridge.utl;

import java.sql.Date;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class SqlDateMatcher extends BaseMatcher<Date> {
    private Date theDate;
    
    public SqlDateMatcher(Date theDate) {
        this.theDate = theDate;
    }
    
    @Override
    public boolean matches(Object otherDate) {
        return theDate.getTime() == ((Date)otherDate).getTime();
    }

    @Override
    public void describeTo(Description description) {
    }
}