package be.rubus.microstream.spring.cache.service;

import org.springframework.stereotype.Service;

import javax.cache.Cache;
import javax.cache.CacheManager;
import java.util.ArrayList;
import java.util.List;

@Service
public class NamesService {

    private static final String KEY = "data";

    private final Cache<String, List<String>> namesCache;

    public NamesService(CacheManager cacheManager) {

        namesCache = cacheManager.getCache("manual");
        namesCache.putIfAbsent(KEY, new ArrayList<>());
    }

    public List<String> getNames() {
        return namesCache.get(KEY);
    }

    public void resetNames() {
        performOperation(List::clear);
    }

    public void addName(String name) {
        performOperation((names) -> names.add(name));
    }

    public void deleteName(String name) {
        performOperation((names) -> names.remove(name));
    }

    private void performOperation(CacheOperation cacheOperation) {
        List<String> names = getNames();
        cacheOperation.operation(names);
        // Investigate the reason why this doesn't work!
        namesCache.put(KEY, names);
    }

    @FunctionalInterface
    interface CacheOperation {
        void operation(List<String> names);
    }
}
