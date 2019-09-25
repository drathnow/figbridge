package zedi.pacbridge.utl;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Set;
import java.util.jar.JarFile;

import org.jboss.vfs.VirtualFile;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.vfs.SystemDir;
import org.reflections.vfs.Vfs;
import org.reflections.vfs.ZipDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AnnotationDiscoveryHelper {

    private static Logger logger = LoggerFactory.getLogger(AnnotationDiscoveryHelper.class.getName());
    
    static {
        if (System.getProperties().containsKey("jboss.home.dir"))
            addVfsScanners();
    }

    public Set<Class<?>> classesWithAnnotation(Class<? extends Annotation> annotationClass, String fromPackageRoot) {
        Reflections reflections = new Reflections(fromPackageRoot);
        return reflections.getTypesAnnotatedWith(annotationClass);
    }
    
    private static void addVfsScanners() { 
        Vfs.addDefaultURLTypes(
                new Vfs.UrlType() {
                    public boolean matches(URL url) {
                        logger.trace("Scanning classpath entry: "+ url.toString());
                        return url.getProtocol().equals("vfs");
                    }

                    public Vfs.Dir createDir(URL url) {
                        VirtualFile content;
                        try {
                            content = (VirtualFile) url.openConnection().getContent();
                        } catch (Throwable e) {
                            throw new ReflectionsException("could not open url connection as VirtualFile [" + url + "]", e);
                        }

                        Vfs.Dir dir = null;
                        try {
                            dir = createDir(new java.io.File(content.getPhysicalFile().getParentFile(), content.getName()));
                        } catch (IOException eatIt) {
                        }
                        if (dir == null) {
                            try {
                                dir = createDir(content.getPhysicalFile());
                            } catch (IOException eatIt) {
                            }
                        }
                        return dir;
                    }

                    Vfs.Dir createDir(java.io.File file) {
                        try {
                            return file.exists() && file.canRead() ? file.isDirectory() ? new SystemDir(file) : new ZipDir(new JarFile(file)) : null;
                        } catch (IOException e) {
                            logger.error("Unable to create VSF directory", e);
                        }
                        return null;
                    }
                });        
        
    }

}
