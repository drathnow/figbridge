package zedi.pacbridge.app.events;

import java.util.UUID;

public class UniqueEventIdGenerator {
    public static Long nextUniqueId() {
        return UUID.randomUUID().getLeastSignificantBits();
    }
}
