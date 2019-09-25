package zedi.pacbridge.gdn;

import java.io.Serializable;

public class IoPointIndexes implements Serializable {
    private static final long serialVersionUID = 1L;

    // The following constants define index number for the PAC internal IO
    // Points
    public static final int INDEX_FOR_ERROR_CODE = 255;
    public static final int INDEX_FOR_SERIAL_NUMBER_ONE = 254;
    public static final int INDEX_FOR_SERIAL_NUMBER_TWO = 253;
    public static final int INDEX_FOR_SOFTWARE_VERSION = 252;
    public static final int INDEX_FOR_NUMBER_OF_IO_POINTS = 251;
    public static final int INDEX_FOR_RTU_DEVICE_TYPE = 250;
    public static final int INDEX_FOR_NET_DEVICE_TYPE = 249;
    public static final int INDEX_FOR_SYSTIME = 246;
    public static final int INDEX_FOR_POLLA_INTERVAL = 245;
    public static final int INDEX_FOR_POLLA_START_TIME = 244;
    public static final int INDEX_FOR_POLLB_INTERVAL = 243;
    public static final int INDEX_FOR_POLLB_START_TIME = 242;
    public static final int INDEX_FOR_REPORTA_INTERVAL = 241;
    public static final int INDEX_FOR_REPORTA_START_TIME = 240;
    public static final int INDEX_FOR_REPORTB_INTERVAL = 239;
    public static final int INDEX_FOR_REPORTB_START_TIME = 238;
    public static final int INDEX_FOR_PIDA_INDEX = 237;
    public static final int INDEX_FOR_PIDB_INDEX = 236;
    public static final int INDEX_FOR_SEND_ACK_FLAG = 235;
    public static final int INDEX_FOR_WRITEBACK = 234;
    public static final int INDEX_FOR_SNR = 233;
    public static final int INDEX_FOR_LOPOWER = 232;
    public static final int INDEX_FOR_PAC_VOLTAGE = 231;
    public static final int INDEX_FOR_TAG_NUMBER = 230;
    public static final int INDEX_FOR_ALARM_FILTER_STATUS = 229;
    public static final int INDEX_FOR_ALARM_FILTER_TIME = 228;
    public static final int INDEX_FOR_MASTER_ALARM_INDEX = 227;
    public static final int INDEX_FOR_PAC_REBOOT_SWITCH = 225;
    public static final int INDEX_FOR_PAC_BUILD_NUMBER = 224;
    public static final int INDEX_FOR_PAC_OPERATIONAL_FLAGS = 223;
    public static final int INDEX_FOR_LCP_ECHO_INTERVAL = 222;
    public static final int INDEX_FOR_NETWORK_TERMINAL_FAILURE_COUNT = 221;
    public static final int INDEX_FOR_PIDC_INDEX = 220;
    public static final int INDEX_FOR_PIDC_START_TIME = 219;
    public static final int INDEX_FOR_POLLC_INTERVAL = 218;
    public static final int INDEX_FOR_REPORTC_INTERVAL = 217;
    public static final int INDEX_FOR_PIDD_INDEX = 216;
    public static final int INDEX_FOR_PIDD_START_TIME = 215;
    public static final int INDEX_FOR_POLLD_INTERVAL = 214;
    public static final int INDEX_FOR_REPORTD_INTERVAL = 213;
    public static final int INDEX_FOR_PIDE_INDEX = 212;
    public static final int INDEX_FOR_PIDE_START_TIME = 211;
    public static final int INDEX_FOR_POLLE_INTERVAL = 210;
    public static final int INDEX_FOR_REPORTE_INTERVAL = 209;
    public static final int INDEX_FOR_PIDF_INDEX = 208;
    public static final int INDEX_FOR_PIDF_START_TIME = 207;
    public static final int INDEX_FOR_POLLF_INTERVAL = 206;
    public static final int INDEX_FOR_REPORTF_INTERVAL = 205;
    public static final int INDEX_FOR_PIDG_INDEX = 204;
    public static final int INDEX_FOR_PIDG_START_TIME = 203;
    public static final int INDEX_FOR_POLLG_INTERVAL = 202;
    public static final int INDEX_FOR_REPORTG_INTERVAL = 201;
    public static final int INDEX_FOR_PIDH_INDEX = 200;
    public static final int INDEX_FOR_PIDH_START_TIME = 199;
    public static final int INDEX_FOR_POLLH_INTERVAL = 198;
    public static final int INDEX_FOR_REPORTH_INTERVAL = 197;
    public static final int INDEX_FOR_PIDI_INDEX = 196;
    public static final int INDEX_FOR_PIDI_START_TIME = 195;
    public static final int INDEX_FOR_POLLI_INTERVAL = 194;
    public static final int INDEX_FOR_REPORTI_INTERVAL = 193;
    public static final int INDEX_FOR_PIDJ_INDEX = 192;
    public static final int INDEX_FOR_PIDJ_START_TIME = 191;
    public static final int INDEX_FOR_POLLJ_INTERVAL = 190;
    public static final int INDEX_FOR_REPORTJ_INTERVAL = 189;
    public static final int INDEX_FOR_PIDK_INDEX = 188;
    public static final int INDEX_FOR_PIDK_START_TIME = 187;
    public static final int INDEX_FOR_POLLK_INTERVAL = 186;
    public static final int INDEX_FOR_REPORTK_INTERVAL = 185;
    public static final int INDEX_FOR_PIDL_INDEX = 184;
    public static final int INDEX_FOR_PIDL_START_TIME = 183;
    public static final int INDEX_FOR_POLLL_INTERVAL = 182;
    public static final int INDEX_FOR_REPORTL_INTERVAL = 181;
    public static final int INDEX_FOR_RTU_BACKOFF_TIMER = 180;
    public static final int INDEX_FOR_RTU_BACKOFF_COUNT = 179;
    public static final int INDEX_FOR_SITE_COMM_PING_TIME = 178;
    public static final int INDEX_FOR_PIDM_INDEX = 177;
    public static final int INDEX_FOR_PIDM_START_TIME = 176;
    public static final int INDEX_FOR_POLLM_INTERVAL = 175;
    public static final int INDEX_FOR_REPORTM_INTERVAL = 174;
    public static final int INDEX_FOR_PIDN_INDEX = 173;
    public static final int INDEX_FOR_PIDN_START_TIME = 172;
    public static final int INDEX_FOR_POLLN_INTERVAL = 171;
    public static final int INDEX_FOR_REPORTN_INTERVAL = 170;
    public static final int INDEX_FOR_PIDO_INDEX = 169;
    public static final int INDEX_FOR_PIDO_START_TIME = 168;
    public static final int INDEX_FOR_POLLO_INTERVAL = 167;
    public static final int INDEX_FOR_REPORTO_INTERVAL = 166;
    public static final int INDEX_FOR_PIDP_INDEX = 165;
    public static final int INDEX_FOR_PIDP_START_TIME = 164;
    public static final int INDEX_FOR_POLLP_INTERVAL = 163;
    public static final int INDEX_FOR_REPORTP_INTERVAL = 162;
    public static final int INDEX_FOR_PAC_RUN_LEVEL = 161;
    public static final int INDEX_FOR_PAC_TIME_SYNC = 160;
    public static final int INDEX_FOR_REPORT_OFFSET = 159;
    public static final int INDEX_BOTTOM_INTERNAL = INDEX_FOR_REPORT_OFFSET;
    
    
    public static final int CURRENT_NUMBER_OF_INDEXES = 76;
    public static final int DEFAULT_INDEXES_START = INDEX_FOR_ERROR_CODE;
    public static final int DEFAULT_INDEXES_END = INDEX_FOR_PAC_RUN_LEVEL;
    

    // This is a hard coded event number or the PAC communication ping
    // event
    public static final int EVENT_INDEX_FOR_SITE_COMM_PING_TIME = 29;

    public static final int DIAGNOSTIC_POLLSET_NUMBER = 255;
 
    public static String readableEventList() {
        String filler = "   ";
        StringBuffer stringBuffer = new StringBuffer();
        for (char eventLetter = 'A'; eventLetter <= 'P'; eventLetter++) {
            try {
                if (eventLetter < 'C') {
                        int pollStartTimeIndex = IoPointIndexes.class.getDeclaredField("INDEX_FOR_POLL" + eventLetter + "_START_TIME").getInt(null);
                        int pollIntervalIndex = IoPointIndexes.class.getDeclaredField("INDEX_FOR_POLL" + eventLetter + "_INTERVAL").getInt(null);
                        int reportStartTimeIndex = IoPointIndexes.class.getDeclaredField("INDEX_FOR_REPORT" + eventLetter + "_START_TIME").getInt(null);
                        int reportIntervalIndex = IoPointIndexes.class.getDeclaredField("INDEX_FOR_REPORT" + eventLetter + "_INTERVAL").getInt(null);
                        int pollSetIndex = IoPointIndexes.class.getDeclaredField("INDEX_FOR_PID" + eventLetter + "_INDEX").getInt(null);
                        stringBuffer
                            .append("Event: ").append(eventLetter).append('\n')
                            .append(filler).append("Poll Start Time Index  : ").append(pollStartTimeIndex).append('\n')
                            .append(filler).append("Poll Interval Index    : ").append(pollIntervalIndex).append('\n')
                            .append(filler).append("Report Start Time Index: ").append(reportStartTimeIndex).append('\n')
                            .append(filler).append("Report Interval Index  : ").append(reportIntervalIndex).append('\n')
                            .append(filler).append("Poll Set Index         : ").append(pollSetIndex).append('\n');
                } else {
                    int startTimeIndex = IoPointIndexes.class.getDeclaredField("INDEX_FOR_PID" + eventLetter + "_START_TIME").getInt(null);
                    int pollIntervalIndex = IoPointIndexes.class.getDeclaredField("INDEX_FOR_POLL" + eventLetter + "_INTERVAL").getInt(null);
                    int reportIntervalIndex = IoPointIndexes.class.getDeclaredField("INDEX_FOR_REPORT" + eventLetter + "_INTERVAL").getInt(null);
                    int pollSetIndex = IoPointIndexes.class.getDeclaredField("INDEX_FOR_PID" + eventLetter + "_INDEX").getInt(null);
                    stringBuffer
                        .append("Event: ").append(eventLetter).append('\n')
                        .append(filler).append("Start Time Index     : ").append(startTimeIndex).append('\n')
                        .append(filler).append("Poll Interval Index  : ").append(pollIntervalIndex).append('\n')
                        .append(filler).append("Report Interval Index: ").append(reportIntervalIndex).append('\n')
                        .append(filler).append("Poll Set Index       : ").append(pollSetIndex).append('\n');
                }
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                throw new RuntimeException("Unable to format event " + eventLetter, e);
            }
        }
        return stringBuffer.toString();
    }
    
    public static String eventListAsCsvString() {
        StringBuffer stringBufferAB = new StringBuffer();
        stringBufferAB.append("Event,Poll Start Time Index,Poll Interval Index,Report Start Time Index,Report Interval Index,Poll Set Index\n");
        
        StringBuffer stringBufferTheRest = new StringBuffer();
        stringBufferTheRest.append("Event,Start Time Index,Poll Interval Index,Report Interval Index,Poll Set Index\n");
        
        for (char eventLetter = 'A'; eventLetter <= 'P'; eventLetter++) {
            try {
                if (eventLetter < 'C') {
                        int pollStartTimeIndex = IoPointIndexes.class.getDeclaredField("INDEX_FOR_POLL" + eventLetter + "_START_TIME").getInt(null);
                        int pollIntervalIndex = IoPointIndexes.class.getDeclaredField("INDEX_FOR_POLL" + eventLetter + "_INTERVAL").getInt(null);
                        int reportStartTimeIndex = IoPointIndexes.class.getDeclaredField("INDEX_FOR_REPORT" + eventLetter + "_START_TIME").getInt(null);
                        int reportIntervalIndex = IoPointIndexes.class.getDeclaredField("INDEX_FOR_REPORT" + eventLetter + "_INTERVAL").getInt(null);
                        int pollSetIndex = IoPointIndexes.class.getDeclaredField("INDEX_FOR_PID" + eventLetter + "_INDEX").getInt(null);
                        stringBufferAB
                            .append(eventLetter).append(',')
                            .append(pollStartTimeIndex).append(',')
                            .append(pollIntervalIndex).append(',')
                            .append(reportStartTimeIndex).append(',')
                            .append(reportIntervalIndex).append(',')
                            .append(pollSetIndex).append('\n');
                } else {
                    int startTimeIndex = IoPointIndexes.class.getDeclaredField("INDEX_FOR_PID" + eventLetter + "_START_TIME").getInt(null);
                    int pollIntervalIndex = IoPointIndexes.class.getDeclaredField("INDEX_FOR_POLL" + eventLetter + "_INTERVAL").getInt(null);
                    int reportIntervalIndex = IoPointIndexes.class.getDeclaredField("INDEX_FOR_REPORT" + eventLetter + "_INTERVAL").getInt(null);
                    int pollSetIndex = IoPointIndexes.class.getDeclaredField("INDEX_FOR_PID" + eventLetter + "_INDEX").getInt(null);
                    stringBufferTheRest
                        .append(eventLetter).append(',')
                        .append(startTimeIndex).append(',')
                        .append(pollIntervalIndex).append(',')
                        .append(reportIntervalIndex).append(',')
                        .append(pollSetIndex).append('\n');
                }
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                throw new RuntimeException("Unable to format event " + eventLetter, e);
            }
        }
        return stringBufferAB.append(stringBufferTheRest.toString()).toString();
    }
}
