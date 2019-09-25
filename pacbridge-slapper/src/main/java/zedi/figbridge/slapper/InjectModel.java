package zedi.figbridge.slapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.figbridge.slapper.config.ConfigParam;
import zedi.figbridge.slapper.config.Configuration;
import zedi.figbridge.slapper.config.ConfigurationValidator;
import zedi.figbridge.slapper.utl.DeviceConglomerator;
import zedi.pacbridge.msg.JmsImplementor;
import zedi.pacbridge.msg.annotations.JmsImplParam;
import zedi.pacbridge.net.core.NetworkEventDispatcherManager;
import zedi.pacbridge.utl.JDomUtilities;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.wsmq.WsmqJmsImplementator;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;


public class InjectModel extends AbstractModule {
    private static final Logger logger = LoggerFactory.getLogger(InjectModel.class.getName());
    
    @Override
    protected void configure() {
        bind(BridgeSlapper.class)
            .in(Singleton.class);
        
        bind(NotificationCenter.class)
            .in(Singleton.class);
        
        bind(NetworkEventDispatcherManager.class)
            .in(Singleton.class);
        
        bind(DeviceConglomerator.class)
            .in(Singleton.class);
        
        bind(JmsImplementor.class)
            .annotatedWith(JmsImplParam.class)
            .to(WsmqJmsImplementator.class)
            .in(Scopes.SINGLETON);
        
        bind(Configuration.class)
            .annotatedWith(ConfigParam.class)
            .to(Configuration.class)
            .in(Scopes.SINGLETON);
    }

    @Provides
    private static Configuration configuration() {
        ConfigurationValidator validator = new ConfigurationValidator();
        try {
            File configFile = new File(Configuration.getConfigurationFilename());
            InputStream inputStream = new FileInputStream(configFile);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            String xmlConfig = new String(bytes);
            validator.validateXmlConfig(xmlConfig);
            Element configElement = JDomUtilities.elementForXmlString(xmlConfig);
            return Configuration.configurationFromElement(configElement);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load configuration", e);
        }
    }

    @Provides
    private static WsmqJmsImplementator jmsImplementor(@ConfigParam Configuration configuration) {
        WsmqJmsImplementator implementator = new WsmqJmsImplementator();
        implementator.setQueueManagerName(configuration.getJmsQueueManagerName());
        implementator.setHostName(configuration.getJmsHostName());
        implementator.setClientId(configuration.getJmsClientId());
        try {
            implementator.initialize();
        } catch (Exception ex) {
            throw new RuntimeException("Unable to load JMS Stuff", ex);
        }
        return implementator;
    }

}
