package zedi.pacbridge.zap.reporting;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.zap.messages.ZapReasonCode;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ZapReasonCode.class, ReportHeader.class, IoPointTemplate.class})
public class ReportHeaderTest extends BaseTestCase {

    private static final Long EVENT_ID = 5678L;
    private static final Integer VERSION = 1;
    private static final Integer UNIQUEID = 123;
    private static final Integer CREATIONTIME = 100;
    private static final Integer READING_COLLECTION_COUNT = 200;
    private static final ZapReasonCode REASONCODE = ZapReasonCode.AlarmModify;
    private static final Integer POLLSETNUMBER = 43;
    
    @Test
    @SuppressWarnings("unchecked")
    public void shouldDeserialize() throws Exception {
        ByteBuffer byteBuffer = mock(ByteBuffer.class);
        ReportHeader reportHeader = mock(ReportHeader.class);
        IoPointTemplate template1 = mock(IoPointTemplate.class);
        IoPointTemplate template2 = mock(IoPointTemplate.class);
        Date creationDate = mock(Date.class);
        ArrayList<IoPointTemplate> templates = mock(ArrayList.class);
    
        given(byteBuffer.get())
                .willReturn(VERSION.byteValue())
                .willReturn(REASONCODE.getNumber().byteValue());
        given(byteBuffer.getInt())
                .willReturn(UNIQUEID.intValue())
                .willReturn(CREATIONTIME.intValue());
        given(byteBuffer.getShort())
                .willReturn((short)2)
                .willReturn(READING_COLLECTION_COUNT.shortValue())
                .willReturn(POLLSETNUMBER.shortValue());
        given(byteBuffer.getLong())
                .willReturn(EVENT_ID.longValue());
        
        mockStatic(IoPointTemplate.class);
        mockStatic(ZapReasonCode.class);
        
        given(IoPointTemplate.templateFromByteBuffer(byteBuffer))
            .willReturn(template1)
            .willReturn(template2);
        
        given(ZapReasonCode.reasonCodeForReasonNumber(REASONCODE.getNumber().intValue()))
            .willReturn(REASONCODE);
        
        whenNew(ArrayList.class)
            .withNoArguments()
            .thenReturn(templates);
        
        whenNew(Date.class)
            .withArguments(CREATIONTIME*1000L)
            .thenReturn(creationDate);
        
        whenNew(ReportHeader.class)
            .withArguments(UNIQUEID, creationDate, READING_COLLECTION_COUNT, REASONCODE, POLLSETNUMBER, templates, EVENT_ID)
            .thenReturn(reportHeader);
        
        assertSame(reportHeader, ReportHeader.reportHeaderFromByteBuffer(byteBuffer));
        
        verify(byteBuffer, times(2)).get();
        verify(byteBuffer, times(2)).getInt();
        verify(byteBuffer, times(3)).getShort();
        verify(byteBuffer).getLong();
        verify(templates).add(template1);
        verify(templates).add(template2);

        verifyStatic(ZapReasonCode.class);
        ZapReasonCode.reasonCodeForReasonNumber(REASONCODE.getNumber().intValue());
        verifyStatic(IoPointTemplate.class, times(2));
        IoPointTemplate.templateFromByteBuffer(byteBuffer);
    }
}
