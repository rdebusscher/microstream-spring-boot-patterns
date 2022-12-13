package be.rubus.microstream.spring.example.database;

import be.rubus.microstream.spring.example.model.Book;
import one.microstream.storage.types.StorageManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BookRepository {

    private final Root root;

    public BookRepository(StorageManager storageManager) {
        root = (Root) storageManager.root();
    }

    public List<Book> getAll() {
        return root.getBooks();
    }

    public Optional<Book> getBookByISBN(String isbn) {
        return root.getBooks().stream()
                .filter(b -> b.getIsbn().equals(isbn))
                .findAny();
    }

}
