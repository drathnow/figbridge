package zedi.pacbridge.zap.messages;

import zedi.pacbridge.utl.NamedType;


public class ProtocolErrorType extends NamedType {

    private static final int INVALID_SESSION_ID_NUMBER = 1;
    private static final int NO_HANDLER_ID_NUMBER = 2;
    private static final int INVALID_MESSAGE_PASSED_TO_HANDLER = 3;
    private static final int INVALID_MESSAGE_NUMBER = 4;
    private static final int MESSAGE_PROCESSING_ERROR_NUMBER = 5;

    public static final ProtocolErrorType InvalidSessionId = new ProtocolErrorType("Invalid Session ID", INVALID_SESSION_ID_NUMBER);
    public static final ProtocolErrorType NoMessageHandler = new ProtocolErrorType("No Message Handler", NO_HANDLER_ID_NUMBER);
    public static final ProtocolErrorType InvalidMessagePassedToHandler = new ProtocolErrorType("Invalid Message Passed To Handler", INVALID_MESSAGE_PASSED_TO_HANDLER);
    public static final ProtocolErrorType InvalidMessageNumber = new ProtocolErrorType("Invalid/Unknown Message Number", INVALID_MESSAGE_NUMBER);
    public static final ProtocolErrorType MessageProcessingError = new ProtocolErrorType("Error while processing message", MESSAGE_PROCESSING_ERROR_NUMBER);

    private ProtocolErrorType(String name, Integer number) {
        super(name, number);
    }

    public static final ProtocolErrorType protocolErrorForNumber(Integer number) {
        switch (number) {
            case INVALID_SESSION_ID_NUMBER :
                return InvalidSessionId;
            case NO_HANDLER_ID_NUMBER: 
                return NoMessageHandler;
            case INVALID_MESSAGE_PASSED_TO_HANDLER :
                return InvalidMessagePassedToHandler;
            case INVALID_MESSAGE_NUMBER :
                return InvalidMessageNumber;
            case MESSAGE_PROCESSING_ERROR_NUMBER :
                return MessageProcessingError;
        }
        throw new IllegalArgumentException("Unknown Protocol Error number: " + number);
    }
}
