package zedi.pacbridge.app.zap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.events.zios.ConfigureUpdateEvent;
import zedi.pacbridge.app.publishers.EventHandler;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.zap.ZapConfigurationUpdateHandler;
import zedi.pacbridge.zap.messages.ConfigureUpdateMessage;

@Stateless
@EJB(name = ZapConfigurationUpdateHandler.JNDI_NAME, beanInterface = ZapConfigurationUpdateHandler.class)
public class ConfigurationUpdateHandler implements ZapConfigurationUpdateHandler {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationUpdateHandler.class.getName());
    
    private EventHandler eventPublisher;
    
    @Inject
    public ConfigurationUpdateHandler(EventHandler eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public boolean didProcessConfigurationUpdate(SiteAddress siteAddress, ConfigureUpdateMessage updateMessage) {
        ConfigureUpdateEvent updateEvent = new ConfigureUpdateEvent(siteAddress, updateMessage.getObjectTyp(), updateMessage.getActions());
        try {
            eventPublisher.publishEvent(updateEvent);
            return true;
        } catch (Exception e) {
            logger.error("Unable to publish ConfigureUpdate event", e);
            return false;
        }
    }
}
