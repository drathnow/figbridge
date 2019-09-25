package zedi.pacbridge.app.auth.zap;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import zedi.pacbridge.app.controls.OutgoingRequestCache;
import zedi.pacbridge.app.devices.Device;
import zedi.pacbridge.app.devices.DeviceCache;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SiteAddress;

public class ZapAuthenticationDelegateTest extends BaseTestCase {
        
    private static final String NUID = "123";

    @Test
    public void shouldReturnDeviceForNuid() throws Exception {
        Device device = mock(Device.class);
        DeviceCache cache = mock(DeviceCache.class);
        OutgoingRequestCache requestCache = mock(OutgoingRequestCache.class);
        
        given(cache.deviceForNetworkUnitId(NUID)).willReturn(device);
        
        ZapAuthenticationDelegate delegate = new ZapAuthenticationDelegate(requestCache, cache);
        
        assertSame(device, delegate.deviceForNuid(NUID));
        verify(cache).deviceForNetworkUnitId(NUID);
    }
    
    @Test
    public void shouldCheckOutgoingRequestCacheAndReturnTrueIfRequestsArePending() throws Exception {
        DeviceCache cache = mock(DeviceCache.class);
        SiteAddress siteAddress = mock(SiteAddress.class);
        OutgoingRequestCache requestCache = mock(OutgoingRequestCache.class);
        
        given(requestCache.hasOutgoingRequests(siteAddress)).willReturn(true);
        ZapAuthenticationDelegate delegate = new ZapAuthenticationDelegate(requestCache, cache);
        
        assertTrue(delegate.hasOutgoingDataRequests(siteAddress));
        verify(requestCache).hasOutgoingRequests(siteAddress);
    }
}
