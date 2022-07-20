package be.rubus.microstream.spring.example.database;

import be.rubus.microstream.spring.example.dto.CreateUser;
import be.rubus.microstream.spring.example.exception.UserAlreadyExistsException;
import be.rubus.microstream.spring.example.exception.UserNotFoundException;
import be.rubus.microstream.spring.example.model.User;
import one.microstream.integrations.spring.boot.types.Store;
import one.microstream.storage.types.StorageManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserRepository {

    private static final Object USER_LOCK = new Object();

    private final Root root;

    public UserRepository(Root root) {
        this.root = root;
    }

    public List<User> getAll() {
        return root.getUsers();
    }

    public Optional<User> getById(String id) {
        return root.getUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findAny();
    }

    public Optional<User> findByEmail(String email) {
        return root.getUsers().stream()
                .filter(u -> email.equals(u.getEmail()))
                .findAny();
    }

    @Store
    public User add(CreateUser user) {
        User result;
        synchronized (USER_LOCK) {
            Optional<User> byEmail = findByEmail(user.getEmail());
            if (byEmail.isPresent()) {
                throw new UserAlreadyExistsException();
            }
            result = root.addUser(new User(user.getName(), user.getEmail()));
        }
        return result;
    }

    @Store
    public User updateEmail(String id, String email) {
        Optional<User> byId = getById(id);
        if (byId.isEmpty()) {
            throw new UserNotFoundException();
        }
        User user = byId.get();
        user.setEmail(email);
        root.updateUser(user);
        return user;
    }

    @Store(asynchronous = false)
    public void removeById(String id) {
        synchronized (USER_LOCK) {
            Optional<User> userById = root.getUsers().stream()
                    .filter(u -> u.getId().equals(id))
                    .findAny();
            userById.ifPresent(root::removeUser);
        }
    }
}
