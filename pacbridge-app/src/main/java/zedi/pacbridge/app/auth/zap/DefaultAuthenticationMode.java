package zedi.pacbridge.app.auth.zap;

import java.util.Arrays;

import zedi.pacbridge.app.auth.AuthenticationDelegate;
import zedi.pacbridge.app.devices.Device;
import zedi.pacbridge.net.logging.TraceLogger;
import zedi.pacbridge.utl.HexStringEncoder;
import zedi.pacbridge.utl.io.Hasher;
import zedi.pacbridge.zap.messages.ChallengeResponseMessage;

class DefaultAuthenticationMode implements AuthenticationMode {
    private AuthenticationDelegate authenticationDelegate;
    private Hasher hasher;
    private TraceLogger traceLogger;
    private byte[] secretKey;
    
    DefaultAuthenticationMode(AuthenticationDelegate authenticationDelegate, Hasher hasher, TraceLogger traceLogger) {
        this.authenticationDelegate = authenticationDelegate;
        this.hasher = hasher;
        this.traceLogger = traceLogger;
    }

    @Override
    public byte[] getSecretKey() {
        return secretKey;
    }

    public boolean isAuthorized(ChallengeResponseMessage response, byte[] serverSalt) {
        Device device = authenticationDelegate.deviceForNuid(response.getUsername());
        if (device != null) {
            //
            // In order to calculate a matching hash, these values must be entered into the hash algorithm in 
            // the same order on both the client and server end
            //
            secretKey = device.getSecretKey();
            hasher.update(response.getUsername().getBytes());
            hasher.update(device.getSecretKey());
            hasher.update(response.getClientSalt());
            hasher.update(serverSalt);
            byte[] hash = hasher.hashedValue(16);
            
            if (traceLogger.isEnabled()) {
                traceLogger.trace("Username: " + HexStringEncoder.bytesAsHexString(response.getUsername().getBytes()) + "(" + response.getUsername()+ ")");
                if (device.getSecretKey() != null)
                    traceLogger.trace("SecretKey: " + HexStringEncoder.bytesAsHexString(device.getSecretKey()));
                traceLogger.trace("ClientSalt: " + HexStringEncoder.bytesAsHexString(response.getClientSalt()));
                traceLogger.trace("ServerSalt: " + HexStringEncoder.bytesAsHexString(serverSalt));
                traceLogger.trace("ClientHash: " + HexStringEncoder.bytesAsHexString(response.getClientHash()));
                traceLogger.trace("ServerHash: " + HexStringEncoder.bytesAsHexString(hash));
                traceLogger.trace("Firmware  : " + response.getFirmwareVersion());
            }
            return Arrays.equals(hash, response.getClientHash());
        }

        return false;
    }
}
