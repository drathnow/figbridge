package zedi.pacbridge.app.controls;

import java.util.UUID;

public class RequestIdGenerator {

    public String nextRequestId() {
        return UUID.randomUUID().toString();
    }
}
