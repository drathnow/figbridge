package zedi.pacbridge.app.services;


import java.lang.management.ManagementFactory;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.management.ObjectName;

import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.msc.service.ServiceName;
import org.jgroups.Address;
import org.jgroups.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.annotations.ClusterName;

@Singleton
@Startup
public class ClusterService {
    private static Logger logger = LoggerFactory.getLogger(ClusterService.class.getName());
        
    @PostConstruct
    void start() {
        logger.info("Cluster Service starting");
    }

    public List<Address> getClusterMembers() {
        Channel channel = (Channel)CurrentServiceContainer.getServiceContainer().getService(ServiceName.JBOSS.append("jgroups", "channel", "web")).getValue();
        return channel.getView().getMembers();
    }

    @Produces
    @ClusterName
    public String clusterName() {
        try {
            ObjectName serverMBean = new ObjectName("jboss.as:management-root=server");
            String serverGroupName = (String)ManagementFactory.getPlatformMBeanServer().getAttribute(serverMBean, "serverGroup");
            logger.info("JBoss server group name is" + serverGroupName);
            return serverGroupName;
        } catch (Exception e) {
            logger.error("Unable to identify JBoss server-group-name", e);
            return "<UNKNOWN>";
        }
    }
}
