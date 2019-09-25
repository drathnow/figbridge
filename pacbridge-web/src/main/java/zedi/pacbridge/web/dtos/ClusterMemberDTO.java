package zedi.pacbridge.web.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ClusterMember")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClusterMemberDTO {
    @XmlElement(name="id")
    private String id;
   
    public ClusterMemberDTO(String id) {
        this.id = id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
}
