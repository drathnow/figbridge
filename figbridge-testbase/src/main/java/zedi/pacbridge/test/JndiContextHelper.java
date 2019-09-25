package zedi.pacbridge.test;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;


/**
 * Provides a JNDI initial context factory for the MockContext.
 */
public class JndiContextHelper implements InitialContextFactory {
    private static Context mockCtx = null;

    public static void setContext(Context ctx) {
        if (ctx == null)
            System.getProperties().remove(Context.INITIAL_CONTEXT_FACTORY);
        else
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY, JndiContextHelper.class.getName());
        mockCtx = ctx;
    }

    public Context getInitialContext(java.util.Hashtable<?, ?> environment) throws NamingException {
        if (mockCtx == null) {
            throw new IllegalStateException("mock context was not set.");
        }
        return mockCtx;
    }
}