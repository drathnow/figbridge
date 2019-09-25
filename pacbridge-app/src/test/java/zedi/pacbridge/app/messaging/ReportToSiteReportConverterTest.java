package zedi.pacbridge.app.messaging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.zap.messages.ZapReasonCode;
import zedi.pacbridge.zap.reporting.IoPointReading;
import zedi.pacbridge.zap.reporting.IoPointTemplate;
import zedi.pacbridge.zap.reporting.ReadingCollection;
import zedi.pacbridge.zap.reporting.ZapReport;
import zedi.pacbridge.zap.values.ZapDataType;
import zedi.pacbridge.zap.values.ZapValue;

public class ReportToSiteReportConverterTest extends BaseTestCase {
    private static final String ADDRESS = "1.2.3.4";
    private static final ZapReasonCode REASON_CODE = ZapReasonCode.IOModify;
    private static final Long INDEX1 = 1L;
    private static final Long INDEX2 = 2L;
    private static final ZapDataType DATA_TYPE1 = ZapDataType.UnsignedByte;
    private static final ZapDataType DATA_TYPE2 = ZapDataType.UnsignedByte;
    private static final Date TIMESTAMP1 = new Date(1);
    private static final Date TIMESTAMP2 = new Date(2);
    
    @Mock
    private IoPointTemplate template1;
    @Mock
    private IoPointTemplate template2;
    @Mock
    private IoPointReading reading11;
    @Mock
    private IoPointReading reading12;
    @Mock
    private IoPointReading reading21;
    @Mock
    private IoPointReading reading22;
    @Mock
    private ZapValue value11;
    @Mock
    private ZapValue value12;
    @Mock
    private ZapValue value21;
    @Mock
    private ZapValue value22;
    @Mock
    private ReadingCollection collection1;
    @Mock
    private ReadingCollection collection2;
    @Mock
    private SiteReportItemBuilder itemBuilder;
    @Mock
    private SiteReportItem item1;
    @Mock
    private SiteReportItem item2;
    @Mock
    private SiteReportItem item3;
    
    
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        given(reading11.isEmptyValue()).willReturn(false);
        given(reading11.isNullValue()).willReturn(false);
        given(reading11.value()).willReturn(value11);
        given(value11.toString()).willReturn("value11");
        
        given(reading12.isEmptyValue()).willReturn(false);
        given(reading12.isNullValue()).willReturn(false);
        given(reading12.value()).willReturn(value12);
        given(value12.toString()).willReturn("value12");
        
        given(reading21.isEmptyValue()).willReturn(false);
        given(reading21.isNullValue()).willReturn(false);
        given(reading21.value()).willReturn(value21);
        given(value21.toString()).willReturn("value21");
        
        given(reading22.isEmptyValue()).willReturn(true);
    }
    
    @Test
    public void shouldBuildSiteReport() throws Exception {
        List<IoPointReading> readings1 = new ArrayList<>();
        readings1.add(reading11);
        readings1.add(reading12);
        
        List<IoPointReading> readings2 = new ArrayList<>();
        readings2.add(reading21);
        readings2.add(reading22);
        
        ReadingCollection collection1 = new ReadingCollection(TIMESTAMP1, readings1);
        ReadingCollection collection2 = new ReadingCollection(TIMESTAMP2, readings2);
        
        List<IoPointTemplate> templates = new ArrayList<>();
        templates.add(template1);
        templates.add(template2);
        
        List<ReadingCollection> collections = new ArrayList<>();
        collections.add(collection1);
        collections.add(collection2);
        
        ZapReport report = mock(ZapReport.class);

        given(report.ioPointTemplate()).willReturn(templates);
        given(report.readingCollections()).willReturn(collections);
        given(report.reasonCode()).willReturn(REASON_CODE);
        
        given(template1.index()).willReturn(INDEX1);
        given(template2.index()).willReturn(INDEX2);
        given(template1.dataType()).willReturn(DATA_TYPE1);
        given(template2.dataType()).willReturn(DATA_TYPE2);
        given(reading11.value()).willReturn(value11);
        given(reading12.value()).willReturn(value12);
        given(reading21.value()).willReturn(value21);
        given(reading22.value()).willReturn(value22);
        
        given(itemBuilder.siteReportItemForTemplateAndReading(template1, reading11)).willReturn(item1);
        given(itemBuilder.siteReportItemForTemplateAndReading(template2, reading12)).willReturn(item2);
        given(itemBuilder.siteReportItemForTemplateAndReading(template1, reading21)).willReturn(item3);
        given(itemBuilder.siteReportItemForTemplateAndReading(template2, reading22)).willReturn(null);
        
        InOrder inOrder = inOrder(itemBuilder);
        ReportToSiteReportConverter converter = new ReportToSiteReportConverter();
        converter.init(ADDRESS, report, itemBuilder);
        
        SiteReport siteReport = null;
        
        siteReport = converter.nextReport();
        inOrder.verify(itemBuilder).siteReportItemForTemplateAndReading(template1, reading11);
        inOrder.verify(itemBuilder).siteReportItemForTemplateAndReading(template2, reading12);
        
        assertNotNull(siteReport);
        assertEquals(REASON_CODE, siteReport.getReasonCode());
        assertEquals(ADDRESS, siteReport.getNuid());
        assertEquals(2, siteReport.getReportItems().size());
        
        siteReport = converter.nextReport();
        verify(itemBuilder).siteReportItemForTemplateAndReading(template1, reading21);
        verify(itemBuilder, never()).siteReportItemForTemplateAndReading(template1, reading22);
        assertNotNull(siteReport);
        assertEquals(REASON_CODE, siteReport.getReasonCode());
        assertEquals(ADDRESS, siteReport.getNuid());
        assertEquals(1, siteReport.getReportItems().size());
        
        assertNull(converter.nextReport());
    }
}
