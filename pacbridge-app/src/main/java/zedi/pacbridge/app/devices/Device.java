package zedi.pacbridge.app.devices;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

@Indexed(index = "DeviceIndex")
public class Device implements Serializable {
    private static final long serialVersionUID = 1001L;
    
    @Field
    private String nuid;
    @Field
    private Date lastUpdateTime;
    private byte[] secretKey;
    private Integer networkNumber;
    private String firmwareVersion;
    
    public Device() {
    }

    public Device(String nuid, Integer networkNumber) {
        this(nuid, null, networkNumber, new Date());
    }
    
    public Device(String nuid) {
        this(nuid, null, 0, new Date());
    }
    
    public Device(String nuid, byte[] secretKey, Integer networkNumber, Date lastUpdateTime) {
        this(nuid, secretKey, lastUpdateTime, networkNumber, 0);
    }

    public Device(String nuid, byte[] secretKey, Date lastUpdateTime, Integer networkNumber, Integer firmwareVersion) {
        this.secretKey = secretKey;
        this.nuid = nuid;
        this.lastUpdateTime = lastUpdateTime;
        this.networkNumber = networkNumber;
    }

    public byte[] getSecretKey() {
        return secretKey;
    }

    public String getNuid() {
        return nuid;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    public Integer getNetworkNumber() {
        return networkNumber;
    }
    
    public String getFirmwareVersion() {
        return firmwareVersion;
    }
}
