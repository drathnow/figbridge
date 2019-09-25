package zedi.pacbridge.app.messaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.zap.messages.ZapAlarmStatus;
import zedi.pacbridge.zap.reporting.IoPointReading;
import zedi.pacbridge.zap.reporting.IoPointTemplate;
import zedi.pacbridge.zap.values.ZapDataType;
import zedi.pacbridge.zap.values.ZapValue;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SiteReportItem.class, SiteReportItemBuilder.class})
public class SiteReportItemBuilderTest extends BaseTestCase {
    private static final Long INDEX = 1L;
    private static final ZapDataType DATA_TYPE = ZapDataType.UnsignedByte;
    private static final ZapAlarmStatus ALARM_STATUS = ZapAlarmStatus.High;
    private static final String VALUE_STRING = "1.2";

    @Test
    public void shouldShouldBuildEmptyValueReportItem() throws Exception {
        IoPointTemplate template = mock(IoPointTemplate.class);
        IoPointReading reading = mock(IoPointReading.class);
        
        given(reading.isEmptyValue()).willReturn(true);
        given(reading.alarmStatus()).willReturn(ALARM_STATUS);
        
        SiteReportItemBuilder builder = new SiteReportItemBuilder();
        assertNull(builder.siteReportItemForTemplateAndReading(template, reading));

        verify(reading, never()).value();
    }
    
    @Test
    public void shouldShouldBuildNullValueReportItem() throws Exception {
        IoPointTemplate template = mock(IoPointTemplate.class);
        IoPointReading reading = mock(IoPointReading.class);
        SiteReportItem item = mock(SiteReportItem.class);
        
        given(reading.isEmptyValue()).willReturn(false);
        given(reading.isNullValue()).willReturn(true);
        given(reading.alarmStatus()).willReturn(ALARM_STATUS);
        given(template.dataType()).willReturn(DATA_TYPE);
        given(template.index()).willReturn(INDEX);
        
        whenNew(SiteReportItem.class).withArguments(DATA_TYPE, INDEX, ALARM_STATUS).thenReturn(item);
        
        SiteReportItemBuilder builder = new SiteReportItemBuilder();
        assertEquals(item, builder.siteReportItemForTemplateAndReading(template, reading));

        verify(reading, never()).value();
        verifyNew(SiteReportItem.class).withArguments(DATA_TYPE, INDEX, ALARM_STATUS);
    }
    
    @Test
    public void shouldShouldBuildFullValueReportItem() throws Exception {
        IoPointTemplate template = mock(IoPointTemplate.class);
        IoPointReading reading = mock(IoPointReading.class);
        SiteReportItem item = mock(SiteReportItem.class);
        ZapValue value = mock(ZapValue.class);
        
        given(reading.isEmptyValue()).willReturn(false);
        given(reading.isNullValue()).willReturn(false);
        given(reading.value()).willReturn(value);
        given(reading.alarmStatus()).willReturn(ALARM_STATUS);
        given(template.dataType()).willReturn(DATA_TYPE);
        given(template.index()).willReturn(INDEX);
        given(value.toString()).willReturn(VALUE_STRING);
        
        whenNew(SiteReportItem.class).withArguments(DATA_TYPE, INDEX, VALUE_STRING, ALARM_STATUS).thenReturn(item);
        
        SiteReportItemBuilder builder = new SiteReportItemBuilder();
        assertEquals(item, builder.siteReportItemForTemplateAndReading(template, reading));

        verify(reading).value();
        verifyNew(SiteReportItem.class).withArguments(DATA_TYPE, INDEX, VALUE_STRING, ALARM_STATUS);
    }
}
