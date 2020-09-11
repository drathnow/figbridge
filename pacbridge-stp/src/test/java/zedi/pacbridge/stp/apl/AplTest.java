package zedi.pacbridge.stp.apl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import zedi.pacbridge.net.ByteBufferManager;
import zedi.pacbridge.net.LowerLayer;
import zedi.pacbridge.net.UpperLayer;
import zedi.pacbridge.test.BaseTestCase;

public class AplTest extends BaseTestCase {

    @Mock
    private AplEncoder aplEncoder;
    @Mock
    private AplDecoder aplDecoder;
    @Mock
    private UpperLayer upperLayer;
    @Mock
    private LowerLayer lowerLayer;
    @Mock
    private ByteBufferManager byteBufferManager;
    
    @Test
    @Ignore
    public void shouldname() throws Exception {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Apl.DEFAULT_BUFFER_SIZE+1);
        
        when(byteBufferManager.allocateByteBufferWithSize(anyInt())).thenReturn(ByteBuffer.allocate(100));

        Apl framingLayer = new Apl(aplDecoder, aplEncoder, byteBufferManager);
//        framingLayer.setUpperLayer(upperLayer);
//        framingLayer.setLowerLayer(lowerLayer);
//        framingLayer.transmitData(byteBuffer);
        
        verify(byteBufferManager).allocateByteBufferWithSize(eq((Apl.DEFAULT_BUFFER_SIZE+1)*2));
        
    }
    
    @Test
    @Ignore
    public void shouldEncodeMessageAndPassToLowerLayer() throws Exception {
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        
        when(byteBufferManager.allocateByteBufferWithSize(anyInt())).thenReturn(ByteBuffer.allocate(100));
        
        Apl framingLayer = new Apl(aplDecoder, aplEncoder, byteBufferManager);
//        framingLayer.setUpperLayer(upperLayer);
//        framingLayer.setLowerLayer(lowerLayer);
//        framingLayer.transmitData(byteBuffer);
        
        verify(aplEncoder).encodeDataFromSrcBufferToDstBuffer(eq(byteBuffer), any(ByteBuffer.class));
        verify(lowerLayer).transmitData(any(ByteBuffer.class));
    }
    
    @Test
    @Ignore
    public void shouldDecodeMessageAndPassToUpperLayer() throws Exception {
        byte[] bytes1 = new byte[]{1, 2};
        byte[] bytes2 = new byte[]{3, 4};
        ByteBuffer byteBuffer = mock(ByteBuffer.class);

        InOrder inOrder = inOrder(upperLayer);
        
        when(aplDecoder.nextMessage())
            .thenReturn(bytes1)
            .thenReturn(bytes2)
            .thenReturn(null);
        
        Apl framingLayer = new Apl(aplDecoder, aplEncoder, byteBufferManager);
//        framingLayer.setUpperLayer(upperLayer);
//        framingLayer.setLowerLayer(lowerLayer);
//        framingLayer.handleReceivedData(byteBuffer);
        
        verify(aplDecoder).decodeBytesFromByteBuffer(eq(byteBuffer));
//        inOrder.verify(upperLayer).handleReceivedData(argThat(matchesByteBufferWithBytes(bytes1)));
//        inOrder.verify(upperLayer).handleReceivedData(argThat(matchesByteBufferWithBytes(bytes2)));
    }

//    private ByteBufferMatcher matchesByteBufferWithBytes(byte[] bytes) {
//        return new ByteBufferMatcher(bytes);
//    }
}
