package zedi.pacbridge.gdn.messages;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;

import org.junit.Test;

import zedi.pacbridge.gdn.DataTypeFactory;
import zedi.pacbridge.gdn.GdnAlarmStatus;
import zedi.pacbridge.gdn.GdnDataType;
import zedi.pacbridge.gdn.GdnValue;
import zedi.pacbridge.gdn.GdnValueFactory;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.io.Unsigned;


public class ExtendedReportItemTest extends BaseTestCase {

    private static final int INDEX = 1;
    private static final int FLOAT_TYPE_NUMBER = GdnDataType.Float.getNumber();
    private static final int ALARM_STATUS_TYPE_NUMBER = GdnAlarmStatus.High.getTypeNumber(); 
    
    @Test
    public void shouldSerializeExtendedReportItem() throws Exception {
        GdnValue<?> gdnValue = mock(GdnValue.class);

        ExtendedReportItem reportItem = new ExtendedReportItem(INDEX, gdnValue, GdnAlarmStatus.High);
        
        when(gdnValue.dataType()).thenReturn(GdnDataType.Float);
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        reportItem.serialize(byteBuffer);
        
        byteBuffer.flip();
        assertEquals(INDEX, Unsigned.getUnsignedShort(byteBuffer));
        assertEquals(FLOAT_TYPE_NUMBER, Unsigned.getUnsignedByte(byteBuffer));
        assertEquals(ALARM_STATUS_TYPE_NUMBER, Unsigned.getUnsignedByte(byteBuffer));
        verify(gdnValue).serialize(eq(byteBuffer));
    }    
    
    @Test
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void shouldDeserializeExtendedReportItem() throws Exception {
        GdnValue gdnValue = mock(GdnValue.class);
        GdnDataType dataType = mock(GdnDataType.class);
        GdnAlarmStatus alarmStatus = mock(GdnAlarmStatus.class);
        GdnValueFactory valueFactory = mock(GdnValueFactory.class);
        DataTypeFactory dataTypeFactory = mock(DataTypeFactory.class);
        AlarmStatusFactory alarmStatusFactory = mock(AlarmStatusFactory.class);
        
        when(alarmStatusFactory.alarmStatusForAlarmStatusNumber(ALARM_STATUS_TYPE_NUMBER)).thenReturn(alarmStatus);
        when(dataTypeFactory.dataTypeForTypeNumber(FLOAT_TYPE_NUMBER)).thenReturn(dataType);
        when(valueFactory.valueForDataType(dataType)).thenReturn(gdnValue);
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        byteBuffer.putShort((short)INDEX);
        byteBuffer.put((byte)FLOAT_TYPE_NUMBER);
        byteBuffer.put((byte)ALARM_STATUS_TYPE_NUMBER);
        byteBuffer.flip();

        ExtendedReportItem reportItem = new ExtendedReportItem(valueFactory, dataTypeFactory, alarmStatusFactory);
        reportItem.deserialize(byteBuffer);
        
        assertEquals(INDEX, reportItem.getIndex());
        assertEquals(alarmStatus, reportItem.getAlarmStatus());
        assertEquals(gdnValue, reportItem.getValue());
        verify(gdnValue).deserialize(eq(byteBuffer));
    }
}
