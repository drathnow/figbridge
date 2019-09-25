package zedi.pacbridge.msg;

/**
 * A JmsServerReconnector encapsulates the logic neccessary to determine if connection to a JMS
 * server has been reestablished in the event connection is lost.  All JmsImplemenators must provide
 * one of these in order for the JMS subsystem to maintain connections to the server.
 *
 */
public interface JmsServerReconnector {
    public boolean isConnectionReestabilshed();
}
