package be.rubus.microstream.spring.cache;

import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig implements CachingConfigurer {

    public KeyGenerator keyGenerator() {
        return new CustomKeyGenerator();
    }
}
