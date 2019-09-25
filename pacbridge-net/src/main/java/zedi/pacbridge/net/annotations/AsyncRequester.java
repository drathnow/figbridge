package zedi.pacbridge.net.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use the <code>AsyncRequester</code> annotation to request that an {@link zedi.pacbridge.utl.ThreadContext}
 * object be injected into a class by the PAC Bridge's networking framework.  The annotation can be applied
 * to either a method that accepts {@link zedi.pacbridge.utl.ThreadContext} argument or on a field with the
 * {@link zedi.pacbridge.utl.ThreadContext} type.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface AsyncRequester {

}
