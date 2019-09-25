package zedi.pacbridge.app.controls.zap;

import javax.ejb.Stateless;
import javax.inject.Inject;

import zedi.pacbridge.app.cache.InterestingSitesCache;
import zedi.pacbridge.app.controls.ControlResponseStrategy;
import zedi.pacbridge.app.controls.ControlResponseStrategyFactory;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.net.Control;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.ZapMessageType;
import zedi.pacbridge.zap.messages.ConfigureControl;
import zedi.pacbridge.zap.messages.DemandPollControl;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.OtadRequestControl;
import zedi.pacbridge.zap.messages.ScrubControl;
import zedi.pacbridge.zap.messages.WriteIoPointsControl;


@Stateless
public class ZapControlResponseStrategyFactory implements ControlResponseStrategyFactory {
    private FieldTypeLibrary fieldTypeLibrary;
    private EventHandler eventPublisher;
    private InterestingSitesCache interestingSitesCache;
    
    public ZapControlResponseStrategyFactory() {
    }

    @Inject
    public ZapControlResponseStrategyFactory(FieldTypeLibrary fieldTypeLibrary, EventHandler eventPublisher, InterestingSitesCache interestingSitesCache) {
        this.fieldTypeLibrary = fieldTypeLibrary;
        this.eventPublisher = eventPublisher;
        this.interestingSitesCache = interestingSitesCache;
    }

    public ControlResponseStrategy responseStrategyForControl(Control control, SiteAddress siteAddress) {
        switch (control.messageType().getNumber()) {
            case ZapMessageType.CONFIGURE_NUMBER :
                return new ConfigureResponseStrategy((ConfigureControl)control, fieldTypeLibrary, siteAddress, eventPublisher, interestingSitesCache);
            case ZapMessageType.DEMAND_POLL_NUMBER :
                return new DemandPollControlResponseStrategy((DemandPollControl)control, siteAddress, eventPublisher, interestingSitesCache);
            case ZapMessageType.WRITE_IO_POINT_NUMBER :
                return new WriteIoPointsControlResponseStrategy((WriteIoPointsControl)control, siteAddress, eventPublisher, interestingSitesCache);
            case ZapMessageType.SCRUB_NUMBER :
                return new ScrubControlResponseStrategy((ScrubControl)control, siteAddress, eventPublisher, interestingSitesCache);
            case ZapMessageType.OTAD_REQUEST_NUMBER :
                return new OtadRequestControlResponseStrategy((OtadRequestControl)control, siteAddress, eventPublisher, interestingSitesCache);
            default :
                return null;
        }
    }
}
