package zedi.pacbridge.gdn.messages;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import zedi.pacbridge.gdn.GdnAlarmStatus;
import zedi.pacbridge.gdn.GdnFloat;
import zedi.pacbridge.gdn.GdnInteger;
import zedi.pacbridge.test.BaseTestCase;

public abstract class ReportMessageTestCase extends BaseTestCase {

    public static final float FLOAT_VALUE = (float)1.1;
    public static final Integer INDEX2 = 43;
    public static final Integer PID = 1;
    public static final Date TIMESTAMP1 = new Date();
    public static final Date TIMESTAMP2 = new Date(TIMESTAMP1.getTime() + 10000);
    public static final Integer INTEGER_VALUE = new Integer(5);
    public static final int INDEX1 = 10;
    

    public static StandardReportMessage standardReportMessageForTest() {
        List<StandardReportItem> list = Arrays.asList(reportItem());
        return new StandardReportMessage(list, PID, TIMESTAMP1);
    }

    public static StandardReportItem reportItem() {
        return new StandardReportItem(INDEX1, new GdnInteger(INTEGER_VALUE));
    }

    public static ExtendedReportMessage extendedReportMessageForTest() {
        
        ExtendedReportItem extendedReportItem1 = new ExtendedReportItem(INDEX1, new GdnInteger(INTEGER_VALUE), GdnAlarmStatus.High);
        ExtendedReportItem extendedReportItem2 = new ExtendedReportItem(INDEX2, new GdnFloat(FLOAT_VALUE), GdnAlarmStatus.Low);

        List<ExtendedReportItem> list = Arrays.asList(extendedReportItem1, extendedReportItem2);

        ExtendedReportMessage extendedReportMessage = new ExtendedReportMessage(list, GdnReasonCode.AlarmTrigger, 1, TIMESTAMP1);

        return extendedReportMessage;
    }
}
