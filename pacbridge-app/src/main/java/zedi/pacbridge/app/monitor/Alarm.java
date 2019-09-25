package zedi.pacbridge.app.monitor;

public abstract class Alarm {

    private String description;
    private String messageText;
    
    public Alarm(String description, String messageText) {
        this.description = description;
        this.messageText = messageText;
    }
    
    public String getDescription() {
        return description;
    }
 
    public String getMessageText() {
        return messageText;
    }
}
