package zedi.pacbridge.web.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.cache.NoCache;

import zedi.pacbridge.app.net.Network;
import zedi.pacbridge.app.services.NetworkService;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.web.dtos.ConnectionDTO;
import zedi.pacbridge.web.dtos.ConnectionDTOInfoCollector;

@Path("/connections")
public class Connections {
    private static final Pattern pattern = Pattern.compile("(.*):(\\d*)");
    @Inject
    private NetworkService networkService;
    
    @GET
    @Path("/")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON) 
    public Collection<ConnectionDTO> allConnections() {
        ConnectionDTOInfoCollector collector = new ConnectionDTOInfoCollector();
        List<ConnectionDTO> dtos = new ArrayList<>();
        for (Network network : networkService.getNetworks())
            dtos.addAll(network.connectionInfo(collector));
        return dtos;
    }
    
    @DELETE
    @Path("/{id}")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON) 
    public String deleteConnection(@PathParam("id") String connectionId) {
        StringBuilder stringBuilder = new StringBuilder("{status: '");
        Matcher matcher = pattern.matcher(connectionId);
        if (matcher.matches()) {
            String nuid = matcher.group(1);
            Integer networkNumber = new Integer(matcher.group(2));
            NuidSiteAddress siteAddress = new NuidSiteAddress(nuid, networkNumber);
            Network network = networkService.networkForNetworkNumber(networkNumber);
            if (network == null) {
                stringBuilder.append("Failure'}");
            } else {
                network.removeConnectionWithSiteAddress(siteAddress);
                stringBuilder.append("Success'}");
            }
        } else 
            stringBuilder.append("Failure'}");
        return stringBuilder.toString();
    }    
}
