package zedi.pacbridge.utl;

import java.lang.reflect.Field;

import javax.naming.InitialContext;

public interface DependencyResolver {
    public static final String LOOKUP_NAME_FIELD_NAME = "JNDI_NAME";

    public <T> T getImplementationOf(String lookupName);
    public <T> T getImplementationOf(Class<T> objectClass);
    
    public static class Implementation implements DependencyResolver {

        static DependencyResolver sharedInstance;
        
        public static DependencyResolver sharedInstance() {
            if (sharedInstance == null)
                sharedInstance = new Implementation();
            return sharedInstance;
        }
        
        public static void setImplementation(DependencyResolver dependencyResolver) {
            sharedInstance = dependencyResolver;
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getImplementationOf(String lookupName) {
            try {
                InitialContext initialContext = new InitialContext();
                return (T)initialContext.lookup(lookupName);
            } catch (Exception e) {
                throw new RuntimeException("Unable to lookup object '" + lookupName + "'", e);
            }
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getImplementationOf(Class<T> objectClass) {
            String lookupName = null;
            try {
                Field field = objectClass.getDeclaredField(LOOKUP_NAME_FIELD_NAME);
                lookupName = (String)field.get(objectClass);
            } catch (Exception e) {
                throw new RuntimeException("Unable to lookup field "
                        + LOOKUP_NAME_FIELD_NAME 
                        + " on class " 
                        + objectClass.getName(),e);
            }
            return (T)getImplementationOf(lookupName);
        }        
    }
}
