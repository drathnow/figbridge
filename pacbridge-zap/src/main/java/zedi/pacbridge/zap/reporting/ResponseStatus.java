package zedi.pacbridge.zap.reporting;

import zedi.pacbridge.utl.NamedType;

public class ResponseStatus extends NamedType {

    private static final int OK_NUMBER = 1;
    private static final int PERM_ERROR_NUMBER = 2;
    private static final int TRANS_ERROR_NUMBER = 3;
    
    public static final ResponseStatus OK = new ResponseStatus("OK", OK_NUMBER);
    public static final ResponseStatus PermanentError = new ResponseStatus("Permanent Error", PERM_ERROR_NUMBER);
    public static final ResponseStatus TransientError = new ResponseStatus("Transient Error", TRANS_ERROR_NUMBER);

    private ResponseStatus(String name, Integer number) {
        super(name, number);
    }
    
    public static ResponseStatus reportStatusForNumber(Integer number) {
        switch (number) {
            case OK_NUMBER : return OK;
            case PERM_ERROR_NUMBER : return PermanentError;
            case TRANS_ERROR_NUMBER : return TransientError;
        }
        throw new IllegalArgumentException("Uknown report status number: " + number);
    }
}
