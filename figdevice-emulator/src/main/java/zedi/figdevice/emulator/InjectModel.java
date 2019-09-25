package zedi.figdevice.emulator;

import zedi.pacbridge.net.core.NetworkEventDispatcherManager;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class InjectModel extends AbstractModule {
    @Override 
    protected void configure() {
      bind(NetworkEventDispatcherManager.class)
          .in(Singleton.class);
    }
}
