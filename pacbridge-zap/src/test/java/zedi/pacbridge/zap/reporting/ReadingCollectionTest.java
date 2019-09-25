package zedi.pacbridge.zap.reporting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.zap.values.ZapDataType;

@RunWith(PowerMockRunner.class)
@PrepareForTest({IoPointReading.class, ReadingCollection.class})
public class ReadingCollectionTest extends BaseTestCase {

    private static final ZapDataType DATATYPE1 = ZapDataType.UnsignedInteger;
    private static final ZapDataType DATATYPE2 = ZapDataType.UnsignedByte;
    private static final Integer TIMESTAMP = 100;
    
    @Test
    public void shouldShouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        IoPointTemplate template1 = mock(IoPointTemplate.class);
        IoPointTemplate template2 = mock(IoPointTemplate.class);
        IoPointReading reading1 = mock(IoPointReading.class);
        IoPointReading reading2 = mock(IoPointReading.class);
        
        mockStatic(IoPointReading.class);
        given(IoPointReading.ioPointReadingFromByteBuffer(DATATYPE1, byteBuffer)).willReturn(reading1);
        given(IoPointReading.ioPointReadingFromByteBuffer(DATATYPE2, byteBuffer)).willReturn(reading2);
        given(byteBuffer.getInt()).willReturn(TIMESTAMP);
        given(template1.dataType()).willReturn(DATATYPE1);
        given(template2.dataType()).willReturn(DATATYPE2);

        List<IoPointTemplate> templates = new ArrayList<>();
        templates.add(template1);
        templates.add(template2);
        
        InOrder inOrder = inOrder(byteBuffer, template1, template2);
        
        ReadingCollection collection = ReadingCollection.readingCollectionFromByteBuffer(templates, byteBuffer);
        
        assertEqualDates(new Date(TIMESTAMP*1000L), collection.timestamp(), 0);
        List<IoPointReading> readings = collection.ioPointReadings();
        assertEquals(2, readings.size());
        assertSame(reading1, readings.get(0));
        assertSame(reading2, readings.get(1));
        
        inOrder.verify(byteBuffer).getInt();
        inOrder.verify(template1).dataType();
        inOrder.verify(template2).dataType();
    }
}
