package be.rubus.microstream.spring.example.database;

import be.rubus.microstream.spring.example.exception.BookAlreadyAssignedException;
import be.rubus.microstream.spring.example.exception.BookNotFoundException;
import be.rubus.microstream.spring.example.exception.UserNotFoundException;
import be.rubus.microstream.spring.example.model.Book;
import be.rubus.microstream.spring.example.model.User;
import one.microstream.storage.types.StorageManager;
import org.springframework.stereotype.Component;

import javax.inject.Provider;
import java.util.Optional;

@Component
public class UserBookRepository {

    private static final Object USER_BOOK_LOCK = new Object();

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final Provider<StorageManager> storageManagerProvider;

    public UserBookRepository(Provider<StorageManager> storageManagerProvider, UserRepository userRepository, BookRepository bookRepository) {
        this.storageManagerProvider = storageManagerProvider;

        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    private Root getRoot() {
        return (Root) storageManagerProvider.get().root();
    }

    public void addBookToUser(String id, String isbn) {
        synchronized (USER_BOOK_LOCK) {
            Optional<User> byId = userRepository.getById(id);
            if (byId.isEmpty()) {
                throw new UserNotFoundException();
            }
            Optional<Book> bookByISBN = bookRepository.getBookByISBN(isbn);
            if (bookByISBN.isEmpty()) {
                throw new BookNotFoundException();
            }

            User user = byId.get();
            Book book = bookByISBN.get();
            if (user.getBooks().contains(book)) {
                throw new BookAlreadyAssignedException();
            }
            getRoot().addBookToUser(user, book);
        }
    }
}
