package be.rubus.microstream.spring.example;

import be.rubus.microstream.spring.example.database.DatabaseColor;
import one.microstream.integrations.spring.boot.types.config.EmbeddedStorageFoundationCustomizer;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class FoundationCustomizerGreenDatabase implements EmbeddedStorageFoundationCustomizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FoundationCustomizerGreenDatabase.class);

    @Override
    public void customize(final EmbeddedStorageFoundation embeddedStorageFoundation) {
        if (DatabaseColor.RED.getName().equals(embeddedStorageFoundation.getDataBaseName())) {
            // This customizer operates on the Green database
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
