package be.rubus.microstream.spring.example.model;

import be.rubus.microstream.spring.example.dto.CreateUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import one.microstream.storage.types.StorageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends CreateUser {

    private final String id;
    // We don't need a technical id when using MicroStream but need some kind of identification of the instance.
    // but email address is not a good candidate as that might change.
    @JsonIgnore
    private final List<Book> books = new ArrayList<>();

    public User(String name, String email) {
        super(name, email);
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }


    public List<Book> getBooks() {
        return new ArrayList<>(books);
    }

    public void addBook(Book book, StorageManager storageManager) {
        books.add(book);
        // Since we don't like to expose the actual list of Books (through getBooks)  as that
        // means we could alter the list outside the root, we provide the StorageManager as
        // parameter.
        storageManager.store(books);
    }
}
