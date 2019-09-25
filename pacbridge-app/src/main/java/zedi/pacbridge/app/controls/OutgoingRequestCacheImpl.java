package zedi.pacbridge.app.controls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.DependsOn;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.lucene.search.Query;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.infinispan.Cache;
import org.infinispan.query.CacheQuery;
import org.infinispan.query.Search;
import org.infinispan.query.SearchManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.utl.SiteAddress;

@Stateless
@DependsOn("CacheProvider")
public class OutgoingRequestCacheImpl implements OutgoingRequestCache {
    private static final Logger logger = LoggerFactory.getLogger(OutgoingRequestCacheImpl.class.getName());

    private Cache<String, OutgoingRequest> outgoingRequestCache;
    
    public OutgoingRequestCacheImpl() {
    }

    @Inject
    public OutgoingRequestCacheImpl(Cache<String, OutgoingRequest> outgoingRequestCache) {
        this.outgoingRequestCache = outgoingRequestCache;
    }

    @Override
    public void storeOutgoingRequest(OutgoingRequest outgoingRequest) {
        logger.trace("Storing outgoing request for " + outgoingRequest.toString());
        outgoingRequestCache.put(outgoingRequest.getRequestId(), outgoingRequest);
    }

    @Override
    public boolean updateOutgoingRequest(OutgoingRequest outgoingRequest) {
        logger.trace("Updating outgoing request for " + outgoingRequest.toString());
        return outgoingRequestCache.replace(outgoingRequest.getRequestId(), outgoingRequest) != null;
    }
    
    @Override
    public OutgoingRequest outgoingRequestForRequestId(String requestId) {
        return outgoingRequestCache.get(requestId);
    }

    @Override
    public boolean deleteOutgoingRequestWithRequestId(String requestId) {
        logger.trace("Deleting outgoing request with requestID " + requestId);
        return outgoingRequestCache.remove(requestId) != null;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Collection<OutgoingRequest> outgoingRequestsForSiteAddress(SiteAddress siteAddress) {
        SearchManager searchManager = Search.getSearchManager(outgoingRequestCache);
        QueryBuilder queryBuilder = searchManager.buildQueryBuilderForClass(OutgoingRequest.class).get();
        Query luceneQuery = queryBuilder
                                .keyword()
                                .onField("nuid")
                                .matching(siteAddress.getAddress())
                                .createQuery();
        CacheQuery query = searchManager.getQuery(luceneQuery, OutgoingRequest.class);
        List<? extends Object> list = query.list();
        return (Collection<OutgoingRequest>)list;
    }

    @Override
    public boolean hasOutgoingRequests(SiteAddress siteAddress) {
        return outgoingRequestsForSiteAddress(siteAddress).size() > 0;
    }

    @Override
    public Collection<OutgoingRequest> allOutgoingRequests() {
        List<OutgoingRequest> requests = new ArrayList<>();
        for (OutgoingRequest request : outgoingRequestCache.values())
            requests.add(request);
        return requests;
    }
}
