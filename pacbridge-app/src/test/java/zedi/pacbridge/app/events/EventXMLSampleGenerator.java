package zedi.pacbridge.app.events;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import zedi.pacbridge.app.annotations.SampleEventXML;
import zedi.pacbridge.utl.JDomUtilities;

public class EventXMLSampleGenerator {

    public static void main(String[] args) {
        try {
            Iterable<Class<?>> classes = getClasses("zedi.pacbridge.app.events");
            for (Iterator<Class<?>> iter = classes.iterator(); iter.hasNext(); ) {
                for (Method method : iter.next().getMethods()) {
                    if (method.isAnnotationPresent(SampleEventXML.class)) {
                        String s = (String)method.invoke(null, null);
                        System.out.println(JDomUtilities.xmlStringForElement(JDomUtilities.elementForXmlString(s)));
                        System.out.println();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Scans all classes accessible from the context class loader which belong
     * to the given package and subpackages.
     * 
     * @param packageName
     *            The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Iterable<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements())
        {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (File directory : dirs)
        {
            classes.addAll(findClasses(directory, packageName));
        }

        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * subdirs.
     * 
     * @param directory
     *            The base directory
     * @param packageName
     *            The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException
    {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (!directory.exists())
        {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files)
        {
            if (file.isDirectory())
            {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            }
            else if (file.getName().endsWith(".class"))
            {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
    
}
