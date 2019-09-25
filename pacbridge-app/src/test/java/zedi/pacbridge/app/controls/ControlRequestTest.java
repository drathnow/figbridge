package zedi.pacbridge.app.controls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.controls.zap.ZapControlResponseStrategyFactory;
import zedi.pacbridge.app.net.RequestProgressListener;
import zedi.pacbridge.app.services.NetworkService;
import zedi.pacbridge.app.util.LookupHelper;
import zedi.pacbridge.gdn.GdnFloat;
import zedi.pacbridge.gdn.messages.WriteIoPointControl;
import zedi.pacbridge.net.Control;
import zedi.pacbridge.net.controls.ControlStatus;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.DependencyResolver;
import zedi.pacbridge.utl.GlobalScheduledExecutor;
import zedi.pacbridge.utl.IpSiteAddress;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.PropertyBag;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.messages.WriteIoPointsControl;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ControlRequest.class)
public class ControlRequestTest extends BaseTestCase {
    private static final Long EVENT_ID = 12L;
    private static final Integer TIMEOUT_SECONDS = 90;
    private static final Integer NETWORK_NUMBER = 122;
    private static final String ADDRESS_STRING = "123456/17";
    private static final String STATUS_MSG = "Hello World";

    @Mock
    private DependencyResolver dependencyResolver;
    @Mock
    private SiteAddress siteAddress;
    @Mock
    private GlobalScheduledExecutor scheduledExecutor;
    @Mock
    public ZapControlResponseStrategyFactory responseStrategyFactory;
    
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        DependencyResolver.Implementation.setImplementation(dependencyResolver);
    }
    
    @Override
    @After
    public void tearDown() throws Exception {
        DependencyResolver.Implementation.setImplementation(null);
        super.tearDown();
    }
    
    @Test
    public void shouldReturnResponseTimeoutFromNetworkPropertyBag() throws Exception {
        WriteIoPointsControl control = mock(WriteIoPointsControl.class);
        NetworkService networkService = mock(NetworkService.class);
        PropertyBag propertyBag = mock(PropertyBag.class);
        
        given(siteAddress.getNetworkNumber()).willReturn(NETWORK_NUMBER);
        given(dependencyResolver.getImplementationOf(NetworkService.JNDI_NAME)).willReturn(networkService);
        given(networkService.propertyBagForNetworkNumber(NETWORK_NUMBER)).willReturn(propertyBag);
        given(propertyBag.integerValueForProperty(ControlRequest.CONTROL_RESPONSE_TIMEOUT_SECONDS_PROPERTY_NAME, ControlRequest.DEFAULT_CONTROL_RESPONSE_TIMEOUT_SECONDS))
            .willReturn(TIMEOUT_SECONDS);

        ControlRequest controlRequest = new ControlRequest(siteAddress, EVENT_ID, control);
        
        assertEquals(TIMEOUT_SECONDS, controlRequest.getResponseTimeoutSeconds());
    }
    
    @Test
    public void shouldToJsonString() throws Exception {
        SiteAddress siteAddress = new NuidSiteAddress("123456", 17);
        WriteIoPointsControl control = mock(WriteIoPointsControl.class);
        
        given(control.messageType()).willReturn(ZapMessageType.WriteIOPoints);
        
        ControlRequest controlRequest = new ControlRequest(siteAddress, EVENT_ID, control);
        controlRequest.incrementSendAttempts();
        controlRequest.setStatus(ControlStatus.RUNNING);
        controlRequest.setLastStatusMessage(STATUS_MSG);

        String jsonString = controlRequest.toJSONString();
        JSONObject jobj = new JSONObject(jsonString);
        assertEquals(ADDRESS_STRING, jobj.get("address"));
        assertNotNull(jobj.get("creationDate"));
        assertEquals(OutgoingRequestType.CONTROL.getName(), jobj.get("type"));
        assertEquals(EVENT_ID.intValue(), jobj.get("eventId"));
        assertEquals(controlRequest.getRequestId(), jobj.get("requestId"));
        assertEquals(ControlStatus.RUNNING.getName(), jobj.get("status"));
        assertEquals(1, jobj.get("sendAttempts"));
        assertNotNull(jobj.get("lastSendAttempt"));
        assertEquals(STATUS_MSG, jobj.get("lastStatusMessage"));
        assertEquals(ZapMessageType.WriteIOPoints.getName(), jobj.get("control"));
    }
    
    @Test
    public void shouldReturnControlRequestProcessor() throws Exception {
        LookupHelper lookupHelper = mock(LookupHelper.class);
        Control control = mock(Control.class);
        PropertyBag propertyBag = mock(PropertyBag.class);
        NetworkService networkService = mock(NetworkService.class);
        RequestProgressListener listener = mock(RequestProgressListener.class);
        ControlRequestProcessor controlRequestProcessor = mock(ControlRequestProcessor.class);
        NotificationCenter notificationCenter = mock(NotificationCenter.class);
        
        given(control.messageType()).willReturn(ZapMessageType.WriteIOPoints);
        given(siteAddress.getNetworkNumber()).willReturn(NETWORK_NUMBER);
        given(dependencyResolver.getImplementationOf(LookupHelper.JNDI_NAME)).willReturn(lookupHelper);
        given(lookupHelper.getRequestProgressListener()).willReturn(listener);
        given(lookupHelper.getNetworkService()).willReturn(networkService);
        given(lookupHelper.getNotificationCenter()).willReturn(notificationCenter);
        given(lookupHelper.getControlResponseStrategyFactory()).willReturn(responseStrategyFactory);
        given(networkService.propertyBagForNetworkNumber(NETWORK_NUMBER)).willReturn(propertyBag);
        
        ControlRequest controlRequest = new ControlRequest(siteAddress, EVENT_ID, control);

        whenNew(ControlRequestProcessor.class)
            .withArguments(controlRequest, responseStrategyFactory, listener, notificationCenter)
            .thenReturn(controlRequestProcessor);
        
        OutgoingRequestProcessor result = controlRequest.outgoingRequestProcessor();
        
        verifyNew(ControlRequestProcessor.class).withArguments(controlRequest, responseStrategyFactory, listener, notificationCenter);
        verify(dependencyResolver).getImplementationOf(LookupHelper.JNDI_NAME);
        verify(lookupHelper).getRequestProgressListener();
        verify(lookupHelper).getNotificationCenter();
        assertSame(controlRequestProcessor, result);
    }
    
    @Test
    public void shouldSerializeAndDeserialize() throws Exception {
        SiteAddress siteAddress = new IpSiteAddress("1.2.3.4", 12);
        WriteIoPointControl control1 = new WriteIoPointControl(1, new GdnFloat(1.2F));
        
        ControlRequest request = new ControlRequest(siteAddress, EVENT_ID, control1);
        assertSame(control1, request.getControl());
        
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
        objectOutputStream.writeObject(request);
        
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(arrayOutputStream.toByteArray()));
        
        request = (ControlRequest)inputStream.readObject();
        assertTrue(request.getControl() instanceof WriteIoPointControl);
    }
}
