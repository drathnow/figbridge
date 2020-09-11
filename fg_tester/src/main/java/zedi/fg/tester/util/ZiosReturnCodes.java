package zedi.fg.tester.util;

public class ZiosReturnCodes
{
    public static String stringForErrorCode(Number errorCode)
    {
        switch (errorCode.intValue())
        {
            case 0:
                return "SUCCESS";
            case 1:
                return "UNDEFINED_ERROR";
            case 2:
                return "DUPLICATE_NAME";
            case 3:
                return "DUPLICATE_ID";
            case 4:
                return "NOT_FOUND";
            case 5:
                return "NOT_EMPTY";
            case 6:
                return "INVALID_PARAMETER";
            case 7:
                return "TIMEOUT";
            case 8:
                return "UNSUPPORTED_OPERATION";
            case 9:
                return "INVALID_NAME";
            case 46:
                return "MISSING_KEY";
            case 47:
                return "DUPLICATE_KEY";
            case 48:
                return "INVALID_KEY";
            case 49:
                return "INVALID_VALUE";
            case 50:
                return "DUPLICATE_SITE";
            case 51:
                return "SITE_NOT_FOUND";
            case 52:
                return "MISSMATCHED_SITE";
            case 100:
                return "DUPLICATE_EXT_DEV";
            case 101:
                return "EXT_DEV_NOT_FOUND";
            case 200:
                return "DUPLICATE_IOPOINT";
            case 201:
                return "IOPOINT_NOT_FOUND";
            case 202:
                return "INVALID_POLLSET";
            case 203:
                return "SYSTEM_POINT";
            case 204:
                return "NOT_TIME_YET";
            case 205:
                return "INVALID_ALARM_SETTING";
            case 206:
                return "INVALID_SOURCE_ADDRESS";
            case 207:
                return "INVALID_EXT_ID";
            case 208:
                return "IOPOINT_UPDATED";
            case 209:
                return "NO_ACTIVE_ALARM_SETTINGS";
            case 300:
                return "PROTOCOL_NOT_FOUND";
            case 301:
                return "PROTOCOL_PARSE_ERROR";
            case 303:
                return "PROTOCOL_EXCEPTION_ERROR";
            case 304:
                return "PORT_API_ERROR";
            case 401:
                return "COMM_API_ERROR";
            case 402:
                return "COMM_NOT_FOUND";
            case 403:
                return "DUPLICATE_PORT";
            case 404:
                return "PORT_NOT_FOUND";
            case 405:
                return "PORT_ALREADY_OPEN";
            case 406:
                return "PORT_NOT_OPEN";
            case 407:
                return "PORT_LINE_ERROR";
            case 409:
                return "PORT_NO_ACCESS";
            case 410:
                return "PORT_LOCK_NOT_ACQUIRED";
            case 411:
                return "SYSTEM_PORT";
            case 412:
                return "DUPLICATE_PASSTHROUGH_PORT";
            case 450:
                return "DPORT_ACCESS_FAIL";
            case 451:
                return "DPORT_BIT_READ_ONLY";
            case 452:
                return "DPORT_NOT_INITIALIZED";
            case 500:
                return "PERSISTENCE_FAIL";
            case 501:
                return "PERSIST_API_ERROR";
            case 502:
                return "PERSIST_TRANSACTION_ERROR";
            case 503:
                return "PERSIST_SAVE_ERROR";
            case 504:
                return "PERSIST_DELETE_ERROR";
            case 505:
                return "PERSIST_NOT_OPEN";
            case 600:
                return "MQ_INVALID_PARAMETER";
            case 601:
                return "MQ_RECEIVER_WAKE_ERROR";
            case 602:
                return "MQ_NOT_OPEN";
            case 603:
                return "MQ_CREATION_FAIL";
            case 604:
                return "MQ_WRITE_FAIL";
            case 605:
                return "MQ_INVALID_MESSAGE";
            case 700:
                return "OTAD_FAILURE";
            case 701:
                return "OTAD_ALREADY_IN_PROGRESS";
            case 702:
                return "OTAD_VERSION_EXISTS";
            case 703:
                return "OTAD_SERVER_MISSING";
            case 704:
                return "OTAD_COULDNT_GET_FILE";
            case 707:
                return "OTAD_NO_OTOAD_SCRIPT";
            case 708:
                return "OTAD_SCRIPT_FAILURE";
            case 750:
                return "OTAD_GETTING_FILE";
            case 755:
                return "OTAD_UPDATING";
            case 760:
                return "OTAD_COMPLETE";
            case 800:
                return "NET_CONFIG_NOT_FOUND";
            case 801:
                return "NET_CONFIG_BACKUP_NOT_FOUND";
            case 802:
                return "NET_CONFIG_UNREADABLE";
        }
        return "Unknown";
    }
}
