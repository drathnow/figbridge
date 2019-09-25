package zedi.pacbridge.utl;

import java.io.InputStream;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;


/**
 * <p>The VersionNumberExtractor class is a utility class that allows easy retrieval and
 * formating of a version numbers from WMX software.  This class assumes
 * version and build information are stored in the manifest file in a JAR
 * file or in a Manifest.additions file located in the 'meta-inf' directory
 * at the root of the class path for the current package.</p>
 * <p> To build the version number, this class will first determine if a class
 * was loaded from a jarfile.  If so, it will read the Manifest.mf from the
 * JAR file and look for "Version" and "Build" attributes. From these, the version
 * and build number will be constructed.</p>
 * <p>If the class did not come from a jar file, it will determine the pathname of
 * the class file and strip off everything from the start of the package name to the
 * end of the absolute pathname.  It will then append "meta-inf/Manifest.additions" onto
 * the resulting path and use the as the file spec to read.</p>
 * <p>The VersionNumberExtractor class is not intended to be instantiated directly but instead must
 * be subclasses.</p>
 * <p>Version numbers have four parts:
 * <br>
 * Major.Minor.Patch.Build</p>
 */
public class VersionNumberExtractor {
    private static final Logger logger = Logger.getLogger(VersionNumberExtractor.class);

    public static final String NOTAVAILABLEKEY = "N/A";
    protected static final String MANIFEST = "/META-INF/MANIFEST.MF";
    protected static final String MAJOR = "Version-Major";
    protected static final String MINOR = "Version-Minor";
    protected static final String PATCH = "Version-Patch";
    protected static final String BUILD = "Version-Build";
    protected String major;
    protected String minor;
    protected String patch;
    protected String build;

    public static final String DEVELOPMENT_RELEASE = "SNAPSHOT";

    /**
     * This constructor is kept protected. Child classes can be derived from this class
     * and can invoke this constructor by default to read and format a version string.
     */
    protected VersionNumberExtractor(Class<?> clazz) {
        Manifest manifest = null;
        major = NOTAVAILABLEKEY;
        minor = NOTAVAILABLEKEY;
        patch = NOTAVAILABLEKEY;
        build = NOTAVAILABLEKEY;

        //  Figure out where this class came from
        try {
            String className = clazz.getName().replace('.', '/');
            String classJar = clazz.getResource('/' + className + ".class").toString();

            // Was it a jar file?
            if (classJar.startsWith("jar:")) {
                URL manifURL = new URL(classJar.substring(0, classJar.indexOf("!") + 1) + MANIFEST);
                InputStream is = manifURL.openStream();
                manifest = new Manifest(is);
                try {
                    is.close();
                } catch (Exception ex) {
                }
            }
            if (manifest != null) {
                Attributes map = manifest.getMainAttributes();
                if (map != null)
                    extractBuildInfoFromAttributeMap(map);
            }

        } catch (Exception e) {
            //  Normally, we won't worry about this.
            logger.debug("Could not read Manifest", e);
        }
    }

    protected void extractBuildInfoFromAttributeMap(Attributes attributeMap) {
        major = (attributeMap.getValue(MAJOR) != null) ? attributeMap.getValue(MAJOR) : NOTAVAILABLEKEY;
        minor = (attributeMap.getValue(MINOR) != null) ? attributeMap.getValue(MINOR) : NOTAVAILABLEKEY;
        patch = (attributeMap.getValue(PATCH) != null) ? attributeMap.getValue(PATCH) : NOTAVAILABLEKEY;
        build = (attributeMap.getValue(BUILD) != null) ? attributeMap.getValue(BUILD) : NOTAVAILABLEKEY;
    }

    public String getVersionNumber() {
        if (major.equals(NOTAVAILABLEKEY))
            return DEVELOPMENT_RELEASE;
        return 'V' + major + '.' + minor + '.' + patch + '.' + build;
    }

    public String toString() {
        return getVersionNumber();
    }

    public static VersionNumberExtractor versionNumberExtractorForClass(Class<?> clazz) {
        return new VersionNumberExtractor(clazz);
    }

    public String cvsVersionTagForVersionNumber() {
        if (major.equals(NOTAVAILABLEKEY))
            return DEVELOPMENT_RELEASE;
        return 'V' + major + '_' + minor + '_' + patch;
    }

    public static String archiveNameForClassName(String clazzName) throws ClassNotFoundException {
        return archiveNameForClass(Class.forName(clazzName));
    }

    public static String archiveNameForClass(Class<?> clazz) {
        String className = clazz.getName().replace('.', '/');
        return clazz.getResource('/' + className + ".class").toString();
    }
}
