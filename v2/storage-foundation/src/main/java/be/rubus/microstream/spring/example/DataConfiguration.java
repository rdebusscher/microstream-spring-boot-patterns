package be.rubus.microstream.spring.example;

import be.rubus.microstream.spring.example.database.Root;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.types.StorageManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataConfiguration {

    private final EmbeddedStorageFoundation<?> storageFoundation;

    private final DataInit dataInit;

    public DataConfiguration(EmbeddedStorageFoundation<?> storageFoundation, DataInit dataInit) {
        this.storageFoundation = storageFoundation;
        this.dataInit = dataInit;
    }

    @Bean(destroyMethod = "shutdown")
    @Primary
    public StorageManager defineStorageManager() {

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

        StorageManager storageManager = storageFoundation.start();

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
}
