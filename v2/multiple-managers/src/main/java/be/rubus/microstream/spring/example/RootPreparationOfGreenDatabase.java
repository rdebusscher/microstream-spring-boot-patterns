package be.rubus.microstream.spring.example;

import be.rubus.microstream.spring.example.database.DatabaseColor;
import be.rubus.microstream.spring.example.database.Names;
import one.microstream.integrations.spring.boot.types.config.StorageManagerInitializer;
import one.microstream.storage.types.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RootPreparationOfGreenDatabase implements StorageManagerInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootPreparationOfGreenDatabase.class);

    @Override
    public void initialize(StorageManager storageManager) {
        if (DatabaseColor.RED.getName().equals(storageManager.databaseName())) {
            // This customizer operates on the Red database
            return;
        }

        LOGGER.info("(From the App) Add basic data if needed (For Root of Green database)");

        // Since we have @Storage used, we are sure that Root object is initialized in StorageManager
        // We only need to check if there is an initialization of data required or not (since we already ran it before)

        Names root = (Names) storageManager.root();
        // Init 'database' with some data
        if (root.getNames().isEmpty()) {
            init(root);
        }

    }

    public void init(Names root) {
        root.addName("Markus");
        root.addName("Rudy");
    }


}
