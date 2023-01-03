package be.rubus.microstream.spring.cache;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * We require a custom Cache Key generation mechanism when we use MicroStream as 'backend' for Cache storage.
 * The default {@link org.springframework.cache.interceptor.SimpleKey} implementation has its hash
 * stored in a transient variable by the constructor. And thus this hash is not stored by MicroStream.
 * When restarting, keys can't be matched due the missing hash and thus cache appears to be empty, not able to use stored values.
 * <p>
 * See also {@link CacheConfig}.
 */
public class CustomKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        // Key should only depend on parameters so that @Cacheable and @CacheEvict annotated methods result in same key
        return "Key" + StringUtils.arrayToDelimitedString(params, "_");
    }
}
