package zedi.pacbridge.net.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import zedi.pacbridge.net.PacketDecoder;
import zedi.pacbridge.net.PacketLayer;

/**
 * Annotation do indicate a class can be used by {@link PacketLayer} objects to encode
 * packets.  Classes that use this annotation must implement the {@link PacketDecoder}
 * interface and must provide a no argument constructor;
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface PacketLayerDecoder {
    public String forNetworkType();
}
