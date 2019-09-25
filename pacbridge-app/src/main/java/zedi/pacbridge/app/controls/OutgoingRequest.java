package zedi.pacbridge.app.controls;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.json.JSONObject;

import zedi.pacbridge.net.controls.ControlStatus;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;


@Indexed(index = "OutputRequestIndex")
public class OutgoingRequest implements Serializable {
    private static final long serialVersionUID = 1001L;
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");

    @Field
    private String requestId;
    @Field
    private Long eventId;
    @Field
    private String nuid;
    private Integer networkNumber;

    private Long creationTime;
    private boolean cancelled;

    private int sendAttempts;
    private Long lastSendAttempt;
    private String lastStatusMessage;
    private ControlStatus status;
    private OutgoingRequestType outgoingRequestType;

    public OutgoingRequest() {
    }

    protected OutgoingRequest(SiteAddress siteAddress, Long eventId, OutgoingRequestType type) {
        this.eventId = eventId;
        this.nuid = siteAddress.getAddress();
        this.networkNumber = siteAddress.getNetworkNumber();
        this.outgoingRequestType = type;
        this.creationTime = System.currentTimeMillis();
        this.requestId = UUID.randomUUID().toString();
        this.status = ControlStatus.PENDING;
        this.lastStatusMessage = "<none>";
    }

    public Integer getResponseTimeoutSeconds() {
        throw new UnsupportedOperationException("Method must be overridden by subclass");
    }
    
    public OutgoingRequestProcessor outgoingRequestProcessor() {
        throw new UnsupportedOperationException("Method must be overridden by subclass");
    }

    public boolean hasExpired(Long expirationMinutes) {
        return (System.currentTimeMillis() - creationTime) > TimeUnit.MINUTES.toMillis(expirationMinutes);
    }
    
    /**
     * Derived classes can override this to give a bridge description for display purposes. 
     * 
     * @return String a string giving a short description of this outgoing request.
     */
    public String shortDescription() {
        return "OutgoingRequest";
    }
    
    public void incrementSendAttempts() {
        sendAttempts++;
        lastSendAttempt = System.currentTimeMillis();
    }

    public Long getLastSendAttempt() {
        return lastSendAttempt;
    }
    
    public String getLastStatusMessage() {
        return lastStatusMessage;
    }
    
    public Integer getSendAttempts() {
        return sendAttempts;
    }
    
    public ControlStatus getStatus() {
        return status;
    }

    public void setStatus(ControlStatus status) {
        this.status = status;
    }

    public OutgoingRequestType getType() {
        return outgoingRequestType;
    }

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public String getRequestId() {
        return requestId;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public Long getEventId() {
        return eventId;
    }

    public SiteAddress getSiteAddress() {
        return new NuidSiteAddress(nuid, networkNumber);
    }

    public String getNiud() {
        return nuid;
    }
    
    public Integer getNetworkNumber() {
        return networkNumber;
    }
    
    public void setLastStatusMessage(String lastStatusMessage) {
        this.lastStatusMessage = lastStatusMessage;
    }
    
    @Override
    public String toString() {
        return toJSONString();
    }

    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("address", getSiteAddress().toString());
        jsonObject.put("creationDate", dateFormat.format(getCreationTime()));
        jsonObject.put("type", getType().getName());
        jsonObject.put("requestId", getRequestId());
        jsonObject.put("eventId", getEventId());
        jsonObject.put("sendAttempts", getSendAttempts());
        jsonObject.put("status", getStatus().getName());
        jsonObject.put("lastSendAttempt", lastSendAttempt == null ? "never" : dateFormat.format(getLastSendAttempt()));
        jsonObject.put("lastStatusMessage", getLastStatusMessage());
        return jsonObject.toString();
    }    
}