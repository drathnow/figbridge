package zedi.pacbridge.utl;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Notification implements Serializable {
    private static final long serialVersionUID = 1001;
    
    private String originatingHostname;
    private String name;
    private Long creationTime;
    private Object attachment;
    
    public Notification(String name) {
        this.name = name;
        this.creationTime = System.currentTimeMillis();
        try {
            this.originatingHostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            this.originatingHostname = "Unknown";
        }
    }
    
    public Notification(String name, Object attachment) {
        this(name);
        this.attachment = attachment;
    }
    
    public Long getCreationTime() {
        return creationTime;
    }
    
    public String getOriginatingHostname() {
        return originatingHostname;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getAttachment() {
        return (T)attachment;
    }
    
    public String getName() {
        return name;
    }
}
