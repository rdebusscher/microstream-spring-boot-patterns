package be.rubus.microstream.spring.example;

import one.microstream.integrations.spring.boot.types.config.EmbeddedStorageFoundationCustomizer;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class FoundationCustomizerSecondaryDatabase implements EmbeddedStorageFoundationCustomizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FoundationCustomizerSecondaryDatabase.class);

    @Override
    public void customize(final EmbeddedStorageFoundation embeddedStorageFoundation) {
        if (!"secondary".equals(embeddedStorageFoundation.getDataBaseName())) {
            // This customizer operates on the Secondary database
            return;
        }
        LOGGER.info("(From the App) Additional configuration on the Green EmbeddedStorageFoundation");
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
