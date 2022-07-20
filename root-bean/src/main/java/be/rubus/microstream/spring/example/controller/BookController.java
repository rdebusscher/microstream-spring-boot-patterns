package be.rubus.microstream.spring.example.controller;

import be.rubus.microstream.spring.example.database.BookRepository;
import be.rubus.microstream.spring.example.model.Book;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class BookController {


    private final BookRepository repository;

    public BookController(BookRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/book")
    public Collection<Book> getAll() {
        return repository.getAll();
    }


}
