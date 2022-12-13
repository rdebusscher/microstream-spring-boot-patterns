package be.rubus.microstream.spring.example.database;

import be.rubus.microstream.spring.example.exception.BookAlreadyAssignedException;
import be.rubus.microstream.spring.example.exception.BookNotFoundException;
import be.rubus.microstream.spring.example.exception.UserNotFoundException;
import be.rubus.microstream.spring.example.model.Book;
import be.rubus.microstream.spring.example.model.User;
import one.microstream.storage.types.StorageManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserBookRepository {

    private static final Object USER_BOOK_LOCK = new Object();

    private final Root root;

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public UserBookRepository(StorageManager storageManager, UserRepository userRepository, BookRepository bookRepository) {
        root = (Root) storageManager.root();
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
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
            root.addBookToUser(user, book);
        }
    }
}
