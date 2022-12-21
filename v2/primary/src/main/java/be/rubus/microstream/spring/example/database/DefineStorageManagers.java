package be.rubus.microstream.spring.example.database;

import one.microstream.integrations.spring.boot.types.config.StorageManagerProvider;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefineStorageManagers {

    private final StorageManagerProvider provider;

    public DefineStorageManagers(StorageManagerProvider provider) {
        this.provider = provider;
    }

    @Bean(destroyMethod = "shutdown")
    @Qualifier("secondary")
    public EmbeddedStorageManager getGreenManager() {
        return provider.get("secondary");
    }

}
