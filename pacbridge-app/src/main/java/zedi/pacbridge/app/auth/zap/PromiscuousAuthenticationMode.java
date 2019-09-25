package zedi.pacbridge.app.auth.zap;

import java.util.Arrays;
import java.util.regex.Pattern;

import zedi.pacbridge.app.auth.AuthenticationDelegate;
import zedi.pacbridge.app.devices.Device;
import zedi.pacbridge.net.logging.TraceLogger;
import zedi.pacbridge.utl.HexStringEncoder;
import zedi.pacbridge.utl.io.Hasher;
import zedi.pacbridge.zap.messages.ChallengeResponseMessage;

class PromiscuousAuthenticationMode implements AuthenticationMode {    
    private Pattern usernamePattern;
    private AuthenticationDelegate authenticationDelegate;
    private Hasher hasher;
    private TraceLogger traceLogger;
    private byte[] secretKey;

    PromiscuousAuthenticationMode(AuthenticationDelegate authenticationDelegate, String usernameMatchingRe, Hasher hasher, TraceLogger traceLogger) {
        if (usernameMatchingRe != null)
            this.usernamePattern = Pattern.compile(usernameMatchingRe);
        this.authenticationDelegate = authenticationDelegate;
        this.hasher = hasher;
        this.traceLogger = traceLogger;
    }

    @Override
    public byte[] getSecretKey() {
        return secretKey;
    }
    
    public boolean isAuthorized(ChallengeResponseMessage response, byte[] serverSalt) {
        if (usernamePattern != null) {
            String nuid = response.getUsername();
            if (usernamePattern != null) {
                if (usernamePattern.matcher(nuid).matches())
                    return true;
                else {
                    Device device = authenticationDelegate.deviceForNuid(nuid);
                    if (device == null)
                        return false;
                    else
                        return isMatchingHash(response, serverSalt, device);
                }
            }
        }
        return true;
    }


    protected boolean isMatchingHash(ChallengeResponseMessage response, byte[] serverSalt, Device device) {
        //
        // In order to calculate a matching hash, these values must be entered into the hash algorithm in 
        // the same order on both the client and server end
        //
        hasher.update(response.getUsername().getBytes());
        hasher.update(device.getSecretKey());
        hasher.update(response.getClientSalt());
        hasher.update(serverSalt);
        byte[] hash = hasher.hashedValue(16);
        
        if (traceLogger.isEnabled()) {
            traceLogger.trace("Username: " + HexStringEncoder.bytesAsHexString(response.getUsername().getBytes()));
            if (device.getSecretKey() != null)
                traceLogger.trace("SecretKey: " + HexStringEncoder.bytesAsHexString(device.getSecretKey()));
            traceLogger.trace("ClientSalt: " + HexStringEncoder.bytesAsHexString(response.getClientSalt()));
            traceLogger.trace("ServerSalt: " + HexStringEncoder.bytesAsHexString(serverSalt));
            traceLogger.trace("ClientHash: " + HexStringEncoder.bytesAsHexString(response.getClientHash()));
            traceLogger.trace("ServerHash: " + HexStringEncoder.bytesAsHexString(hash));
        }
        return Arrays.equals(hash, response.getClientHash());        
    }
}
