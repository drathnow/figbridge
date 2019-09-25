package zedi.pacbridge.web.services;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.cache.NoCache;

import zedi.pacbridge.app.net.Network;
import zedi.pacbridge.app.services.NetworkService;
import zedi.pacbridge.net.core.NetworkEventDispatcherManager;
import zedi.pacbridge.utl.concurrent.ThreadDumper;

@Path("/bridge")
public class Bridge{
//    @Inject
//    private NetworkEventDispatcherManager dispatcherManager;
//    @Inject
//    private NetworkService networkService;
//
//    @GET
//    @Path("/avgscantime")
//    @Produces(MediaType.APPLICATION_JSON) 
//    @NoCache
//    public Map<String, Double> allMembers() {
//        return dispatcherManager.getAverageScanTimes()  ;
//    }   
//
//    @GET
//    @Path("/stat")
//    @Produces(MediaType.APPLICATION_JSON) 
//    @NoCache
//    public Map<String, Map<String, Object>> status() {
//        Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();
//        Map<String, Object> dispatcherMap = new HashMap<String, Object>();
//        dispatcherMap.put("dspCount", dispatcherManager.getNumberOfDispatchers());
//        dispatcherMap.put("avgscantime", dispatcherManager.getAverageScanTimes());
//        dispatcherMap.put("avgrqdepth", dispatcherManager.getRequestQueueDepth());
//        result.put("dspatcher", dispatcherMap);
//        
//        Map<String, Object> connectionMap = new HashMap<String, Object>();
//        int connectionCount = 0;
//        for (Network network : networkService.getNetworks())
//            connectionCount += network.currentConnectionCount();
//        connectionMap.put("concount", connectionCount);
//        result.put("network", dispatcherMap);
//        return result;
//    }
//    
//    @GET
//    @Path("/threaddump")
//    @Produces(MediaType.TEXT_PLAIN) 
//    @NoCache
//    public String threadDump() {
//        return ThreadDumper.threadDump();
//    } 

}
