package be.rubus.microstream.spring.example.controller;

import be.rubus.microstream.spring.example.database.UserBookRepository;
import be.rubus.microstream.spring.example.database.UserRepository;
import be.rubus.microstream.spring.example.model.Book;
import be.rubus.microstream.spring.example.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class UserBookController {

    private final UserRepository userRepository;
    private final UserBookRepository userBookRepository;

    public UserBookController(UserRepository userRepository, UserBookRepository userBookRepository) {
        this.userRepository = userRepository;
        this.userBookRepository = userBookRepository;
    }

    @GetMapping("user/{id}/book")
    public ResponseEntity<List<Book>> getUserBooks(@PathVariable("id") String id) {
        Optional<User> byId = userRepository.getById(id);
        if (byId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(byId.get().getBooks(), HttpStatus.OK);
    }

    @PostMapping(value = "user/{id}/book/{isbn}")
    public void addBookToUser(@PathVariable("id") String id, @PathVariable("isbn") String isbn) {
        userBookRepository.addBookToUser(id, isbn);
    }
}
