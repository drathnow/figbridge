package zedi.pacbridge.net.tcp;

import zedi.pacbridge.net.core.Request;

public interface ServerRequest extends Request {
    public void handleRequestWithServer(ServerHelper server);
}
