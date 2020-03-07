package zedi.fg.tester;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;

import zedi.fg.tester.configs.TestConfigurator;
import zedi.fg.tester.util.Configuration;
import zedi.pacbridge.app.events.zios.ZiosFieldTypeLibrary;
import zedi.pacbridge.utl.NotificationCenter;
import zedi.pacbridge.zap.messages.FieldTypeLibrary;

public class InjectorModel extends AbstractModule
{
	private static final Logger logger = LoggerFactory.getLogger(InjectorModel.class.getName());

	private static FieldTypeLibrary fieldTypeLibrary = null;
	private static Configuration configuration = null;
	
	@Override
	protected void configure()
	{
		bind(NotificationCenter.class)
			.in(Scopes.SINGLETON);
	}

	@Provides
	private static FieldTypeLibrary loadFieldTypeLibrary() 
	{
		if (fieldTypeLibrary == null)
		{
			InputStream inputStream = FieldTypeLibrary.class.getResourceAsStream("/zedi/pacbridge/zap/messages/FieldTypes.xml");
			assert(inputStream != null);
			fieldTypeLibrary = new ZiosFieldTypeLibrary(inputStream);
		}
        return fieldTypeLibrary;
	}
	
    @Provides
    private static Configuration configuration() {

    	if (configuration == null)
    	{
    		try {
    			FileInputStream fileInputStream = new FileInputStream(new File("fgtester.xml"));
    			configuration = new Configuration();
    			configuration.serialize(fileInputStream);
    		} catch (Exception e) {
    			logger.error("Unable to load configuration", e);
    			configuration = null;
    		}
    	}
    	return configuration;
    }

}
