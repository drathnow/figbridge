package zedi.pacbridge.net.auth;



public interface AuthenticationListener {
    public void authenticationStarted();
    public void deviceAuthenticated(AuthenticationContext authenticationContext);
    public void authenticationFailed();
}
