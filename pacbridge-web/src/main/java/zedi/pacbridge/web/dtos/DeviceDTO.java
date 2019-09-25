package zedi.pacbridge.web.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Device")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceDTO {
    @XmlElement
    private String id;
    @XmlElement
    private String secretKey;
    @XmlElement(name="ver")
    private String firmwareVersion;
    @XmlElement
    private Integer networkNumber;              

    public DeviceDTO() {
    }
    
    public DeviceDTO(String nuid, String firmwareVersion) {
        super();
        this.id = nuid;
        this.firmwareVersion = firmwareVersion;
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public Integer getNetworkNumber() {
        return networkNumber;
    }

    public void setNetworkNumber(Integer networkNumber) {
        this.networkNumber = networkNumber;
    }
    
    
}
