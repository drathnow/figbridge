package zedi.pacbridge.web.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Connection")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConnectionDTO {

    @XmlElement(name="id")
    private String id;
    @XmlElement(name="nuid")
    private String nuid;
    @XmlElement(name="netNo")
    private Integer netNo;
    @XmlElement(name="bytesTrx")
    private Integer bytesTrx;
    @XmlElement(name="bytesRcv")
    private Integer bytesRcv;
    @XmlElement(name="lastActivity")
    private String lastActivity;
    @XmlElement(name="ipAddress")
    private String ipAddress;
    @XmlElement(name="ver")
    private String firmwareVersion;
    
    
    public ConnectionDTO() {
    }
    
    public ConnectionDTO(String nuid, Integer networkNumber, Integer bytesSent, Integer bytesReceived, String lastActivityTime, String ipAddress, String firmwareVersion) {
        this.id = nuid + ':' + networkNumber.toString();
        this.nuid = nuid;
        this.netNo = networkNumber;
        this.bytesTrx = bytesSent;
        this.bytesRcv = bytesReceived;
        this.lastActivity = lastActivityTime;
        this.ipAddress = ipAddress;
        this.firmwareVersion = firmwareVersion;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public String getNuid() {
        return nuid;
    }

    public void setNuid(String nuid) {
        this.nuid = nuid;
    }

    public Integer getNetNo() {
        return netNo;
    }

    public void setNetNo(Integer netNo) {
        this.netNo = netNo;
    }

    public Integer getBytesTrx() {
        return bytesTrx;
    }

    public void setBytesTrx(Integer bytesTrx) {
        this.bytesTrx = bytesTrx;
    }

    public Integer getBytesRcv() {
        return bytesRcv;
    }

    public void setBytesRcv(Integer bytesRcv) {
        this.bytesRcv = bytesRcv;
    }

    public String getLastActivity() {
        return lastActivity;
    }
    
    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }
    
    public String getFirmwareVersion() {
        return firmwareVersion;
    }
}
