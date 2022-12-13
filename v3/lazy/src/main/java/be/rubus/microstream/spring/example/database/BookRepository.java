package be.rubus.microstream.spring.example.database;

import be.rubus.microstream.spring.example.model.Book;
import one.microstream.storage.types.StorageManager;
import org.springframework.stereotype.Component;

import jakarta.inject.Provider;
import java.util.List;
import java.util.Optional;

@Component
public class BookRepository {

    private final Provider<StorageManager> storageManagerProvider;

    public BookRepository(Provider<StorageManager> storageManagerProvider) {

        this.storageManagerProvider = storageManagerProvider;
    }

    private Root getRoot() {
        return (Root) storageManagerProvider.get().root();
    }

    public List<Book> getAll() {
        return getRoot().getBooks();
    }

    public Optional<Book> getBookByISBN(String isbn) {
        return getRoot().getBooks().stream()
                .filter(b -> b.getIsbn().equals(isbn))
                .findAny();
    }

}
