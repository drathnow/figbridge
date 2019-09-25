package zedi.pacbridge.web.services;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.cache.NoCache;

import zedi.pacbridge.app.monitor.SiteStatistics;
import zedi.pacbridge.app.monitor.SiteStatisticsCache;
import zedi.pacbridge.web.dtos.SiteDTO;

@Path("/sites")
public class Sites{
    @Inject
    private SiteStatisticsCache cache;

    @GET
    @Path("/{id}")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON) 
    public SiteDTO getSiteInfo(@PathParam("id") String nuid) {
        SiteDTO site = null;
        SiteStatistics stats = cache.siteStatisticsForSite(nuid);
        if (stats != null) {
            site = new SiteDTO(nuid);
            site.setConCnt(stats.getConnectionCount());
            site.setDupRptsCnt(stats.getDuplicateReportCount());
            site.setRdngCnt(stats.getReadingsCount());
            site.setRptCnt(stats.getReportCount());
            site.setControls(stats.getControlMap());
        }
        return site;
    }
}
