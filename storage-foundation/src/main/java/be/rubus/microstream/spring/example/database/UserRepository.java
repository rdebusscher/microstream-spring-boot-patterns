package be.rubus.microstream.spring.example.database;

import be.rubus.microstream.spring.example.dto.CreateUser;
import be.rubus.microstream.spring.example.exception.UserAlreadyExistsException;
import be.rubus.microstream.spring.example.exception.UserNotFoundException;
import be.rubus.microstream.spring.example.model.User;
import one.microstream.storage.types.StorageManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class UserRepository {

    private final Root root;

    private final ReentrantReadWriteLock userLock = new ReentrantReadWriteLock();

    public UserRepository(StorageManager storageManager) {
        root = (Root) storageManager.root();
    }

    public List<User> getAll() {
        return root.getUsers();
    }

    public Optional<User> getById(String id) {
        userLock.readLock().lock();
        try {
            return root.getUsers().stream()
                    .filter(u -> u.getId().equals(id))
                    .findAny();
        } finally {
            userLock.readLock().unlock();
        }
    }

    public Optional<User> findByEmail(String email) {
        userLock.readLock().lock();
        try {

            return root.getUsers().stream()
                    .filter(u -> email.equals(u.getEmail()))
                    .findAny();
        } finally {
            userLock.readLock().unlock();
        }

    }

    public User add(CreateUser user) {
        userLock.writeLock().lock();
        try {
            User result;

            // This block also protects that multiple threads modify the User collection
            // at the time MicroStream stores the changes (avoids ConcurrentModificationException)
            Optional<User> byEmail = findByEmail(user.getEmail());
            if (byEmail.isPresent()) {
                throw new UserAlreadyExistsException();
            }
            result = root.addUser(new User(user.getName(), user.getEmail()));

            return result;
        } finally {
            userLock.writeLock().unlock();
        }

    }

    public User updateEmail(String id, String email) {
        userLock.writeLock().lock();
        try {
            Optional<User> byId = getById(id);
            if (byId.isEmpty()) {
                throw new UserNotFoundException();
            }
            User user = byId.get();
            user.setEmail(email);
            root.updateUser(user);
            return user;
        } finally {
            userLock.writeLock().unlock();
        }
    }

    public void removeById(String id) {
        userLock.writeLock().lock();
        try {

            Optional<User> userById = root.getUsers().stream()
                    .filter(u -> u.getId().equals(id))
                    .findAny();
            userById.ifPresent(root::removeUser);
        } finally {
            userLock.writeLock().unlock();
        }
    }
}
