package be.rubus.microstream.spring.example.controller;

import be.rubus.microstream.spring.example.database.UserRepository;
import be.rubus.microstream.spring.example.dto.CreateUser;
import be.rubus.microstream.spring.example.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
public class UserController {


    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/user")
    public Collection<User> getAll() {
        return this.repository.getAll();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getById(@PathVariable("id") String id) {
        Optional<User> byId = repository.getById(id);
        return byId.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/by/{email}")
    public ResponseEntity<User> findBy(@PathVariable String email) {
        Optional<User> byEmail = repository.findByEmail(email);
        return byEmail.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> addUser(@RequestBody CreateUser user) {
        return new ResponseEntity<>(repository.add(user), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") String id) {
        repository.removeById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(value = "/user/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateEmail(@PathVariable("id") String id, @RequestBody JsonNode json) {
        String email = json.get("email").asText();
        if (email == null || email.isBlank()) {
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }

        User user = repository.updateEmail(id, email);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
