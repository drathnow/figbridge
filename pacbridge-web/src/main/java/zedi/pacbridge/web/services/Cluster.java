package zedi.pacbridge.web.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.cache.NoCache;
import org.jgroups.Address;

import zedi.pacbridge.app.services.ClusterService;
import zedi.pacbridge.web.dtos.ClusterMemberDTO;

@Path("/cluster")
public class Cluster {
    @Inject
    private ClusterService clusterService;

    @GET
    @Path("/members")
    @Produces(MediaType.APPLICATION_JSON) 
    @NoCache
    public Collection<ClusterMemberDTO> allMembers() {
        List<Address> addresses = clusterService.getClusterMembers();
        Collection<ClusterMemberDTO> clusterMembers = new ArrayList<>();
        for (Address address : addresses)
            clusterMembers.add(new ClusterMemberDTO(address.toString()));
        return clusterMembers;
    }   
}
