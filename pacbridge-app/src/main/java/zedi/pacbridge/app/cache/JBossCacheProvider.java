package zedi.pacbridge.app.cache;

import java.io.File;

import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.Index;
import org.infinispan.configuration.cache.SingleFileStoreConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.GenericTransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zedi.pacbridge.app.annotations.ClusterName;
import zedi.pacbridge.app.controls.ExpirationManager;
import zedi.pacbridge.app.controls.OutgoingRequest;
import zedi.pacbridge.app.controls.OutgoingRequestCache;
import zedi.pacbridge.app.controls.OutgoingRequestCacheCreateEventListener;
import zedi.pacbridge.app.devices.Device;
import zedi.pacbridge.app.monitor.BridgeStatistics;
import zedi.pacbridge.app.monitor.LostConnectionTracker;
import zedi.pacbridge.app.monitor.SiteStatistics;
import zedi.pacbridge.utl.IntegerSystemProperty;
import zedi.pacbridge.utl.StringPropertyReplacer;

@Singleton (name = "CacheProvider")
@Startup
@DependsOn("BridgeConfiguration")
public class JBossCacheProvider implements CacheProvider {
    private static final Logger logger = LoggerFactory.getLogger(JBossCacheProvider.class.getName());
    
    public static final String CACHESTORE_PATH_PROPERTY_NAME = "pacbridge.cacheStorePath";
    public static final String DEFAULT_CACHE_STORE_PATH = "./cacheStore";

    private String clusterName;
    private DefaultCacheManager cacheManager;

    @Inject
    public JBossCacheProvider(@ClusterName String clusterName) {
        this.clusterName = clusterName;
        postConstruct();
    }

    @Produces
    @Override
    public Cache<String, OutgoingRequest> getOutgoingRequestCache() {
        return cacheManager.getCache(OUTGOING_REQUEST_CACHE_NAME);
    }
    
    @Produces
    @Override
    public Cache<String, LostConnectionTracker> getLostConnectionTrackerCache() {
        return cacheManager.getCache(LOST_CONNECTION_TRACKER_CACHE_NAME);
    }

    @Produces
    @Override 
    public Cache<String, SiteStatistics> getSiteStatisticsCache() {
        return cacheManager.getCache(SITE_STATISTIC_CACHE_NAME);
    }

    @Produces
    @Override
    public Cache<String, BridgeStatistics> getBridgeStatisticsCache() {
        return cacheManager.getCache(BRIDGE_STATISTICS_CACHE_NAME);
    }
    
    @Produces
    @Override
    public Cache<String, Device> getDeviceCache() {
        return cacheManager.getCache(DEVICE_CACHE_NAME);
    }
    
    @Produces
    @Override
    public Cache<String, Long> getInterestingSitesCache() {
        return cacheManager.getCache(INTERESTING_SITES_CACHE_NAME);
    }
    
    private void postConstruct() {
        GlobalConfiguration glob = new GlobalConfigurationBuilder()
                .transport()
                    .defaultTransport()
                    .clusterName(clusterName)
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
        
        cacheManager = new DefaultCacheManager(glob, defaultConfig);
        
        cacheManager.start();
        
        createOutgoingRequestCache();
        createDeviceCache();
        createBridgeStatisticsCache();
        createLostConnectionTrackerCache();
        createSiteStatisticsCache();
        createInterestingSitesCache();
    }
    
    private void createOutgoingRequestCache() {
        String cacheStorePath = getCacheStorePath();
        IntegerSystemProperty expirationMinutes = new IntegerSystemProperty(OutgoingRequestCache.OUTGOING_REQUEST_TIMEOUT_PROPERTY_NAME, OutgoingRequestCache.DEFAULT_REQUEST_EXPIRATION_MINUTES);
        Configuration requestCacheConfig = new ConfigurationBuilder()
                                                    .clustering()
                                                    .cacheMode(CacheMode.REPL_SYNC)
                                                    .persistence()
                                                        .addStore(SingleFileStoreConfigurationBuilder.class)
                                                        .location(cacheStorePath)
                                                    .indexing()
                                                        .index(Index.LOCAL)
                                                    .build();
        
        cacheManager.defineConfiguration(OUTGOING_REQUEST_CACHE_NAME, requestCacheConfig);
        
        logger.info("*=*=*=*=*=*=*=*=*=*=*=*=*= Getting cache *=*=*=*=*=*=*=*=*=*=*=*=*=");
        Cache<String, OutgoingRequest> cache = cacheManager.getCache(OUTGOING_REQUEST_CACHE_NAME);
        logger.info("*=*=*=*=*=*=*=*=*=*=*=*=*= Got Cache *=*=*=*=*=*=*=*=*=*=*=*=*=");
        
        ExpirationManager expirationManager = new ExpirationManager(cache, expirationMinutes.currentValue().longValue());
        OutgoingRequestCacheCreateEventListener createListener = new OutgoingRequestCacheCreateEventListener();

        cache.addListener(expirationManager);
        cache.addListener(createListener);
        
        logger.debug("========================================");
        logger.debug("OutgoingRequestCache Initialized");
        logger.debug("            Cache: " + OUTGOING_REQUEST_CACHE_NAME);
        logger.debug("  Cache Store Dir: " + cacheStorePath);
        logger.debug("      Expiry Time: " + expirationMinutes.currentValue() + " Minutes");
        logger.debug("========================================");
    }
    
    private void createDeviceCache() {
        String cacheStorePath = getCacheStorePath();
        Configuration cacheConfig =  new ConfigurationBuilder()
                                            .clustering()
                                            .cacheMode(CacheMode.REPL_SYNC)
                                            .persistence()
                                                .addStore(SingleFileStoreConfigurationBuilder.class)
                                                .location(cacheStorePath)
                                            .indexing() 
                                                .index(Index.LOCAL)
                                            .transaction()
                                                .transactionMode(TransactionMode.TRANSACTIONAL)
                                                .autoCommit(true)
                                                .lockingMode(LockingMode.OPTIMISTIC)
                                                .transactionManagerLookup(new GenericTransactionManagerLookup())
                                            .build();
            cacheManager.defineConfiguration(DEVICE_CACHE_NAME, cacheConfig);
        
    }
    
    private void createSiteStatisticsCache() {
        Configuration cacheConfig = new ConfigurationBuilder()
                                            .clustering()
                                                .cacheMode(CacheMode.REPL_ASYNC)
                                            .transaction()
                                                .transactionMode(TransactionMode.TRANSACTIONAL)
                                                .autoCommit(true)
                                                .lockingMode(LockingMode.OPTIMISTIC)
                                                .transactionManagerLookup(new GenericTransactionManagerLookup())
                                            .locking()
                                                .isolationLevel(IsolationLevel.REPEATABLE_READ)
                                            .indexing()
                                                .index(Index.LOCAL)
                                                // Cache is being kept in RAM because it is non-persistent.
                                                .addProperty("default.directory_provider", "ram")
                                            .build();
        cacheManager.defineConfiguration(SITE_STATISTIC_CACHE_NAME, cacheConfig);
    }
    
    private void createBridgeStatisticsCache() {
        Configuration cacheConfig = new ConfigurationBuilder()
                                            .clustering()
                                                .cacheMode(CacheMode.REPL_ASYNC)
                                            .transaction()
                                                .transactionMode(TransactionMode.TRANSACTIONAL)
                                                .autoCommit(true)
                                                .lockingMode(LockingMode.OPTIMISTIC)
                                                .transactionManagerLookup(new GenericTransactionManagerLookup())
                                            .locking()
                                                .isolationLevel(IsolationLevel.REPEATABLE_READ)
                                            .indexing()
                                                .index(Index.LOCAL)
                                                // Cache is being kept in RAM because it is non-persistent.
                                                .addProperty("default.directory_provider", "ram")
                                            .build();
        cacheManager.defineConfiguration(BRIDGE_STATISTICS_CACHE_NAME, cacheConfig);
    }
    
    private void createLostConnectionTrackerCache() {
        Configuration cacheConfig = new ConfigurationBuilder()
                                            .clustering()
                                                .cacheMode(CacheMode.REPL_ASYNC)
                                            .transaction()
                                                .transactionMode(TransactionMode.TRANSACTIONAL)
                                                .autoCommit(true)
                                                .lockingMode(LockingMode.OPTIMISTIC)
                                                .transactionManagerLookup(new GenericTransactionManagerLookup())
                                            .locking()
                                                .isolationLevel(IsolationLevel.REPEATABLE_READ)
                                            .indexing()
                                                .index(Index.LOCAL)
                                                .addProperty("default.directory_provider", "ram")
                                            .build();
        cacheManager.defineConfiguration(LOST_CONNECTION_TRACKER_CACHE_NAME, cacheConfig);
    }
    
    private void createInterestingSitesCache() {
        Configuration cacheConfig = new ConfigurationBuilder()
                                        .clustering()
                                            .cacheMode(CacheMode.REPL_ASYNC)
                                        .transaction()
                                            .transactionMode(TransactionMode.TRANSACTIONAL)
                                            .autoCommit(true)
                                            .lockingMode(LockingMode.OPTIMISTIC)
                                            .transactionManagerLookup(new GenericTransactionManagerLookup())
                                        .locking()
                                            .isolationLevel(IsolationLevel.REPEATABLE_READ)
                                        .indexing()
                                            .index(Index.LOCAL)
                                            // Cache is being kept in RAM because it is non-persistent.
                                            .addProperty("default.directory_provider", "ram")
                                        .build();
        cacheManager.defineConfiguration(INTERESTING_SITES_CACHE_NAME, cacheConfig);
    }
    
    private String getCacheStorePath() {
        String pathname = System.getProperty(CACHESTORE_PATH_PROPERTY_NAME, System.getProperty("jboss.server.data.dir", DEFAULT_CACHE_STORE_PATH));
        pathname = StringPropertyReplacer.replaceProperties(pathname);
        File file = new File(pathname);
        if (file.exists()) {
            if (file.isDirectory() == false)
                throw new RuntimeException("Cachestore path is not a directory or cannot be created: " + file.getAbsolutePath());
        } else if (file.mkdirs() == false)
            throw new RuntimeException("Cachestore path is not a directory or cannot be created: " + file.getAbsolutePath());
        return file.getAbsolutePath();
    }    
}
