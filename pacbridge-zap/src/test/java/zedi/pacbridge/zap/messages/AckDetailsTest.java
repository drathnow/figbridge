package zedi.pacbridge.zap.messages;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import java.nio.ByteBuffer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.test.BaseTestCase;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AckDetails.class, 
                 WriteIoPointsControlAckDetails.class, 
                 DemandPollControlAckDetails.class,
                 ConfigureResponseAckDetails.class,
                 ScrubControlAckDetails.class})
public class AckDetailsTest extends BaseTestCase {

    @Test
    public void shouldDeserializeScrubMessageAckDetails() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        byteBuffer.position(2);
        byteBuffer.put(AckDetailsType.ScrubResult.getNumber().byteValue());
        byteBuffer.put(ScrubControlAckDetails.VERSION1.byteValue());
        byteBuffer.put((byte)1);
        byteBuffer.putShort(ScrubControl.MSG_SCRUB_EVENTS.shortValue());
        byteBuffer.flip();
                
        ScrubControlAckDetails ackDetails = mock(ScrubControlAckDetails.class);
        mockStatic(ScrubControlAckDetails.class);
        given(ScrubControlAckDetails.scrubResultsFromByteBuffer(byteBuffer)).willReturn(ackDetails);

        AckDetails result = AckDetails.ackDetailsFromByteBuffer(byteBuffer);
        
        assertSame(ackDetails, result);
        verifyStatic(ScrubControlAckDetails.class);
        ScrubControlAckDetails.scrubResultsFromByteBuffer(byteBuffer);
    }
    
    @Test
    public void shouldHandlConfigureResponseAckDetails() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(3);
        byteBuffer.position(2);
        byteBuffer.put(AckDetailsType.ConfigureResponse.getNumber().byteValue());
        byteBuffer.flip();

        ConfigureResponseAckDetails ackDetails = mock(ConfigureResponseAckDetails.class);
        mockStatic(ConfigureResponseAckDetails.class);
        given(ConfigureResponseAckDetails.configureResponseAckDetailsFromByteBuffer(byteBuffer)).willReturn(ackDetails);

        AckDetails result = AckDetails.ackDetailsFromByteBuffer(byteBuffer);
        
        assertSame(ackDetails, result);
        verifyStatic(ConfigureResponseAckDetails.class);
        ConfigureResponseAckDetails.configureResponseAckDetailsFromByteBuffer(byteBuffer);
    }
    
    @Test
    public void shouldBuildDemandPollAckDetails() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(3);
        byteBuffer.position(2);
        byteBuffer.put(AckDetailsType.DemandPoll.getNumber().byteValue());
        byteBuffer.flip();
        
        DemandPollControlAckDetails ackDetails = mock(DemandPollControlAckDetails.class);
        mockStatic(DemandPollControlAckDetails.class);
        
        given(DemandPollControlAckDetails.demandPollControlAckDetailsFromByteBuffer(byteBuffer)).willReturn(ackDetails);
        
        AckDetails result = AckDetails.ackDetailsFromByteBuffer(byteBuffer);
        
        assertSame(ackDetails, result);
        verifyStatic(DemandPollControlAckDetails.class);
        DemandPollControlAckDetails.demandPollControlAckDetailsFromByteBuffer(byteBuffer);
    }
    
    @Test
    public void shouldBuildWriteIOPointsAckDetails() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(3);
        byteBuffer.position(2);
        byteBuffer.put(AckDetailsType.WriteIoPoints.getNumber().byteValue());
        byteBuffer.flip();
        
        WriteIoPointsControlAckDetails ackDetails = mock(WriteIoPointsControlAckDetails.class);
        mockStatic(WriteIoPointsControlAckDetails.class);
        
        given(WriteIoPointsControlAckDetails.writeIoPointsMessageAckDetailsFromByteBuffer(byteBuffer)).willReturn(ackDetails);
        
        AckDetails result = AckDetails.ackDetailsFromByteBuffer(byteBuffer);
        
        assertSame(ackDetails, result);
        verifyStatic(WriteIoPointsControlAckDetails.class);
        WriteIoPointsControlAckDetails.writeIoPointsMessageAckDetailsFromByteBuffer(byteBuffer);
    }
}
