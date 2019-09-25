import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.lucene.search.Query;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.Index;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.query.CacheQuery;
import org.infinispan.query.Search;
import org.infinispan.query.SearchManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.GenericTransactionManagerLookup;

import zedi.pacbridge.app.cache.CacheProvider;
import zedi.pacbridge.app.controls.OutgoingRequest;
import zedi.pacbridge.utl.NuidSiteAddress;
import zedi.pacbridge.utl.SiteAddress;
import zedi.pacbridge.utl.Utilities;

public class InfinispanTest {
    
    
    private static void printJarfile(Class<?> clazz) {
        String filename = Utilities.jarFilenameContainingClass(clazz);
        filename = filename.substring(0, filename.lastIndexOf('!'));
        System.out.println("-----------------> " + clazz.getName() + ": " + filename);
    }

    public static void main(String[] args) {
        Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%m%n")));
        System.out.println("Starting...");
        System.out.println("---------> Jarfile: " + Utilities.jarFilenameContainingClass(Indexed.class));

        printJarfile(org.apache.lucene.search.Query.class);
        printJarfile(org.hibernate.search.query.dsl.QueryBuilder.class);
        printJarfile(org.infinispan.Cache.class);
        printJarfile(org.infinispan.query.CacheQuery.class);
        printJarfile(org.infinispan.query.Search.class);
        printJarfile(org.infinispan.query.SearchManager.class);
        printJarfile(org.hibernate.search.annotations.Field.class);
        printJarfile(org.hibernate.search.annotations.FieldBridge.class);
        printJarfile(org.hibernate.search.annotations.Indexed.class);
        printJarfile(org.hibernate.search.annotations.IndexedEmbedded.class);

        
        SiteAddress siteAddress = new NuidSiteAddress("Hello World");
        GlobalConfiguration glob = new GlobalConfigurationBuilder()
                .transport()
//                    .defaultTransport()
//                    .clusterName("Foo")
                .globalJmxStatistics()
                    .allowDuplicateDomains(true)
                .build();

        Configuration defaultConfig = new ConfigurationBuilder()
                        .transaction()
                            .transactionMode(TransactionMode.TRANSACTIONAL)
                            .autoCommit(true)
                            .lockingMode(LockingMode.OPTIMISTIC)
                            .transactionManagerLookup(new GenericTransactionManagerLookup())
                        .build();
        
        DefaultCacheManager cacheManager = new DefaultCacheManager(glob, defaultConfig);
        Configuration requestCacheConfig = new ConfigurationBuilder()
//                                                    .clustering()
//                                                    .cacheMode(CacheMode.REPL_SYNC)
                                                    .indexing()
                                                        .index(Index.LOCAL)
                                                    .build();
        
        cacheManager.defineConfiguration(CacheProvider.OUTGOING_REQUEST_CACHE_NAME, requestCacheConfig);
        
        Cache<String, OutgoingRequest> cache = cacheManager.getCache(CacheProvider.OUTGOING_REQUEST_CACHE_NAME);
        cacheManager.start();
        
        SearchManager searchManager = Search.getSearchManager(cache);
        QueryBuilder queryBuilder = searchManager.buildQueryBuilderForClass(OutgoingRequest.class).get();
        Query luceneQuery = queryBuilder
                                .keyword()
                                .onField("siteAddress")
                                .matching(siteAddress)
                                .createQuery();
        CacheQuery query = searchManager.getQuery(luceneQuery, OutgoingRequest.class);
        List<? extends Object> list = query.list();
        System.out.println("Done...");
    }
}
