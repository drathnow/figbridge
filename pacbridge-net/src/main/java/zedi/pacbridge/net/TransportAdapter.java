package zedi.pacbridge.net;

import zedi.pacbridge.net.annotations.TransportAdapterClosed;
import zedi.pacbridge.net.annotations.TransportAdapterConnected;
import zedi.pacbridge.net.annotations.TransportAdapterReceivedData;
import zedi.pacbridge.net.annotations.TransportAdapterWritingData;
import zedi.pacbridge.utl.SiteAddress;



public interface TransportAdapter extends DataTransmitter, ActivityTrackable {
    public void setSiteAddress(SiteAddress siteAddress);
    public void setDataReceiver(DataReceiver dataReceiver);
    
    /**
     * Registers an object as an event listener for this <code>TransportAdapter</code>.  Methods of the object
     * can be designated as event handlers by annotating them with one of the following annotations:
     * 
     * <ul>
     *      <li>{@link TransportAdapterClosed}</li>
     *      <li>{@link TransportAdapterConnected}</li>
     *      <li>{@link TransportAdapterReceivedData}</li>
     *      <li>{@link TransportAdapterWritingData}</li>
     * </ul>
     * @param eventListener
     */
    public void setEventListener(Object eventListener);
}