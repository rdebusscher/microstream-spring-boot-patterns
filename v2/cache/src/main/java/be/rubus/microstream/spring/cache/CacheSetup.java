package be.rubus.microstream.spring.cache;

import one.microstream.cache.types.CacheConfiguration;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.stereotype.Component;

import javax.cache.CacheManager;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

/**
 * Configuration of the Spring Cache integrations through JCache (as MicroStream implements JCache).
 */
@Component
public class CacheSetup implements JCacheManagerCustomizer {


    private final EmbeddedStorageManager storageManager;

    public CacheSetup(EmbeddedStorageManager storageManager) {
        // Retrieve the MicroStream Manager as Spring Bean
        this.storageManager = storageManager;
    }


    @Override
    public void customize(CacheManager cacheManager) {
        // Define the caches and their Expiration time
        defineCache(cacheManager, "countries", Duration.ONE_MINUTE);

        // This manual one doesn't work (see NamesService)
        defineCache(cacheManager, "manual", Duration.ONE_HOUR);
    }


    private void defineCache(CacheManager cacheManager, String cacheName, Duration duration) {
        CacheConfiguration<?, ?> configuration = CacheConfiguration
                .Builder(Object.class, Object.class, cacheName, storageManager)
                .expiryPolicyFactory(CreatedExpiryPolicy.factoryOf(duration))
                .build();

        cacheManager.createCache(cacheName, configuration);
    }

}