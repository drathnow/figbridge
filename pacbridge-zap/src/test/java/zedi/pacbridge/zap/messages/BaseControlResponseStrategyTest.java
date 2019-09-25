package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import zedi.pacbridge.net.controls.ControlStatus;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.zap.ZapMessageType;

public class BaseControlResponseStrategyTest extends BaseTestCase {

    private class MyTestingBaseControlResponseStrategy extends BaseControlResponseStrategy {

        protected MyTestingBaseControlResponseStrategy(ZapMessageType expectedMessageType, Integer expectedSequenceNumber) {
            super(expectedMessageType, expectedSequenceNumber);
        }

        @Override
        public boolean expectsAsyncReport() {
            return false;
        }
    }
    
    public static final Integer SEQUENCE_NUMBER = 1;
    
    @Test
    public void shouldHandleNotProtocolError() throws Exception {
        DemandPollControl control = mock(DemandPollControl.class);
        AckMessage ackMessage = mock(AckMessage.class);
        
        given(ackMessage.isProtocolError()).willReturn(false);
        given(ackMessage.getAckedMessageType()).willReturn(ZapMessageType.DemandPoll);
        given(ackMessage.sequenceNumber()).willReturn(SEQUENCE_NUMBER);
        given(control.sequenceNumber()).willReturn(SEQUENCE_NUMBER);
        
        MyTestingBaseControlResponseStrategy strategy = new MyTestingBaseControlResponseStrategy(ZapMessageType.DemandPoll, SEQUENCE_NUMBER);

        assertTrue(strategy.isNotProtocolError(ackMessage));
        assertFalse(strategy.isFinished());
        assertNull(strategy.finalStatus());
    }
    
    @Test
    public void shouldHandleProtocolError() throws Exception {
        DemandPollControl control = mock(DemandPollControl.class);
        AckMessage ackMessage = mock(AckMessage.class);
        ProtocolErrorDetails details = new ProtocolErrorDetails(ProtocolErrorType.InvalidMessageNumber);
        
        given(ackMessage.isProtocolError()).willReturn(true);
        given(ackMessage.additionalDetails()).willReturn(details);
        given(ackMessage.getAckedMessageType()).willReturn(ZapMessageType.DemandPoll);
        given(ackMessage.sequenceNumber()).willReturn(SEQUENCE_NUMBER);
        given(control.sequenceNumber()).willReturn(SEQUENCE_NUMBER);
        
        MyTestingBaseControlResponseStrategy strategy = new MyTestingBaseControlResponseStrategy(ZapMessageType.DemandPoll, SEQUENCE_NUMBER);

        assertFalse(strategy.isNotProtocolError(ackMessage));
        assertTrue(strategy.isFinished());
        assertEquals(strategy.finalStatus(), ControlStatus.FAILURE);
        assertEquals(details.toString(), strategy.finalStatusMessage());
    }

    @Test
    public void shouldReturnFalseIfIsNotExpectedMesageTypeAndNotExpectedSequenceNumber() throws Exception {
        AckMessage ackMessage = mock(AckMessage.class);
        given(ackMessage.getAckedMessageType()).willReturn(ZapMessageType.WriteIOPoints);
        given(ackMessage.sequenceNumber()).willReturn(SEQUENCE_NUMBER+1);

        MyTestingBaseControlResponseStrategy strategy = new MyTestingBaseControlResponseStrategy(ZapMessageType.DemandPoll, SEQUENCE_NUMBER);
        assertFalse(strategy.isExpectedMessage(ackMessage));
    }
    
    @Test
    public void shouldReturnFalseIfIsExpectedMesageTypeAndNotExpectedSequenceNumber() throws Exception {
        AckMessage ackMessage = mock(AckMessage.class);
        given(ackMessage.getAckedMessageType()).willReturn(ZapMessageType.DemandPoll);
        given(ackMessage.sequenceNumber()).willReturn(SEQUENCE_NUMBER+1);

        MyTestingBaseControlResponseStrategy strategy = new MyTestingBaseControlResponseStrategy(ZapMessageType.DemandPoll, SEQUENCE_NUMBER);
        assertFalse(strategy.isExpectedMessage(ackMessage));
    }
    
    @Test
    public void shouldReturnFalseIfIsNotExpectedMesageTypeAndSequenceNumber() throws Exception {
        AckMessage ackMessage = mock(AckMessage.class);
        given(ackMessage.getAckedMessageType()).willReturn(ZapMessageType.WriteIOPoints);
        given(ackMessage.sequenceNumber()).willReturn(SEQUENCE_NUMBER);

        MyTestingBaseControlResponseStrategy strategy = new MyTestingBaseControlResponseStrategy(ZapMessageType.DemandPoll, SEQUENCE_NUMBER);
        assertFalse(strategy.isExpectedMessage(ackMessage));
    }
    
    @Test
    public void shouldReturnTrueIfIsExpectedMesageTypeAndSequenceNumber() throws Exception {
        AckMessage ackMessage = mock(AckMessage.class);
        given(ackMessage.getAckedMessageType()).willReturn(ZapMessageType.DemandPoll);
        given(ackMessage.sequenceNumber()).willReturn(SEQUENCE_NUMBER);

        MyTestingBaseControlResponseStrategy strategy = new MyTestingBaseControlResponseStrategy(ZapMessageType.DemandPoll, SEQUENCE_NUMBER);
        assertTrue(strategy.isExpectedMessage(ackMessage));
    }
}
