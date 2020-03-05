package zedi.fg.tester;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import zedi.pacbridge.utl.NotificationCenter;

public class InjectModel extends AbstractModule
{
	private static final Logger logger = LoggerFactory.getLogger(InjectModel.class.getName());

	@Override
	protected void configure()
	{
		bind(NotificationCenter.class)
		                .in(Scopes.SINGLETON);
	}
}
