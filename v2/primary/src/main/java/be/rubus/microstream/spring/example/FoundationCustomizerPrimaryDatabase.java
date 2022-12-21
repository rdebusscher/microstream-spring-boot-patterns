package be.rubus.microstream.spring.example;

import one.microstream.integrations.spring.boot.types.config.EmbeddedStorageFoundationCustomizer;
import one.microstream.integrations.spring.boot.types.config.StorageManagerProvider;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class FoundationCustomizerPrimaryDatabase implements EmbeddedStorageFoundationCustomizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FoundationCustomizerPrimaryDatabase.class);

    @Override
    public void customize(EmbeddedStorageFoundation embeddedStorageFoundation) {
        if (!StorageManagerProvider.PRIMARY_QUALIFIER.equals(embeddedStorageFoundation.getDataBaseName())) {
            // This customizer operates on the Primary database
            return;
        }
        LOGGER.info("(From the App) Additional configuration on the RED EmbeddedStorageFoundation");
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
    }
}
