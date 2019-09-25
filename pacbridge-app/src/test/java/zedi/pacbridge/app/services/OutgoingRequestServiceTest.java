package zedi.pacbridge.app.services;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.app.controls.OutgoingRequestCache;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.SiteAddress;

public class OutgoingRequestServiceTest extends BaseTestCase {

    @Mock
    private OutgoingRequestCache outgoingRequestCache;
    
    @Test
    public void shouldLookupRequestForSite() throws Exception {
        SiteAddress siteAddress = mock(SiteAddress.class);
        given(outgoingRequestCache.hasOutgoingRequests(siteAddress)).willReturn(true);
        OutgoingRequestService service = new OutgoingRequestService(outgoingRequestCache);
        assertTrue(service.hasOutgoingRequestsForSiteAddress(siteAddress));
        verify(outgoingRequestCache).hasOutgoingRequests(siteAddress);
    }
}
