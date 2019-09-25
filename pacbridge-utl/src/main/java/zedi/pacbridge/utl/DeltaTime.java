package zedi.pacbridge.utl;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeltaTime {
    private static Integer SECONDS_IN_A_DAY = 86400;
    private static Integer SECONDS_IN_AN_HOUR = 3600;
    private static Integer SECONDS_IN_AN_MIN = 60;
    
    public static final String FULL_TIME_FMT = "{0} {1,number,00}:{2,number,00}:{3,number,00}";
    public static final String JUST_TIME_FMT = "{0,number,00}:{1,number,00}:{2,number,00}";
    public static final String FULL_TIME_RE = "(\\d+) *(\\d+):(\\d+):(\\d+)";
    public static final String JUST_TIME_RE = "(\\d+):(\\d+):(\\d+)";
    
    private static Pattern fullTimeRe = Pattern.compile(FULL_TIME_RE);
    private static Pattern justTimeRe = Pattern.compile(JUST_TIME_RE);
    
    public static Integer deltaTimeStringToSeconds(String deltaTimeString) {
        int days = 0;
        int hours = 0;
        int mins = 0;
        int secs = 0;
        Matcher matcher = justTimeRe.matcher(deltaTimeString);
        if (matcher.matches()) {
            hours = Integer.parseInt(matcher.group(1));
            mins = Integer.parseInt(matcher.group(2));
            secs = Integer.parseInt(matcher.group(3));
        } else {
            matcher = fullTimeRe.matcher(deltaTimeString);
            if (matcher.matches()) {
                days = Integer.parseInt(matcher.group(1));
                hours = Integer.parseInt(matcher.group(2));
                mins = Integer.parseInt(matcher.group(3));
                secs = Integer.parseInt(matcher.group(4));
            } else
                throw new IllegalArgumentException("Delta time string has invalid format.  Must be '[days] HH:MM:SS'");
        }
        
        return (days*SECONDS_IN_A_DAY) + (hours*SECONDS_IN_AN_HOUR) + (mins*SECONDS_IN_AN_MIN) + secs;
    }

    public static String deltaTimeStringForSeconds(Integer seconds) {
        int days = seconds/SECONDS_IN_A_DAY;
        if (days > 0)
            return MessageFormat.format(FULL_TIME_FMT, 
                                        days, 
                                        (seconds%SECONDS_IN_A_DAY)/SECONDS_IN_AN_HOUR, 
                                        (seconds%SECONDS_IN_AN_HOUR)/SECONDS_IN_AN_MIN, 
                                        seconds%SECONDS_IN_AN_MIN);
        return MessageFormat.format(JUST_TIME_FMT, 
                                    (seconds%SECONDS_IN_A_DAY)/SECONDS_IN_AN_HOUR, 
                                    (seconds%SECONDS_IN_AN_HOUR)/SECONDS_IN_AN_MIN, 
                                    seconds%SECONDS_IN_AN_MIN);
    }
}
