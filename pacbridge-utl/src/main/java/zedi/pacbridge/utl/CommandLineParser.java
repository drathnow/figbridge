package zedi.pacbridge.utl;

import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandLineParser {

    public static Properties argumentsFromCommandLine(String[] commandArgs) {
        Properties props = new Properties();
        Pattern pattern = Pattern.compile("(--(.*)=(.*))|--(.*)");
        for (int i = 0; i < commandArgs.length; i++) {
            String arg = commandArgs[i];

            Matcher matcher = pattern.matcher(arg);
            if (matcher.matches() == false)
                continue;

            if (matcher.groupCount() > 1 && matcher.group(1) != null) {
                String key = matcher.group(2).trim();
                String value = matcher.group(3).trim();
                props.setProperty(key, value);
            } else
                props.put(matcher.group(4).trim(), "");
        }
        return props;
    }

    /**
     * Displays a usage file's contents to System.out and exists with a status code of zero.
     * The file must be under the META-INF folder.
     * 
     * @param usageFileName
     */
    public static void displayUsageAndExit(String usageFileName) {
        displayUsageAndExitWithStatus(usageFileName, 0);
    }

    /**
     * Displays a usage file's contents to System.out and exists with the specified status
     * code.  The file must be under the META-INF folder.
     * 
     * @param usageFileName
     */
    public static void displayUsageAndExitWithStatus(String usageFileName, int statusCode) {
        try {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/" + usageFileName);
            byte[] bytes = new byte[stream.available()];
            stream.read(bytes);
            String usage = new String(bytes);
            System.out.println(usage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(statusCode);
    }
}
