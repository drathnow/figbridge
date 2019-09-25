package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import zedi.pacbridge.test.BaseTestCase;

public class ZapReasonCodeTest extends BaseTestCase {
    @Test
    public void shouldIndicateHighPriority() throws Exception {
        assertFalse(ZapReasonCode.Reserved.isHighPriority());
        assertTrue(ZapReasonCode.AlarmTrigger.isHighPriority());
        assertTrue(ZapReasonCode.AlarmModify.isHighPriority());
        assertTrue(ZapReasonCode.IOWrite.isHighPriority());
        assertTrue(ZapReasonCode.IOModify.isHighPriority());
        assertTrue(ZapReasonCode.DemandPoll.isHighPriority());
        assertFalse(ZapReasonCode.Scheduled.isHighPriority());
        assertFalse(ZapReasonCode.Unknown.isHighPriority());
    }
    
    @Test
    public void shouldReturnCorrectReasonCodeForNumber() throws Exception {
        assertEquals(ZapReasonCode.Reserved, ZapReasonCode.reasonCodeForReasonNumber(ZapReasonCode.RESERVED_NUMBER));
        assertEquals(ZapReasonCode.AlarmTrigger, ZapReasonCode.reasonCodeForReasonNumber(ZapReasonCode.ALARM_TRIGGER_NUMBER));
        assertEquals(ZapReasonCode.AlarmModify, ZapReasonCode.reasonCodeForReasonNumber(ZapReasonCode.ALARM_MODIFY_NUMBER));
        assertEquals(ZapReasonCode.IOWrite, ZapReasonCode.reasonCodeForReasonNumber(ZapReasonCode.IO_WRITE_NUMBER));
        assertEquals(ZapReasonCode.IOModify, ZapReasonCode.reasonCodeForReasonNumber(ZapReasonCode.IO_MODIFY_NUMBER));
        assertEquals(ZapReasonCode.DemandPoll, ZapReasonCode.reasonCodeForReasonNumber(ZapReasonCode.DEMAND_POLL_NUMBER));
        assertEquals(ZapReasonCode.Scheduled, ZapReasonCode.reasonCodeForReasonNumber(ZapReasonCode.SCHEDULED_NUMBER));
        assertEquals(ZapReasonCode.Unknown, ZapReasonCode.reasonCodeForReasonNumber(ZapReasonCode.UNKNOWN_NUMBER));        
        assertEquals(ZapReasonCode.Unknown, ZapReasonCode.reasonCodeForReasonNumber(909999));        
    }
    
    @Test
    public void shouldReturnCorrectReasonCodeForName() throws Exception {
        assertEquals(ZapReasonCode.Reserved, ZapReasonCode.reasonCodeForName(ZapReasonCode.RESERVED_NAME));
        assertEquals(ZapReasonCode.AlarmTrigger, ZapReasonCode.reasonCodeForName(ZapReasonCode.ALARM_TRIGGER_NAME));
        assertEquals(ZapReasonCode.AlarmModify, ZapReasonCode.reasonCodeForName(ZapReasonCode.ALARM_MODIFY_NAME));
        assertEquals(ZapReasonCode.IOWrite, ZapReasonCode.reasonCodeForName(ZapReasonCode.IO_WRITE_NAME));
        assertEquals(ZapReasonCode.IOModify, ZapReasonCode.reasonCodeForName(ZapReasonCode.IO_MODIFY_NAME));
        assertEquals(ZapReasonCode.DemandPoll, ZapReasonCode.reasonCodeForName(ZapReasonCode.DEMAND_POLL_NAME));
        assertEquals(ZapReasonCode.Scheduled, ZapReasonCode.reasonCodeForName(ZapReasonCode.SCHEDULED_NAME));
        assertEquals(ZapReasonCode.Unknown, ZapReasonCode.reasonCodeForName(ZapReasonCode.UNKNOWN_NAME));
        assertEquals(ZapReasonCode.Unknown, ZapReasonCode.reasonCodeForName("Spooge"));
    }
}
