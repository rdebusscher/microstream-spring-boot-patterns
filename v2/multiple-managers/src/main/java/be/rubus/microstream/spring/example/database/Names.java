package be.rubus.microstream.spring.example.database;

import one.microstream.integrations.spring.boot.types.Storage;
import one.microstream.storage.types.StorageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashSet;
import java.util.Set;

@Storage
@Qualifier("green")  // Only constants allowed
public class Names {

    // No constructor injection supported on @Storage Beans.
    @Autowired
    @Qualifier("green")
    private transient StorageManager storageManager;

    private final Set<String> names = new HashSet<>();

    public Set<String> getNames() {
        return new HashSet<>(names);
    }

    public void addName(String name) {
        names.add(name);
        storageManager.store(names);
    }

    public void deleteName(String name) {
        names.remove(name);
        storageManager.store(names);
    }
}
