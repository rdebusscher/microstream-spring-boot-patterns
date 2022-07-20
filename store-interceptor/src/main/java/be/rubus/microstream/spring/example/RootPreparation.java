package be.rubus.microstream.spring.example;

import be.rubus.microstream.spring.example.database.Root;
import be.rubus.microstream.spring.example.model.Book;
import be.rubus.microstream.spring.example.model.User;
import one.microstream.integrations.spring.boot.types.DirtyMarker;
import one.microstream.integrations.spring.boot.types.config.StorageManagerInitializer;
import one.microstream.storage.types.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RootPreparation implements StorageManagerInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootPreparation.class);


    private final DirtyMarker dirtyMarker;

    public RootPreparation(DirtyMarker dirtyMarker) {
        this.dirtyMarker = dirtyMarker;
    }

    @Override
    public void initialize(StorageManager storageManager) {
        LOGGER.info("(From the App) Add basic data if needed");

        // Since we have @Storage used, we are sure that Root object is initialized in StorageManager
        // We only need to check if there is an initialization of data required or not (since we already ran it before)

        Root root = (Root) storageManager.root();
        // Init 'database' with some data
        if (root.getBooks().isEmpty()) {
            init(root);
            // We have made changes outside of the normal way (the repositories) that have automatic
            // storage through @Store.
            // We store the root using an Eager Storer since we need to store all info anyway.
            storageManager.createEagerStorer().store(root);
        }

    }

    public void init(Root root) {
        User johnDoe = new User("John Doe", "john.doe@acme.org");
        User janeDoe = new User("Jane Doe", "jane.doe@acme.org");

        root.addUser(johnDoe);
        root.addUser(janeDoe);

        addBook(root, "9780140434132", "Northanger Abbey", "Austen, Jane", 1814);
        addBook(root, "9780007148387", "War and Peace", "Tolstoy, Leo", 1865);
        addBook(root, "9780141182490", "Mrs. Dalloway", "Woolf, Virginia", 1925);
        addBook(root, "9780312243029", "The Hours", "Cunnningham, Michael", 1999);
        addBook(root, "9780141321097", "Huckleberry Finn", "Twain, Mark", 1865);
        addBook(root, "9780141439723", "Bleak House", "Dickens, Charles", 1870);
        addBook(root, "9780520235755", "The adventures of Tom Sawyer", "Twain, Mark", 1862);
        addBook(root, "9780156030410", "A Room of One's Own", "Woolf, Virginia", 1922);

        addBook(root, "9780140707342", "Hamlet, Prince of Denmark", "Shakespeare", 1603);
        addBook(root, "9780395647400", "Lord of the Rings", "Tolkien, J.R.", 1937);

        Book annaKarenina = addBook(root, "9780679783305", "Anna Karenina", "Tolstoy, Leo", 1875);
        janeDoe.addBook(annaKarenina, dirtyMarker);

        Book book = addBook(root, "9780060114183", "One Hundred Years of Solitude", "Marquez", 1967);
        janeDoe.addBook(book, dirtyMarker);

        Book harryPotter = addBook(root, "9780747532743", "Harry Potter", "Rowling, J.K.", 2000);
        johnDoe.addBook(harryPotter, dirtyMarker);


    }

    private Book addBook(Root root, String isbn, String name, String author, int year) {
        Book result = new Book(isbn, name, author, year);
        root.addBook(result);
        return result;
    }

}
