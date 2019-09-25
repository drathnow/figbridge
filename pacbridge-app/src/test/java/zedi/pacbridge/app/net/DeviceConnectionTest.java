package zedi.pacbridge.app.net;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import zedi.pacbridge.app.controls.OutgoingRequest;
import zedi.pacbridge.net.ProtocolStack;
import zedi.pacbridge.net.annotations.ConnectionIdentityChanged;
import zedi.pacbridge.net.auth.AuthenticationContext;
import zedi.pacbridge.net.auth.AuthenticationListener;
import zedi.pacbridge.test.BaseTestCase;
import zedi.pacbridge.utl.FutureTimer;
import zedi.pacbridge.utl.GlobalScheduledExecutor;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.ThreadContext;
import zedi.pacbridge.utl.ThreadContextHandler;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DeviceConnection.class, ThreadContextHandler.class})
public class DeviceConnectionTest extends BaseTestCase {
    private static final String ADDRESS = "1.2.3.4";
    
    @Mock
    private ProtocolStack protocolStack;
    @Mock
    private Object connectionListener;
    @Mock
    private SiteAddress siteAddress;
    @Mock
    private GlobalScheduledExecutor scheduledExecutor;
    
    private class MyListener {
        public boolean identityChangedCalled = false;
        @ConnectionIdentityChanged
        public void identityChanged(Connection connection) {
            identityChangedCalled = true;
        }
    }
    
    @Test
    public void shouldPostIdentityChangedEventToAllListenersWhenAuthenticationSucceeds() throws Exception {
        MyListener listener1 = new MyListener();
        MyListener listener2 = new MyListener();
        ThreadContext threadContext = mock(ThreadContext.class);
        FutureTimer future = mock(FutureTimer.class);
        AuthenticationContext authenticationContext = mock(AuthenticationContext.class);

        given(threadContext.requestTrap(any(ThreadContextHandler.class), eq((long)Connection.DEFAULT_AUTHENTICATION_TIMEOUT_SECONDS), eq(TimeUnit.SECONDS))).willReturn(future);
        given(threadContext.isCurrentContext()).willReturn(true);
        given(authenticationContext.getSiteAddress()).willReturn(siteAddress);
        given(siteAddress.getAddress()).willReturn(ADDRESS);
        ArgumentCaptor<AuthenticationListener> listenerArg = ArgumentCaptor.forClass(AuthenticationListener.class);
        
        DeviceConnection connection = new DeviceConnection(siteAddress, threadContext, protocolStack, scheduledExecutor);
        connection.addConnectionListener(listener1);
        connection.addConnectionListener(listener2);
        
        verify(protocolStack).setAuthenticationListener(listenerArg.capture());
        AuthenticationListener listener = listenerArg.getValue();
        listener.authenticationStarted();
        listener.deviceAuthenticated(authenticationContext);
        assertTrue(listener1.identityChangedCalled);
        assertTrue(listener1.identityChangedCalled);
    }
    
    @Test
    public void shouldShouldCancelFutureTimerWhenAuthenticationFails() throws Exception {
        ThreadContext threadContext = mock(ThreadContext.class);
        FutureTimer future = mock(FutureTimer.class);
        AuthenticationContext authenticationContext = mock(AuthenticationContext.class);

        given(threadContext.requestTrap(any(ThreadContextHandler.class), eq((long)Connection.DEFAULT_AUTHENTICATION_TIMEOUT_SECONDS), eq(TimeUnit.SECONDS))).willReturn(future);
        given(threadContext.isCurrentContext()).willReturn(true);
        given(authenticationContext.getSiteAddress()).willReturn(siteAddress);
        given(siteAddress.getAddress()).willReturn(ADDRESS);
        ArgumentCaptor<AuthenticationListener> listenerArg = ArgumentCaptor.forClass(AuthenticationListener.class);
        
        new DeviceConnection(siteAddress, threadContext, protocolStack, scheduledExecutor);
        
        verify(protocolStack).setAuthenticationListener(listenerArg.capture());
        AuthenticationListener listener = listenerArg.getValue();
        listener.authenticationStarted();
        listener.authenticationFailed();
        verify(future).cancel();
    }
    
    @Test
    public void shouldShouldCancelFutureTimerWhenAuthenticationSucceeds() throws Exception {
        ThreadContext threadContext = mock(ThreadContext.class);
        FutureTimer future = mock(FutureTimer.class);
        AuthenticationContext authenticationContext = mock(AuthenticationContext.class);

        given(threadContext.requestTrap(any(ThreadContextHandler.class), eq((long)Connection.DEFAULT_AUTHENTICATION_TIMEOUT_SECONDS), eq(TimeUnit.SECONDS))).willReturn(future);
        given(threadContext.isCurrentContext()).willReturn(true);
        given(authenticationContext.getSiteAddress()).willReturn(siteAddress);
        given(siteAddress.getAddress()).willReturn(ADDRESS);
        ArgumentCaptor<AuthenticationListener> listenerArg = ArgumentCaptor.forClass(AuthenticationListener.class);
        
        new DeviceConnection(siteAddress, threadContext, protocolStack, scheduledExecutor);
        
        verify(protocolStack).setAuthenticationListener(listenerArg.capture());
        AuthenticationListener listener = listenerArg.getValue();
        listener.authenticationStarted();
        listener.deviceAuthenticated(authenticationContext);
        verify(future).cancel();
    }
    
    @Test
    public void shouldCloseConnectionWhenFutureTimerExpires() throws Exception {
        ThreadContext threadContext = mock(ThreadContext.class);
        FutureTimer future = mock(FutureTimer.class);

        given(threadContext.requestTrap(any(ThreadContextHandler.class), eq((long)Connection.DEFAULT_AUTHENTICATION_TIMEOUT_SECONDS), eq(TimeUnit.SECONDS))).willReturn(future);
        given(threadContext.isCurrentContext()).willReturn(true);
        ArgumentCaptor<AuthenticationListener> listenerArg = ArgumentCaptor.forClass(AuthenticationListener.class);
        ArgumentCaptor<ThreadContextHandler> handlerArg = ArgumentCaptor.forClass(ThreadContextHandler.class);
        
        new DeviceConnection(siteAddress, threadContext, protocolStack, scheduledExecutor);
        
        verify(protocolStack).setAuthenticationListener(listenerArg.capture());
        AuthenticationListener listener = listenerArg.getValue();
        listener.authenticationStarted();
        
        verify(threadContext).requestTrap(handlerArg.capture(), eq((long)Connection.DEFAULT_AUTHENTICATION_TIMEOUT_SECONDS), eq(TimeUnit.SECONDS));
        handlerArg.getValue().handleSyncTrap();
        verify(protocolStack).close();
    }

    @Test
    public void shouldStartAuthenticationTimerWhenFirstContructed() throws Exception {
        ThreadContext astRequester = mock(ThreadContext.class);
        FutureTimer future = mock(FutureTimer.class);

        given(astRequester.requestTrap(any(ThreadContextHandler.class), eq((long)Connection.DEFAULT_AUTHENTICATION_TIMEOUT_SECONDS), eq(TimeUnit.SECONDS))).willReturn(future);
        ArgumentCaptor<AuthenticationListener> arg = ArgumentCaptor.forClass(AuthenticationListener.class);
        
        new DeviceConnection(siteAddress, astRequester, protocolStack, scheduledExecutor);
        
        verify(protocolStack).setAuthenticationListener(arg.capture());
        AuthenticationListener listener = arg.getValue();
        listener.authenticationStarted();
        
        verify(astRequester).requestTrap(any(ThreadContextHandler.class), eq((long)Connection.DEFAULT_AUTHENTICATION_TIMEOUT_SECONDS), eq(TimeUnit.SECONDS));
    }

    @Test
    public void shouldBuildOutgoingRequestSession() throws Exception {
        ThreadContext astRequester = mock(ThreadContext.class);
        OutgoingRequestSession outgoingRequestSession = mock(OutgoingRequestSession.class);
        OutgoingRequest request = mock(OutgoingRequest.class);

        DeviceConnection connection = new DeviceConnection(siteAddress, astRequester, protocolStack, scheduledExecutor);
        whenNew(OutgoingRequestSession.class)
            .withArguments(request, connection, astRequester)
            .thenReturn(outgoingRequestSession);

        OutgoingRequestSession result = connection.outgoingRequestSessionForOutgoingRequest(request);
        verifyNew(OutgoingRequestSession.class).withArguments(request, connection, astRequester);
        assertSame(outgoingRequestSession, result);
    }

    @Test
    public void shouldDispatchToThreadContextWhenClosedFromDifferentThread() throws Exception {
        ThreadContext threadContext = mock(ThreadContext.class);
        ArgumentCaptor<ThreadContextHandler> arg = ArgumentCaptor.forClass(ThreadContextHandler.class); 
        
        given(threadContext.isCurrentContext()).willReturn(false);
        
        DeviceConnection connection = new DeviceConnection(siteAddress, threadContext, protocolStack, scheduledExecutor);
        
        connection.close();

        verify(threadContext).requestTrap(arg.capture());

        ThreadContextHandler handler = arg.getValue();
        handler.handleSyncTrap();
        
        verify(protocolStack).close();
    }
    
    @Test
    public void shouldCloseWithNoOutgoingRequestSessionAndSameThread() throws Exception {
        ThreadContext threadContext = mock(ThreadContext.class);
        
        given(threadContext.isCurrentContext()).willReturn(true);
        DeviceConnection connection = new DeviceConnection(siteAddress, threadContext, protocolStack, scheduledExecutor);
        
        connection.close();
        verify(protocolStack).close();
    }
    
    @Test
    public void shouldCloseWithOutgoingRequestSession() throws Exception {
        ThreadContext astRequester = mock(ThreadContext.class);
        OutgoingRequestSession outgoingRequestSession = mock(OutgoingRequestSession.class);
        OutgoingRequest request = mock(OutgoingRequest.class);

        DeviceConnection connection = new DeviceConnection(siteAddress, astRequester, protocolStack, scheduledExecutor);
        whenNew(OutgoingRequestSession.class)
            .withArguments(request, connection, astRequester)
            .thenReturn(outgoingRequestSession);

        connection.outgoingRequestSessionForOutgoingRequest(request);
        
        connection.close();
    }
    
    
}
