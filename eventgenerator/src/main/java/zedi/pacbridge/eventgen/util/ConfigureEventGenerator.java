package zedi.pacbridge.eventgen.util;

import zedi.pacbridge.app.events.zios.ConfigureEvent;
import zedi.pacbridge.utl.SiteAddress;

public interface ConfigureEventGenerator {
    public ConfigureEvent eventForSiteAddress(SiteAddress address);
}
