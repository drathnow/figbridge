package zedi.pacbridge.zap.reporting;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.nio.ByteBuffer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.zap.values.ZapDataType;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IoPointTemplate.class})
public class IoPointTemplateTest extends BaseTestCase {

    private static final ZapDataType DATATYPE = ZapDataType.Byte;
    private static final Long INDEX = 122L;
    
    @Test
    public void shouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        IoPointTemplate template = mock(IoPointTemplate.class);
        InOrder inOrder = Mockito.inOrder(byteBuffer);
        
        given(byteBuffer.getInt()).willReturn(INDEX.intValue());
        given(byteBuffer.get()).willReturn(DATATYPE.getNumber().byteValue());
        
        whenNew(IoPointTemplate.class).withArguments(INDEX, DATATYPE).thenReturn(template);
        
        assertSame(template, IoPointTemplate.templateFromByteBuffer(byteBuffer));

        inOrder.verify(byteBuffer).getInt();
        inOrder.verify(byteBuffer).get();
    }
}
