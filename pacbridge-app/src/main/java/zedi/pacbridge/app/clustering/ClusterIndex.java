package zedi.pacbridge.app.clustering;

import java.util.UUID;

public class ClusterIndex {
    public static long newIndex() {
        return UUID.randomUUID().getLeastSignificantBits();
    }
}
