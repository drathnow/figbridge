package zedi.pacbridge.stp.fad;

public class FadMessageStatus {
    public static final FadMessageStatus QUEUED = new FadMessageStatus("Queued");
    public static final FadMessageStatus INTRANSIT = new FadMessageStatus("In-Transit");
    public static final FadMessageStatus FAILED = new FadMessageStatus("Failed");
    public static final FadMessageStatus ACKNOWLEDGED = new FadMessageStatus("Acknowledged");

    private String name;
    
    private FadMessageStatus(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
