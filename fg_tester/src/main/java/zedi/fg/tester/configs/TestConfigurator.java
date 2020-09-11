package zedi.fg.tester.configs;

import zedi.pacbridge.zap.messages.FieldTypeLibrary;
import zedi.pacbridge.zap.messages.TimedEventType;

public class TestConfigurator
{
	private FieldTypeLibrary fieldTypeLibrary;
	private TestConfigurationSetupCoordinator setupCoordinator;
	
	public TestConfigurator(FieldTypeLibrary fieldTypeLibrary, TestConfigurationSetupCoordinator setupCoordinator)
	{
		this.fieldTypeLibrary = fieldTypeLibrary;
		this.setupCoordinator = setupCoordinator;
	}
	
	public void setupModbusTestConfiguration()
	{
		ConfigurationSetup config =  new ModbusTestConfiguration(fieldTypeLibrary);
		setupCoordinator.submitConfigurationSetup(config);
	}

    public void setupAIDITestConfiguration()
    {
        ConfigurationSetup config =  new AIDITestConfiguration(fieldTypeLibrary);
        setupCoordinator.submitConfigurationSetup(config);
    }

    public void setupReportSystemIOsTestConfiguration()
    {
        ConfigurationSetup config =  new SystemIOPointsReportConfiguration(fieldTypeLibrary);
        setupCoordinator.submitConfigurationSetup(config);
    }

    public void addEvent(String name, TimedEventType eventType, Integer startTime, Integer interval, Integer duration, Integer pollsetId)
    {
        ConfigurationSetup config = new AddEventConfiguration(fieldTypeLibrary, name, eventType, startTime, interval, duration, pollsetId);
        setupCoordinator.submitConfigurationSetup(config);
    }

    public void deleteEvent(Integer id)
    {
        ConfigurationSetup config = new DeleteEventConfiguration(fieldTypeLibrary, id);
        setupCoordinator.submitConfigurationSetup(config);
    }

    public void addSite(String name)
    {
        ConfigurationSetup config = new AddSiteConfiguration(fieldTypeLibrary, name);
        setupCoordinator.submitConfigurationSetup(config);
    }

    public void setupSNFTestConfiguration()
    {
        ConfigurationSetup config = new StoreAndForwardTestConfiguration(fieldTypeLibrary);
        setupCoordinator.submitConfigurationSetup(config);
    }

    public void setupA100TestConfiguration()
    {
        ConfigurationSetup config = new A1000TestConfiguration(fieldTypeLibrary);
        setupCoordinator.submitConfigurationSetup(config);
    }
}
