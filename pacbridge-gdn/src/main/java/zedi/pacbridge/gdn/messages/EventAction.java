package zedi.pacbridge.gdn.messages;

import java.io.Serializable;

public class EventAction implements Serializable {
    private static final long serialVersionUID = 1001;

    public static final EventAction Delete = new EventAction(0, "delete");
    public static final EventAction Poll = new EventAction(1, "poll");
    public static final EventAction Report = new EventAction(2, "report");
    public static final EventAction Reboot = new EventAction(4, "reboot");
    
    private String name;
    private Integer actionNumber;

    private EventAction(int actionNumber, String name) {
        this.name = name;
        this.actionNumber = actionNumber;
    }

    public String getName() {
        return name;
    }

    public Integer getActionNumber() {
        return actionNumber;
    }

    public String toString() {
        return name + "(" + actionNumber + ")";
    }

    public static final EventAction eventActionForActionNumber(int eventNumber) {
        switch (eventNumber) {
            case 0 :
                return Delete;
            case 1 :
                return Poll;
            case 2 :
                return Report;
            case 4 :
                return Reboot;
            default :
                return null;
        }
    }

    public static EventAction eventActionForActionName(String keyName) {
        if (Delete.name.equals(keyName))
            return Delete;
        if (Poll.name.equals(keyName))
            return Poll;
        if (Report.name.equals(keyName))
            return Report;
        if (Reboot.name.equals(keyName))
            return Reboot;
        return null;
    }
}
