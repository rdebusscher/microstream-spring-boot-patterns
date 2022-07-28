package be.rubus.microstream.spring.example;

import be.rubus.microstream.spring.example.database.Root;
import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.types.StorageManager;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DataConfiguration {

    private final Environment environment;
    private final DataInit dataInit;

    public DataConfiguration(Environment environment, DataInit dataInit) {
        this.environment = environment;
        this.dataInit = dataInit;
    }

    @Bean(destroyMethod = "shutdown")
    public StorageManager defineStorageManager() {

        EmbeddedStorageFoundation<?> embeddedStorageFoundation = embeddedStorageFoundation(environment);
        // do additional configuration
        /*
        storageFoundation.onConnectionFoundation(BinaryHandlersJDK8::registerJDK8TypeHandlers);
        storageFoundation.onConnectionFoundation(f -> f.registerCustomTypeHandler());
        storageFoundation.onConnectionFoundation(f -> f
                        .getCustomTypeHandlerRegistry()
                        .registerLegacyTypeHandler(
                                new LegacyTypeHandlerBook()
                        )
        );

         */

        StorageManager storageManager = embeddedStorageFoundation.start();

        // Check Root available within StorageManager
        Root root = (Root) storageManager.root();
        boolean initRequired = false;
        if (root == null) {
            root = new Root();
            initRequired = true;
        }
        // Prep Root
        root.setStorageManager(storageManager);

        // Init 'database' with some data
        if (initRequired) {
            dataInit.init(root, storageManager);
            storageManager.setRoot(root);
            storageManager.storeRoot();
        }
        return storageManager;
    }

    private EmbeddedStorageFoundation<?> embeddedStorageFoundation(Environment env) {
        String configLocation = env.getProperty("one.microstream.config");

        if (configLocation == null) {
            throw new BeanCreationException("Unable to create StorageManager as the configuration property 'one.microstream.config' could not be resolved");
        }
        return EmbeddedStorageConfiguration.load(configLocation)
                .createEmbeddedStorageFoundation();

    }
}
