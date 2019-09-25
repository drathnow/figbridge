package zedi.figbridge.monitor;

import zedi.pacbridge.utl.NamedType;

public class Status extends NamedType {
    public static final Integer SEND_ERROR_NUMBER = 3;
    public static final Integer FATAL_ERROR_NUMBER = 99;

    public static final Status SUCCESS = new Status("Success", 0);
    public static final Status CONNECT_FAILURE = new Status("CONNECT_FAILED", 1);
    public static final Status AUTH_FAILURE = new Status("AUTH_FAILED", 2);
    public static final Status NO_MSG = new Status("NO_MSG_PUB", 4); 
    
    private Status(String name, Integer number) {
        super(name, number);
    }

    public String toString() {
        return getNumber().toString() + ':' + getName();
    }
    
    public static Status sendErrorWithMessage(String message) {
        return new Status(message, SEND_ERROR_NUMBER);
    }
    
    public static Status fatalErrorWithMessage(String message) {
        return new Status(message, FATAL_ERROR_NUMBER);
    }
}
