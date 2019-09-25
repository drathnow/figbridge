package zedi.pacbridge.utl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class Utilities {
    private static Logger logger = LoggerFactory.getLogger(Utilities.class.getName());

    public static final String IP_ADDRESS_REGEX = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})";
    public static final long DISTANT_FUTURE_INMILLIS = 0x7fffffffffffffffL;
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MMM-dd HH:mm:ss";
    public static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static SimpleDateFormat DateFormatter;
    public static SimpleDateFormat ISO8601DateFormatterUtc;
    public static SimpleDateFormat universalDateFormatter = null;

    private static Pattern IpAddressPattern = Pattern.compile(IP_ADDRESS_REGEX);
    private static Pattern pattern = Pattern.compile(IP_ADDRESS_REGEX);
    
    static {
        ISO8601DateFormatterUtc = new SimpleDateFormat(ISO8601_DATE_FORMAT);
        ISO8601DateFormatterUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        IpAddressPattern = Pattern.compile(IP_ADDRESS_REGEX);
    }
    

    public static long hash(String string) {
        long hashPrime = 1125899906842597L; // prime
        int len = string.length();
        for (int i = 0; i < len; i++)
            hashPrime = 31 * hashPrime + string.charAt(i);
        return hashPrime;
    }

    public static long ipAddressAsIPv4Int(String address) {
        Matcher matcher = pattern.matcher(address.trim());
        if (matcher.matches()) {
            long value = Long.parseLong(matcher.group(1)) << 24 
                            | Long.parseLong(matcher.group(2)) << 16 
                            | Long.parseLong(matcher.group(3)) << 8 
                            | Long.parseLong(matcher.group(4));
            return value;
        }
        return Long.MIN_VALUE;
    }

    public static byte[] byteArrayfromByteBuffer(ByteBuffer buffer) {
        buffer.flip();
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        return bytes;
    }

    public static boolean logicalXOR(boolean x, boolean y) {
        return ((x || y) && !(x && y));
    }

    public static String messageForException(Throwable exception) {
        String msg = exception.getClass().getName();
        if (exception.getMessage() != null)
            msg = msg.concat(": " + exception.getLocalizedMessage());
        return msg;
    }

    public static String exceptionStackTraceAsString(Throwable exception) {
        String result = "Error parsing exception";
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            result = sw.toString();
        } catch (Exception e) {
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> listOfObjecType(Class<T> clazz) {
        List<T> theList = new ArrayList<T>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType() == clazz)
                try {
                    theList.add((T)field.get(null));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                }
        }
        return theList;
    }
    
    public static byte[] objectAsByteArrays(Object object) throws IOException {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
        objectOutputStream.writeObject(object);
        return arrayOutputStream.toByteArray();
    }

    public static Object byteArrayAsObject(byte[] byteArray) throws Exception {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteArray);
        ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
        return inputStream.readObject();
    }

    public static boolean isValidIpAddress(String ipAddress) {
        if (ipAddress.equals("localhost"))
            return true;
        Matcher matcher = IpAddressPattern.matcher(ipAddress);
        if (matcher.matches()) {
            int value = Integer.parseInt(matcher.group(1));
            if (value < 1 || value > 255)
                return false;

            for (int i = 1; i < matcher.groupCount(); i++) {
                value = Integer.parseInt(matcher.group(i + 1));
                if (value < 0 || value > 255)
                    return false;
            }
            return true;
        }
        return false;
    }

    public static String jarFilenameContainingClass(Class<?> aClass) {
        String className = aClass.getName().replace('.', '/');
        return aClass.getResource("/" + className + ".class").toString();
    }

    public static final String deserializedStringFromStream(DataInputStream input) throws IOException {
        int length = input.readByte();
        byte[] bytes = new byte[length];
        input.readFully(bytes);
        return new String(bytes);
    }

    public static final void serializeStringToDataOutputStream(String address, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.write(address.length());
        dataOutputStream.write(address.getBytes());
    }

    protected static void closeInputStream(InputStream anInputStream) {
        try {
            if (anInputStream != null)
                anInputStream.close();
        } catch (IOException e) {
        }
    }

    /**
     * General form of a resource from a jarfile is:
     * 
     * jar:file:/<FQ-path-to-jar>!<FQ-class-name>
     * 
     * eg: jar:file:/C:/developer/fine/jstp/target/jstp-5.0.0.jar!/zedi/gdn/net/
     * WindowsStpLibraryLoader.class
     * 
     * @return
     */
    public static File archiveDirectoryForClass(Class<?> clazz) {
        String className = clazz.getName().replace('.', '/');
        String archiveName = clazz.getResource("/" + className + ".class").toString();
        if (archiveName.startsWith("jar:") == false)
            return null;
        return new File(filenameFromJavaArhiveName(archiveName)).getParentFile();
    }

    protected static String filenameFromJavaArhiveName(String anArchiveName) {
        return anArchiveName.replaceAll("jar:file:*:/|!.*$", "");
    }

    public static String stringForResourceNamed(String aResourceName) throws IOException {
        InputStream inputStream = Utilities.class.getResourceAsStream(aResourceName);
        if (inputStream == null)
            return null;
        byte bytes[] = new byte[inputStream.available()];
        for (int i = 0; i < bytes.length; bytes[i++] = (byte)inputStream.read());
        return new String(bytes);
    }

    public static void zeroTimeInCalendar(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static final Field[] fieldsFromClassWithNameMatchingPatterString(Class<?> clazz, String aRegExPatternString) {
        List<Field> fieldList = new ArrayList<Field>();
        Pattern pattern = Pattern.compile(aRegExPatternString);
        Field[] fields = clazz.getFields();
        for (int i = 0; i < fields.length; i++) {
            if (pattern.matcher(fields[i].getName()).find())
                fieldList.add(fields[i]);
        }
        return (Field[])fieldList.toArray(new Field[0]);
    }

    public static Number numericValueForSystemProperty(String propertyName, Number defaultValue, Class<?> numberClass) {
        String value = System.getProperty(propertyName, defaultValue.toString());
        try {
            Constructor<?> constructor = numberClass.getConstructor(new Class[]{String.class});
            return (Number)constructor.newInstance(new Object[]{value});
        } catch (NumberFormatException e) {
            logger.warn("Invalid number value specified for property " + propertyName + "(" + value + ")" + ". Default value " + defaultValue.toString() + " will be used");
            return defaultValue;
        } catch (Exception e) {
            logger.error("Unable to create number object", e);
            return null;
        }
    }

    public static void loadUserBuildProperties() {
        String userHomeDir = System.getProperty("user.home");
        File zediDevDir = new File(userHomeDir, ".zedidev");
        loadPropertiesFromFileIfItExists(new File(zediDevDir, "build.properties"));
    }

    private static void loadPropertiesFromFileIfItExists(File propertiesFile) {
        Properties properties = new Properties();
        if (propertiesFile.exists()) {
            try {
                properties.load(new FileInputStream(propertiesFile));
                loadSystemPropertiesWithoutOverride(properties);
            } catch (IOException e) {
                logger.error("Unable to load build properties from " + propertiesFile.getAbsolutePath(), e);
            }
        }
    }

    public static void loadSystemPropertiesWithoutOverride(Properties properties) {
        for (Iterator<Object> iterator = properties.keySet().iterator(); iterator.hasNext();) {
            String key = (String)iterator.next();
            if (!System.getProperties().containsKey(key))
                System.setProperty(key, properties.getProperty(key));
        }
    }

    public static byte[] clonedArray(byte[] aSomeBytes, int aLength) {
        byte[] bytes = new byte[aLength];
        System.arraycopy(aSomeBytes, 0, bytes, 0, aLength);
        return bytes;
    }

    public static byte[] serializedObject(Object object) throws IOException {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(arrayOutputStream);
        objectOutputStream.writeObject(object);
        return arrayOutputStream.toByteArray();
    }

    public static Integer sizeValueFromSystemProperty(String propertyName, Integer defaultValue) {
        String value = System.getProperty(propertyName, defaultValue.toString()).trim().toLowerCase();
        try {
            int multiplier = 1;
            if (value.endsWith("k") || value.endsWith("m")) {
                multiplier = value.endsWith("k") ? 1000 : 1000000;
                value = value.substring(0, value.length() - 1);
            }
            return new Integer(Integer.parseInt(value) * multiplier);
        } catch (NumberFormatException e) {
            logger.warn("Invalid number value specified for property " + propertyName + "(" + value + ")" + ". Default value " + propertyName.toString() + " will be used");
            return defaultValue;
        } catch (Exception e) {
            logger.error("Unable to create number object", e);
            return null;
        }
    }
    public static int minuteOfDayForDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return 60 * calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE);
    }

    public static int secondOfDayForDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return 3600 * calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);
    }

    public static int hourOfDayForDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static InputStream getResourceAsStream(String name, Class<?> clazz) {
        ClassLoader loader;
        InputStream inputStream = null;

        try {
            loader = Thread.currentThread().getContextClassLoader();
            if (loader != null) {
                inputStream = loader.getResourceAsStream(name);
                if (inputStream != null)
                    return inputStream;
            }
        } catch (Throwable t) {
        }

        if (clazz != null) {
            try {
                loader = clazz.getClassLoader();
                if (loader != null) {
                    inputStream = loader.getResourceAsStream(name);
                    if (inputStream != null)
                        return inputStream;
                }
            } catch (Throwable t) {
            }
        }

        try {
            loader = ClassLoader.getSystemClassLoader();
            if (loader != null) {
                return loader.getResourceAsStream(name);
            }
        } catch (Throwable t) {
        }

        return inputStream;
    }
}
