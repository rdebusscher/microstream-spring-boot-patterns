package be.rubus.microstream.spring.example.database;

import be.rubus.microstream.spring.example.model.Product;
import one.microstream.integrations.spring.boot.types.Storage;
import one.microstream.storage.types.StorageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashSet;
import java.util.Set;

@Storage
@Qualifier("red") // Only constants allowed
public class Products {

    // No constructor injection supported on @Storage Beans.
    @Autowired
    @Qualifier("red")
    private transient StorageManager storageManager;

    private final Set<Product> products = new HashSet<>();

    public Set<Product> getProducts() {
        return new HashSet<>(products);
    }

    public void addProduct(Product product) {
        products.add(product);
        storageManager.store(products);
    }

    public void deleteProduct(Long productId) {
        products.removeIf(product -> product.getId().equals(productId));
        storageManager.store(products);
    }
}
