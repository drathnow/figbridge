package zedi.pacbridge.gdn.messages;

import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

import zedi.pacbridge.gdn.GdnAlarmStatus;
import zedi.pacbridge.gdn.GdnUnsignedInteger;

public abstract class ResponseStrategyTestCase {

    public static final int INDEX1 = 1;
    public static final int INDEX2 = 2;
    public static final int INDEX3 = 3;
    public static final int DIAG_INDEX = 237;
    public static final int POLLSET_NUMBER = 2;
    public static final float VALUE = 2.2f;
    public static final long EVENT_ID = 4;
    public static final int ERROR_STATUS = 3;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    protected ExtendedReportMessage extendedReportMessage(int index, int pollsetNumber, GdnAlarmStatus alarmStatus, GdnReasonCode reasonCode) {
        ExtendedReportItem item = new ExtendedReportItem(index, new GdnUnsignedInteger(2), alarmStatus);
        return new ExtendedReportMessage(Arrays.asList(item), reasonCode, pollsetNumber, new Date());
    }
    
    protected StandardReportMessage standardReportMessage(int index, int pollsetNumber) {
        StandardReportItem item = new StandardReportItem(index, new GdnUnsignedInteger(2));
        return new StandardReportMessage(Arrays.asList(item), pollsetNumber, new Date());
    }
        
}
