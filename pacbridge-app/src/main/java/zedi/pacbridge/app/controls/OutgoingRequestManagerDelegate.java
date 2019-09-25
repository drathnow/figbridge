package zedi.pacbridge.app.controls;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.net.Connection;
import zedi.pacbridge.app.net.OutgoingRequestSession;
import zedi.pacbridge.app.net.OutgoingRequestSessionListener;
import zedi.pacbridge.app.net.RequestProgressListener;
import zedi.pacbridge.app.net.SiteConnector;
import zedi.pacbridge.app.services.NetworkService;
import zedi.pacbridge.net.controls.ControlStatus;
import zedi.pacbridge.utl.SiteAddress;

class OutgoingRequestManagerDelegate {
    public static final String NETWORK_NUMBER_ERROR = "Network number {0} does not exist";
    private static final Logger logger = LoggerFactory.getLogger(OutgoingRequestManagerDelegate.class.getName());

    private SessionsManager sessionsManager;
    private RequestProgressListener requestProgressListener;
    private NetworkService networkService;
    private OutgoingRequestManager outgoingRequestManager;

    OutgoingRequestManagerDelegate(OutgoingRequestManager outgoingRequestManager, 
                                   SessionsManager sessionsManager, 
                                   NetworkService networkService, 
                                   RequestProgressListener requestProgressListener) {
        this.outgoingRequestManager = outgoingRequestManager;
        this.sessionsManager = sessionsManager;
        this.requestProgressListener = requestProgressListener;
        this.networkService = networkService;
    }
    
    void removeOutgoingRequestSession(OutgoingRequestSession session) {
        sessionsManager.removeOutgoingRequestSession(session);
    }
    
    void startOutgoingRequest(OutgoingRequest outgoingRequest) {
        SiteAddress siteAddress = outgoingRequest.getSiteAddress();
        if (networkService.isValidNetworkNumber(siteAddress.getNetworkNumber())) {
            if (canStartAnotherRequestForNetwork(siteAddress.getNetworkNumber()))
                handleRequestForSiteAddress(outgoingRequest, siteAddress);
        } else {
            String message = MessageFormat.format(NETWORK_NUMBER_ERROR, siteAddress.getNetworkNumber());
            logger.error("Unable to process request: " + message);
            requestProgressListener.requestProcessingAborted(outgoingRequest, ControlStatus.FAILURE, message, null);
        }
    }
    
    private boolean canStartAnotherRequestForNetwork(Integer networkNumber) {
        Integer maxSessionLimit = networkService.maxOutgoingSessionForNetworkNumber(networkNumber);
        return maxSessionLimit == 0 || (maxSessionLimit > sessionsManager.numberOfDevicesWithSessionsForNetworkNumber(networkNumber));
    }
    
    private void handleRequestForSiteAddress(OutgoingRequest outgoingRequest, SiteAddress siteAddress) {
        SiteConnector connector = networkService.siteConnectorForNetworkNumber(siteAddress.getNetworkNumber());
        Connection connection = connector.connectionForSiteAddress(siteAddress);
        if (connection != null) 
            startOrQueueRequest(outgoingRequest, siteAddress, connection);
        else
            logger.info("Device " + siteAddress.toString() + " is not online.  Request will be processed when device connects.");
    }

    private void startOrQueueRequest(OutgoingRequest outgoingRequest, SiteAddress siteAddress, Connection connection) {
        if (connection.getMaxSessionLimit() == 0 || connection.getMaxSessionLimit() > sessionsManager.numberOfSessionForSiteAddress(siteAddress)) {
            OutgoingRequestSession requestSession = connection.outgoingRequestSessionForOutgoingRequest(outgoingRequest);
            requestSession.setOutgoingRequestSessionListener(new OutgoingRequestSessionListener(){
                
                @Override
                public void sessionClosed(OutgoingRequestSession session) {
                    outgoingRequestManager.sessionClosed(session);
                }});
                
            sessionsManager.addOutgoingRequestSession(requestSession);
            requestSession.start();
        }
    }
}
