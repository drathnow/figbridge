package zedi.pacbridge.app.auth.zap;

import zedi.pacbridge.zap.messages.ChallengeResponseMessage;


interface AuthenticationMode {
    public boolean isAuthorized(ChallengeResponseMessage response, byte[] serverSalt);
    public byte[] getSecretKey(); 
}