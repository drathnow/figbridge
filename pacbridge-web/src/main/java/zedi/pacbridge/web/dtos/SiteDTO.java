package zedi.pacbridge.web.dtos;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "Device")
@XmlAccessorType(XmlAccessType.FIELD)
public class SiteDTO {
    @XmlElement
    private String id;
    @XmlElement
    private Integer conCnt;
    @XmlElement
    private Integer rptCnt;
    @XmlElement
    private Long rdngCnt;
    @XmlElement
    private Integer dupRptsCnt;
    @XmlElement
    private Map<String, Integer> controls;

    public SiteDTO() {
    }

    public SiteDTO(String nuid) {
        super();
        this.id = nuid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getConCnt() {
        return conCnt;
    }

    public void setConCnt(Integer conCnt) {
        this.conCnt = conCnt;
    }

    public Integer getRptCnt() {
        return rptCnt;
    }

    public void setRptCnt(Integer rptCnt) {
        this.rptCnt = rptCnt;
    }

    public Long getRdngCnt() {
        return rdngCnt;
    }

    public void setRdngCnt(Long rdngCnt) {
        this.rdngCnt = rdngCnt;
    }

    public Integer getDupRptsCnt() {
        return dupRptsCnt;
    }

    public void setDupRptsCnt(Integer dupRptsCnt) {
        this.dupRptsCnt = dupRptsCnt;
    }

    public Map<String, Integer> getControls() {
        return controls;
    }

    public void setControls(Map<String, Integer> controls) {
        this.controls = controls;
    }
}
