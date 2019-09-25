package zedi.pacbridge.app.controls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mock;

import zedi.pacbridge.net.controls.ControlStatus;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.DependencyResolver;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.messages.WriteIoPointsControl;

public class OutgoingRequestTest extends BaseTestCase {
    private static final Long EVENT_ID = 12L;
    private static final String ADDRESS_STRING = "123456/17";
    private static final String STATUS_MSG = "Hello World";
    @Mock
    private DependencyResolver dependencyResolver;
        
    @Test
    public void shouldToJsonString() throws Exception {
        SiteAddress siteAddress = new NuidSiteAddress("123456", 17);
        WriteIoPointsControl control = mock(WriteIoPointsControl.class);
        
        given(control.messageType()).willReturn(ZapMessageType.WriteIOPoints);
        
        OutgoingRequest request = new OutgoingRequest(siteAddress, EVENT_ID, OutgoingRequestType.CONTROL);
        request.incrementSendAttempts();
        request.setStatus(ControlStatus.RUNNING);
        request.setLastStatusMessage(STATUS_MSG);
        
        String jsonString = request.toJSONString();
        JSONObject jobj = new JSONObject(jsonString);
        
        assertEquals(ADDRESS_STRING, jobj.get("address"));
        assertNotNull(jobj.get("creationDate"));
        assertEquals(OutgoingRequestType.CONTROL.getName(), jobj.get("type"));
        assertEquals(EVENT_ID.intValue(), jobj.get("eventId"));
        assertEquals(request.getRequestId(), jobj.get("requestId"));
        assertEquals(ControlStatus.RUNNING.getName(), jobj.get("status"));
        assertEquals(1, jobj.get("sendAttempts"));
        assertNotNull(jobj.get("lastSendAttempt"));
        assertEquals(STATUS_MSG, jobj.get("lastStatusMessage"));

    }
}
