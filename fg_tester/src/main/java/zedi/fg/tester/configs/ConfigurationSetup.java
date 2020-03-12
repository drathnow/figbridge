package zedi.fg.tester.configs;

import zedi.pacbridge.zap.messages.ConfigureControl;
import zedi.pacbridge.zap.messages.ConfigureResponseAckDetails;

public interface ConfigurationSetup
{

	void handleConfigurationResponse(ConfigureResponseAckDetails ackDetails);

	ConfigureControl nextConfigureControl();

}