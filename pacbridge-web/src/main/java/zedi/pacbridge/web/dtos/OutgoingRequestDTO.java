package zedi.pacbridge.web.dtos;

import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import zedi.pacbridge.app.controls.ControlRequest;
import zedi.pacbridge.app.controls.OutgoingRequest;
import zedi.pacbridge.app.controls.OutgoingRequestType;

@XmlRootElement(name = "OutgoingRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class OutgoingRequestDTO {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss z");

    @XmlElement(name="creationDate")
    private String creationDate;
    @XmlElement(name="address")
    private String address;
    @XmlElement(name="type")
    private String type;
    @XmlElement(name="id")
    private String id;
    @XmlElement(name="eventId")
    private Long eventId;
    @XmlElement(name="control")
    private String control;
    @XmlElement(name="status")
    private String status;

    public OutgoingRequestDTO() {
    }

    public OutgoingRequestDTO(OutgoingRequest outgoingRequest) {
        this.creationDate = dateFormat.format(outgoingRequest.getCreationTime());
        this.address = outgoingRequest.getSiteAddress().toString();
        this.type = outgoingRequest.getType().getName();
        this.id = outgoingRequest.getRequestId();
        this.eventId = outgoingRequest.getEventId();
        this.status = outgoingRequest.getStatus().getName();
        if (outgoingRequest.getType().equals(OutgoingRequestType.CONTROL))
            this.control = ((ControlRequest)outgoingRequest).getControl().messageType().getName();
        else
            this.control = "<Not Implemented Yet>";
    }
    
    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getControl() {
        return control;
    }

    public void setControl(String control) {
        this.control = control;
    }

    public static SimpleDateFormat getDateformat() {
        return dateFormat;
    }
}
