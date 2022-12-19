package be.rubus.microstream.spring.example;

import be.rubus.microstream.spring.example.database.DatabaseColor;
import be.rubus.microstream.spring.example.database.Products;
import be.rubus.microstream.spring.example.model.Product;
import one.microstream.integrations.spring.boot.types.config.StorageManagerInitializer;
import one.microstream.storage.types.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RootPreparationOfRedDatabase implements StorageManagerInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootPreparationOfRedDatabase.class);

    @Override
    public void initialize(StorageManager storageManager) {
        if (DatabaseColor.GREEN.getName().equals(storageManager.databaseName())) {
            // This customizer operates on the Red database
            return;
        }

        LOGGER.info("(From the App) Add basic data if needed (For Root of Red database)");

        // Since we have @Storage used, we are sure that Root object is initialized in StorageManager
        // We only need to check if there is an initialization of data required or not (since we already ran it before)

        Products root = (Products) storageManager.root();
        // Init 'database' with some data
        if (root.getProducts().isEmpty()) {
            init(root);
        }

    }

    public void init(Products root) {
        root.addProduct(new Product(1L, "Apple", 5));
        root.addProduct(new Product(2L, "Banana", 4));
        root.addProduct(new Product(3L, "Kiwi", 2));
    }


}
