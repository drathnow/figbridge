package zedi.pacbridge.web.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.cache.NoCache;

import zedi.pacbridge.utl.concurrent.ThreadDumper;

@Path("/threads")
public class Threads {

    @GET
    @Path("/dump")
    @Produces(MediaType.TEXT_PLAIN) 
    @NoCache
    public String dump() {
        return ThreadDumper.threadDump();
    }   
}
