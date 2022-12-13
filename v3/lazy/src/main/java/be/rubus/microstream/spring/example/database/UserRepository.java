package be.rubus.microstream.spring.example.database;

import be.rubus.microstream.spring.example.dto.CreateUser;
import be.rubus.microstream.spring.example.exception.UserAlreadyExistsException;
import be.rubus.microstream.spring.example.exception.UserNotFoundException;
import be.rubus.microstream.spring.example.model.User;
import one.microstream.storage.types.StorageManager;
import org.springframework.stereotype.Component;

import jakarta.inject.Provider;
import java.util.List;
import java.util.Optional;

@Component
public class UserRepository {

    private static final Object USER_LOCK = new Object();
    private final Provider<StorageManager> storageManagerProvider;

    public UserRepository(Provider<StorageManager> storageManagerProvider) {

        this.storageManagerProvider = storageManagerProvider;
    }

    private Root getRoot() {
        return (Root) storageManagerProvider.get().root();
    }
    public List<User> getAll() {
        return getRoot().getUsers();
    }

    public Optional<User> getById(String id) {
        return getRoot().getUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findAny();
    }

    public Optional<User> findByEmail(String email) {
        return getRoot().getUsers().stream()
                .filter(u -> email.equals(u.getEmail()))
                .findAny();
    }

    public User add(CreateUser user) {
        User result;
        synchronized (USER_LOCK) {
            Optional<User> byEmail = findByEmail(user.getEmail());
            if (byEmail.isPresent()) {
                throw new UserAlreadyExistsException();
            }
            result = getRoot().addUser(new User(user.getName(), user.getEmail()));
        }
        return result;
    }

    public User updateEmail(String id, String email) {
        Optional<User> byId = getById(id);
        if (byId.isEmpty()) {
            throw new UserNotFoundException();
        }
        User user = byId.get();
        user.setEmail(email);
        getRoot().updateUser(user);
        return user;
    }

    public void removeById(String id) {
        synchronized (USER_LOCK) {
            Optional<User> userById = getRoot().getUsers().stream()
                    .filter(u -> u.getId().equals(id))
                    .findAny();
            userById.ifPresent(getRoot()::removeUser);
        }
    }
}
