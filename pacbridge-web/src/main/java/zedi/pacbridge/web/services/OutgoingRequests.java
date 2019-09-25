package zedi.pacbridge.web.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.cache.NoCache;

import zedi.pacbridge.app.controls.OutgoingRequest;
import zedi.pacbridge.app.controls.OutgoingRequestCache;
import zedi.pacbridge.app.services.OutgoingRequestDeletionService;
import zedi.pacbridge.web.dtos.OutgoingRequestDTO;


@Path("/outgoingrequests")
public class OutgoingRequests {
    @Inject
    private OutgoingRequestCache cache;
    
    @Inject
    private OutgoingRequestDeletionService deletionService;
    
    @GET
    @Path("/")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON) 
    public Collection<OutgoingRequestDTO> allRequests() {
        Collection<OutgoingRequest> ors = cache.allOutgoingRequests();
        if (ors.size() == 0)
            return Collections.emptyList();
        List<OutgoingRequestDTO> dtos = new ArrayList<OutgoingRequestDTO>();
        for (Iterator<OutgoingRequest> iter = ors.iterator(); iter.hasNext(); ) 
            dtos.add(new OutgoingRequestDTO(iter.next()));
        return dtos;
    }

    @GET
    @Path("/{id}")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON) 
    public OutgoingRequestDTO getControlInfo(@PathParam("id") String requestId) {
        OutgoingRequest or = cache.outgoingRequestForRequestId(requestId);
        return (or == null) ? null : new OutgoingRequestDTO(or); 
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON) 
    public String deleteRequest(@PathParam("id") String requestId) {
        StringBuilder stringBuilder = new StringBuilder("{status: '");
        if (deletionService.deleteOutgoingRequestWithRequestId(requestId))
            stringBuilder.append("Success'}");
        else
            stringBuilder.append("Failure'}");
        return stringBuilder.toString();
    }
}
