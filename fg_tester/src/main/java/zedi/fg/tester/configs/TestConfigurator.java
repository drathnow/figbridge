package zedi.fg.tester.configs;

import zedi.pacbridge.zap.messages.FieldTypeLibrary;

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
}
