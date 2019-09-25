package zedi.pacbridge.gdn;

import java.io.Serializable;

public class PacEventStatus implements Serializable {

    public static final PacEventStatus Success = new PacEventStatus(0, "Success");
    public static final PacEventStatus InvalidParameter = new PacEventStatus(1, "Invalid Parameter");
    public static final PacEventStatus TooManyEvents = new PacEventStatus(2, "Maximum number of events exceeded");
    public static final PacEventStatus MemoryError = new PacEventStatus(3, "Memory error");
    public static final PacEventStatus ParsingError = new PacEventStatus(4, "Error parsing event");
    
    static final long serialVersionUID = 1001;
    
    private String message;
    private int statusNumber;
    
    private PacEventStatus(int statusNumber, String message) {
        this.message = message;
        this.statusNumber = statusNumber;
    }

    public int getStatusNumber() {
        return statusNumber;
    }
    
    public String getMessage() {
        return message;
    }
    
    public boolean isSuccess() {
        return Success.equals(this);
    }
    
    @Override
    public String toString() {
        return message;
    }
    
    public static PacEventStatus eventStatusForStatusNumber(int statusNumber) {
        switch (statusNumber) {
            case 0 :
                return Success;
                
            case 1 :
                return InvalidParameter;

            case 2 :
                return TooManyEvents;
            
            case 3 :
                return MemoryError;
                
            case 4 :
                return ParsingError;
                
            default : 
                return null;
        }
    }
}
