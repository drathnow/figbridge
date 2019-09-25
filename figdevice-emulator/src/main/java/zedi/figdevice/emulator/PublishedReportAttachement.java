package zedi.figdevice.emulator;

public class PublishedReportAttachement {
    private String username;
    private Long eventId;
    
    public PublishedReportAttachement(String username, Long eventId) {
        super();
        this.username = username;
        this.eventId = eventId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public Long getEventId() {
        return eventId;
    }
}
