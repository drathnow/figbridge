package zedi.pacbridge.net.auth;

import java.nio.ByteBuffer;

import zedi.pacbridge.utl.SiteAddress;

public interface AuthenticationResponse {
    public boolean isAuthenticated();
    public boolean isExpectingDataRequest();
    public void serialize(ByteBuffer byteBuffer);
    public Integer size();
    public SiteAddress authorizedAddress();
}
