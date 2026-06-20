package org.saahil.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class.getName());

    // Simulated database
    private static final Map<Long, User> DATABASE = new HashMap<>();

    static {
        DATABASE.put(1L, new User(1L, "John Doe", "john@example.com"));
        DATABASE.put(2L, new User(2L, "Jane Smith", "jane@example.com"));
        DATABASE.put(3L, new User(3L, "Bob Johnson", "bob@example.com"));
    }

    @Override
    public User getUserById(Long id) {
        LOGGER.log(Level.INFO, "UserServiceImpl.getUserById() - Fetching user from database for ID: {0}", id);
        // Simulate database query delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        }
        return DATABASE.get(id);
    }

    @Override
    public User getUserByName(String name) {
        LOGGER.log(Level.INFO, "UserServiceImpl.getUserByName() - Fetching user from database for name: {0}", name);
        // Simulate database query delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        }
        return DATABASE.values().stream()
                .filter(u -> u.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        LOGGER.log(Level.INFO, "UserServiceImpl.getAllUsers() - Fetching all users from database");
        // Simulate database query delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        }
        return new ArrayList<>(DATABASE.values());
    }
}

