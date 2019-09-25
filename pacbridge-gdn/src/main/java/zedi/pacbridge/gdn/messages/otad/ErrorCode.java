package zedi.pacbridge.gdn.messages.otad;

public class ErrorCode {

    private static final int ERROR_CODE_NO_ERROR = 0;
    private static final int ERROR_CODE_INVALID_COMMAND_TYPE = 1;
    private static final int ERROR_CODE_INVALID_COMMAND_LENGTH = 2;
    private static final int ERROR_CODE_IMAGE_INCOMPLETE = 3;
    private static final int ERROR_CODE_UNEXPECTED_CODE_BLOCK = 5;
    private static final int ERROR_INVALID_CODEMAP_ID = 6;

    public static final ErrorCode NoError = new ErrorCode("No error", ERROR_CODE_NO_ERROR);
    public static final ErrorCode InvalidCommandType = new ErrorCode("Invalid command type", ERROR_CODE_INVALID_COMMAND_TYPE);
    public static final ErrorCode InvalidCommandLength = new ErrorCode("Invalid command length", ERROR_CODE_INVALID_COMMAND_LENGTH);
    public static final ErrorCode IncompleteImage = new ErrorCode("Incomplete image", ERROR_CODE_IMAGE_INCOMPLETE);
    public static final ErrorCode UnexpectedCodeBlock = new ErrorCode("Unexpected code block", ERROR_CODE_UNEXPECTED_CODE_BLOCK);
    public static final ErrorCode InvalidCodeMap = new ErrorCode("Invalid code map", ERROR_INVALID_CODEMAP_ID);

    private String name;
    private Integer number;

    private ErrorCode(String name, Integer number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public Integer getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return name + '(' + number + ')';
    }

    public static final ErrorCode errorCodeForNumber(Integer number) {
        switch (number) {
            case ERROR_CODE_NO_ERROR :
                return NoError;
            case ERROR_CODE_INVALID_COMMAND_TYPE :
                return InvalidCommandType;
            case ERROR_CODE_INVALID_COMMAND_LENGTH :
                return InvalidCommandLength;
            case ERROR_CODE_IMAGE_INCOMPLETE :
                return IncompleteImage;
            case ERROR_CODE_UNEXPECTED_CODE_BLOCK :
                return UnexpectedCodeBlock;
            case ERROR_INVALID_CODEMAP_ID :
                return InvalidCodeMap;
        }
        return new ErrorCode("Unknown error", number);
    }
}
