package be.rubus.microstream.spring.example.database;

import be.rubus.microstream.spring.example.dto.CreateUser;
import be.rubus.microstream.spring.example.exception.UserAlreadyExistsException;
import be.rubus.microstream.spring.example.exception.UserNotFoundException;
import be.rubus.microstream.spring.example.model.User;
import one.microstream.storage.types.StorageManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserRepository {

    private final Root root;

    private final Locks locks;

    public UserRepository(StorageManager storageManager, Locks locks) {
        root = (Root) storageManager.root();
        this.locks = locks;
    }

    public List<User> getAll() {
        return root.getUsers();
    }

    public Optional<User> getById(String id) {
        return locks.readAction(Locks.USERS,
                () -> root.getUsers().stream()
                        .filter(u -> u.getId().equals(id))
                        .findAny()
        );
    }

    public Optional<User> findByEmail(String email) {
        return locks.readAction(Locks.USERS,
                () -> root.getUsers().stream()
                        .filter(u -> email.equals(u.getEmail()))
                        .findAny()
        );

    }

    public User add(CreateUser user) {
        // This block also protects that multiple threads modify the User collection
        // at the time MicroStream stores the changes (avoids ConcurrentModificationException)

        return locks.writeAction(Locks.USERS,
                () -> {
                    Optional<User> byEmail = findByEmail(user.getEmail());
                    if (byEmail.isPresent()) {
                        throw new UserAlreadyExistsException();
                    }
                    return root.addUser(new User(user.getName(), user.getEmail()));

                }
        );

    }

    public User updateEmail(String id, String email) {
        return locks.writeAction(Locks.USERS,
                () -> {
                    Optional<User> byId = getById(id);
                    if (byId.isEmpty()) {
                        throw new UserNotFoundException();
                    }
                    User user = byId.get();
                    user.setEmail(email);
                    root.updateUser(user);
                    return user;

                }
        );
    }

    public void removeById(String id) {
        locks.writeAction(Locks.USERS,
                () -> {
                    Optional<User> userById = root.getUsers().stream()
                            .filter(u -> u.getId().equals(id))
                            .findAny();
                    userById.ifPresent(root::removeUser);
                    return null;
                }
        );

    }
}
