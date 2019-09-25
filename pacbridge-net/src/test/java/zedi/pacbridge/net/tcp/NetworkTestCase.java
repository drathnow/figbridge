package zedi.pacbridge.net.tcp;

import java.net.InetSocketAddress;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import zedi.pacbridge.net.ConnectionInformation;
import zedi.pacbridge.net.NuidSiteAddressMatcher;
import zedi.pacbridge.test.BaseTestCase;

public abstract class NetworkTestCase extends BaseTestCase {

    protected NuidSiteAddressMatcher matchesSiteAddressWithNuid(String nuid, int networkNumber) {
        return new NuidSiteAddressMatcher(nuid, networkNumber);
    }

    protected InetSocketAddressMatcher matchesRemoteAddressToConnectionInformation(ConnectionInformation connectionInformation) {
        return new InetSocketAddressMatcher(connectionInformation);
    }

    protected class InetSocketAddressMatcher extends BaseMatcher<InetSocketAddress> {

        private ConnectionInformation connectionInformation;
        
        InetSocketAddressMatcher(ConnectionInformation connectionInformation) {
            this.connectionInformation = connectionInformation;
        }

        @Override
        public boolean matches(Object object) {
            InetSocketAddress address = (InetSocketAddress)object;
            return address.getAddress().getHostAddress() == connectionInformation.getSiteAddress().getAddress()
                    || address.getPort() == connectionInformation.getRemotePortNumber();
        }

        @Override
        public void describeTo(Description arg0) {
        }
        
    }
}
