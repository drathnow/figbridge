package zedi.pacbridge.utl;

import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StringUtilities {
    private static final String NONPRINTABLE_CHARS_REGEX = ".*[^\\p{Print}].*";
    private static final Logger logger = LoggerFactory.getLogger(StringUtilities.class);
    public static final char HEX_CHARS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final String REGEX_FOR_NASTY_CHARS_IN_FILENAME = "\\\\|/|\\*|\\||\\\"|<|>|\\?";
    private static final Pattern pattern = Pattern.compile(REGEX_FOR_NASTY_CHARS_IN_FILENAME);
    private static final Pattern nonPrintableCharPattern = Pattern.compile(NONPRINTABLE_CHARS_REGEX);
    private static final String PROPERTY_RE = "\\$\\{(.+?)\\}";
    private static final Pattern propertyPattern = Pattern.compile(PROPERTY_RE);

    public static String validPlatformFilenameForFilename(String aFilename) {
        return pattern.matcher(aFilename).replaceAll("_");
    }

    public static boolean containsUnprintableASCIICharacter(String string) {
        return nonPrintableCharPattern.matcher(string).matches();
    }

    public static String stringAfterPropertyReplacement(String string, Properties properties) {
        String result = string;
        Matcher matcher = propertyPattern.matcher(string);
        while (matcher.find()) {
            String variable = matcher.group(1);
            String replacement = properties.getProperty(variable);
            if (replacement != null) {
                String replacementString = "${" + variable + "}";
                result = result.replace(replacementString, replacement);
            }
        }
        return result;
    }

    public static String capitalize(String string) {
        String result = string;
        if (StringUtilities.isValidInputString(string)) {
            char c = string.charAt(0);
            if (Character.isLowerCase(c))
                result = Character.toUpperCase(c) + string.substring(1);
        }
        return result;
    }

    public static boolean isValidInputString(String value) {
        return value != null && !"".equals(value.trim()) && value.trim().length() > 0;
    }

    public static boolean stringContainsOnlyCharactersDigitsAndSpaces(String string) {
        if (!StringUtilities.isValidInputString(string))
            return false;
        int length = string.length();
        for (int i = 0; i < length; i++) {
            char aChar = string.charAt(i);
            if (!Character.isLetterOrDigit(aChar) && !Character.isSpaceChar(aChar))
                return false;
        }
        return true;
    }

    public static String correctedStringForNonCharactersOrDigitsOrWhiteSpace(byte[] bytesForString) {
        for (int i = 0; i < bytesForString.length; i++)
            if (bytesForString[i] < 32 || bytesForString[i] > 126)
                bytesForString[i] = '?';
        return new String(bytesForString);
    }

    public final static String decodedTEAVString(String aEncodedPassword) {
        TEAV enc = new TEAV();
        String encryptedValue = aEncodedPassword;
        String returnValue = null;
        if (encryptedValue != null) {
            try {
                String unencryptedValue = enc.decode(encryptedValue);
                returnValue = unencryptedValue.trim();
            } catch (Exception e) {
                logger.error("String encryption failed ", e);
            }
        }
        return returnValue;
    }

    public final static boolean isAlphaNumericString(String aString) {
        for (int i = 0; i < aString.length(); i++)
            if (!Character.isLetterOrDigit(aString.charAt(i)))
                return false;
        return true;
    }

    public final static String encodedTEAVString(String aPassword) {
        return new TEAV().encode(aPassword);
    }

    public static String inetSocketAddressToString(InetSocketAddress aSocketAddress) {
        return aSocketAddress.getAddress().getHostAddress() + ":" + aSocketAddress.getPort();
    }

    public static String reverse(String aString) {
        if (aString.length() == 1)
            return aString;
        return new String(reverse(aString.substring(1)) + aString.charAt(0));
    }
    
    public static boolean isNumericString(String aString) {
        return isNumericString(aString, "(^[-|\\+]?\\d\\d*\\.\\d*$)|(^[-|\\+]?\\d\\d*$)|(^[-|\\+]?\\.\\d\\d*$)");
    }

    public static boolean isNumericString(String aString, String regexString) {
        return isValidInputString(aString) && Pattern.matches(regexString, aString);
    }
}
