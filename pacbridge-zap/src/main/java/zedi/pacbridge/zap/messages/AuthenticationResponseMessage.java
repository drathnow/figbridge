package zedi.pacbridge.zap.messages;

import java.nio.ByteBuffer;

import zedi.pacbridge.zap.CountedString;
import zedi.pacbridge.zap.ZapMessageType;

public class AuthenticationResponseMessage extends ZapMessage {
    public static final Integer FIXED_SIZE = 13;

    private ConnectionFlags connectionFlags; 
    private String serverName;
    private byte[] serverHash; 
    private byte[] sessionKey;
    private Integer deviceTime;
    private Integer serverTime;
    
    public AuthenticationResponseMessage(ConnectionFlags connectionFlags, Integer deviceTime, Integer serverTime, String serverName, byte[] serverHash, byte[] sessionKey) {
        super(ZapMessageType.AuthenticationResponse, 0);
        this.connectionFlags = connectionFlags;
        this.deviceTime = deviceTime;
        this.serverTime = serverTime;
        this.serverName = serverName;
        this.serverHash = serverHash;
        this.sessionKey = sessionKey;
    }

    public AuthenticationResponseMessage() {
        super(ZapMessageType.AuthenticationResponse, 0);
        this.connectionFlags = new ConnectionFlags();
        this.connectionFlags.setAuthorized(false);
        this.deviceTime = 0;
        this.serverTime = 0;
    }

    @Override
    public void serialize(ByteBuffer byteBuffer) {
        byteBuffer.put(size().byteValue());
        connectionFlags.serialize(byteBuffer);
        byteBuffer.putInt(deviceTime);
        byteBuffer.putInt(serverTime);
        byteBuffer.put((byte)(serverName == null ? 0 : serverName.length()));
        if (serverName != null)
            byteBuffer.put(serverName.getBytes());
        byteBuffer.put((byte)(serverHash == null ? 0 : serverHash.length));
        if (serverHash != null)
            byteBuffer.put(serverHash);
        byteBuffer.put((byte)(sessionKey == null ? 0 : sessionKey.length));
        if (sessionKey != null)
            byteBuffer.put(sessionKey);
    }
    
    public Integer getDeviceTime() {
        return deviceTime;
    }
    
    public Integer getServerTime() {
        return serverTime;
    }

    public byte[] getServerHash() {
        return serverHash;
    }
    
    public String getServerName() {
        return serverName;
    }
    
    public byte[] getSessionKey() {
        return sessionKey;
    }
    
    public ConnectionFlags getConnectionFlags() {
        return connectionFlags;
    }
    
    @Override
    public Integer size() {
        return FIXED_SIZE 
                + (serverName != null ? serverName.length() : 0) 
                + (serverHash != null ? serverHash.length : 0)
                + (sessionKey != null ? sessionKey.length : 0);
    }
    
    public static final AuthenticationResponseMessage authenticationResponseFromByteByffer(ByteBuffer byteBuffer) {
        byteBuffer.get();
        ConnectionFlags flags = new ConnectionFlags(byteBuffer.get());
        Integer deviceTime = byteBuffer.getInt();
        Integer serverTime = byteBuffer.getInt();
        String serverName = CountedString.stringFromByteBuffer(byteBuffer);
        byte[] serverHash = CountedString.bytesFromByteBuffer(byteBuffer);
        byte[] sessionKey = CountedString.bytesFromByteBuffer(byteBuffer);
        return new AuthenticationResponseMessage(flags, deviceTime, serverTime, serverName, serverHash, sessionKey);
    }

    public boolean isAuthenticated() {
        return connectionFlags.isAuthorized();
    }
}
