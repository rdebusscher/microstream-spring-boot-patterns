package be.rubus.microstream.spring.example.database;

import be.rubus.microstream.spring.example.model.Book;
import be.rubus.microstream.spring.example.model.User;
import one.microstream.integrations.spring.boot.types.DirtyMarker;
import one.microstream.integrations.spring.boot.types.Storage;
import one.microstream.storage.types.StorageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Storage
public class Root {

    @Autowired
    private transient DirtyMarker dirtyMarker;

    private final List<User> users = new ArrayList<>();
    private final List<Book> books = new ArrayList<>();

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public User addUser(User user) {
        this.dirtyMarker.mark(users).add(user);
        return user;
    }

    /**
     * Since the User instance is already part of the User Collection, we just need
     * to make it is stored externally.
     *
     * @param user
     */
    public void updateUser(User user) {
        this.dirtyMarker.mark(user);
    }

    public void removeUser(User user) {
        this.dirtyMarker.mark(users).remove(user);
    }

    public List<Book> getBooks() {
        return new ArrayList<>(books);
    }

    public void addBook(Book book) {
        this.dirtyMarker.mark(books).add(book);
    }

    /**
     * User instance must already be part of the Object graph of the root managed by MicroStream.
     *
     * @param user
     * @param book
     */
    public void addBookToUser(User user, Book book) {
        user.addBook(book, dirtyMarker);
    }
}
