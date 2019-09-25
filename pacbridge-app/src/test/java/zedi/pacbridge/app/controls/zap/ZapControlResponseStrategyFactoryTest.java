package zedi.pacbridge.app.controls.zap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.cache.InterestingSitesCache;
import zedi.pacbridge.app.controls.ControlResponseStrategy;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.net.MessageType;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.messages.ConfigureControl;
import zedi.pacbridge.zap.messages.DemandPollControl;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.OtadRequestControl;
import zedi.pacbridge.zap.messages.ScrubControl;
import zedi.pacbridge.zap.messages.WriteIoPointsControl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ZapControlResponseStrategyFactory.class, 
                 ConfigureResponseStrategy.class,
                 DemandPollControlResponseStrategy.class,
                 WriteIoPointsControlResponseStrategy.class,
                 ScrubControlResponseStrategy.class})
public class ZapControlResponseStrategyFactoryTest extends BaseTestCase {
    
    @Mock
    private EventHandler eventPublisher;

    @Test
    public void shouldContructOtadRequestControlResponseStrategy() throws Exception {
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        OtadRequestControl control = mock(OtadRequestControl.class);
        SiteAddress siteAddress = mock(SiteAddress.class);
        OtadRequestControlResponseStrategy strategy = mock(OtadRequestControlResponseStrategy.class);
        FieldTypeLibrary fieldTypeLibrary = mock(FieldTypeLibrary.class);
        MessageType messageType = mock(MessageType.class);
        
        whenNew(OtadRequestControlResponseStrategy.class)
            .withArguments(control, siteAddress, eventPublisher, cache)
            .thenReturn(strategy);
        given(control.messageType()).willReturn(messageType);
        given(messageType.getNumber()).willReturn(ZapMessageType.OTAD_REQUEST_NUMBER);
        
        ZapControlResponseStrategyFactory factory = new ZapControlResponseStrategyFactory(fieldTypeLibrary, eventPublisher, cache);
        ControlResponseStrategy result = factory.responseStrategyForControl(control, siteAddress);

        assertNotNull(result);
        verifyNew(OtadRequestControlResponseStrategy.class).withArguments(control, siteAddress, eventPublisher, cache);
        verify(control).messageType();
        verify(messageType).getNumber();
        assertSame(strategy, result);
    }
    
    @Test
    public void shouldConstructScrubControlResponseStrategy() throws Exception {
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        ScrubControl control = mock(ScrubControl.class);
        SiteAddress siteAddress = mock(SiteAddress.class);
        ScrubControlResponseStrategy strategy = mock(ScrubControlResponseStrategy.class);
        FieldTypeLibrary fieldTypeLibrary = mock(FieldTypeLibrary.class);
        MessageType messageType = mock(MessageType.class);
        
        whenNew(ScrubControlResponseStrategy.class)
            .withArguments(control, siteAddress, eventPublisher, cache)
            .thenReturn(strategy);
        given(control.messageType()).willReturn(messageType);
        given(messageType.getNumber()).willReturn(ZapMessageType.SCRUB_NUMBER);
        
        ZapControlResponseStrategyFactory factory = new ZapControlResponseStrategyFactory(fieldTypeLibrary, eventPublisher, cache);
        ControlResponseStrategy result = factory.responseStrategyForControl(control, siteAddress);

        assertNotNull(result);
        verifyNew(ScrubControlResponseStrategy.class).withArguments(control, siteAddress, eventPublisher, cache);
        verify(control).messageType();
        verify(messageType).getNumber();
        assertSame(strategy, result);
    }
    
    @Test
    public void shouldConstructWriteIoPointsControlResponseStrategy() throws Exception {
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        WriteIoPointsControl control = mock(WriteIoPointsControl.class);
        SiteAddress siteAddress = mock(SiteAddress.class);
        WriteIoPointsControlResponseStrategy strategy = mock(WriteIoPointsControlResponseStrategy.class);
        FieldTypeLibrary fieldTypeLibrary = mock(FieldTypeLibrary.class);
        MessageType messageType = mock(MessageType.class);
        
        whenNew(WriteIoPointsControlResponseStrategy.class)
            .withArguments(control, siteAddress, eventPublisher, cache)
            .thenReturn(strategy);
        given(control.messageType()).willReturn(messageType);
        given(messageType.getNumber()).willReturn(ZapMessageType.WRITE_IO_POINT_NUMBER);
        
        ZapControlResponseStrategyFactory factory = new ZapControlResponseStrategyFactory(fieldTypeLibrary, eventPublisher, cache);
        ControlResponseStrategy result = factory.responseStrategyForControl(control, siteAddress);

        verifyNew(WriteIoPointsControlResponseStrategy.class).withArguments(control, siteAddress, eventPublisher, cache);
        verify(control).messageType();
        verify(messageType).getNumber();
        assertSame(strategy, result);
    }

    @Test
    public void shouldConstructDemandPollControlResponseStrategy() throws Exception {
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        DemandPollControl control = mock(DemandPollControl.class);
        SiteAddress siteAddress = mock(SiteAddress.class);
        DemandPollControlResponseStrategy strategy = mock(DemandPollControlResponseStrategy.class);
        FieldTypeLibrary fieldTypeLibrary = mock(FieldTypeLibrary.class);
        MessageType messageType = mock(MessageType.class);
        
        whenNew(DemandPollControlResponseStrategy.class)
            .withArguments(control, siteAddress, eventPublisher, cache)
            .thenReturn(strategy);
        given(control.messageType()).willReturn(messageType);
        given(messageType.getNumber()).willReturn(ZapMessageType.DEMAND_POLL_NUMBER);
        
        ZapControlResponseStrategyFactory factory = new ZapControlResponseStrategyFactory(fieldTypeLibrary, eventPublisher, cache);
        ControlResponseStrategy result = factory.responseStrategyForControl(control, siteAddress);

        verifyNew(DemandPollControlResponseStrategy.class).withArguments(control, siteAddress, eventPublisher, cache);
        verify(control).messageType();
        verify(messageType).getNumber();
        assertSame(strategy, result);
    }
    
    @Test
    public void shouldConstructConfigureResponseStrategy() throws Exception {
        InterestingSitesCache cache = mock(InterestingSitesCache.class);
        ConfigureControl control = mock(ConfigureControl.class);
        SiteAddress siteAddress = mock(SiteAddress.class);
        ConfigureResponseStrategy strategy = mock(ConfigureResponseStrategy.class);
        FieldTypeLibrary fieldTypeLibrary = mock(FieldTypeLibrary.class);
        MessageType messageType = mock(MessageType.class);
        
        whenNew(ConfigureResponseStrategy.class)
            .withArguments(control, fieldTypeLibrary, siteAddress, eventPublisher, cache)
            .thenReturn(strategy);
        given(control.messageType()).willReturn(messageType);
        given(messageType.getNumber()).willReturn(ZapMessageType.CONFIGURE_NUMBER);
        
        ZapControlResponseStrategyFactory factory = new ZapControlResponseStrategyFactory(fieldTypeLibrary, eventPublisher, cache);
        ControlResponseStrategy result = factory.responseStrategyForControl(control, siteAddress);
        
        verifyNew(ConfigureResponseStrategy.class).withArguments(control, fieldTypeLibrary, siteAddress, eventPublisher, cache);
        verify(control).messageType();
        verify(messageType).getNumber();
        assertSame(strategy, result);
    }
}
