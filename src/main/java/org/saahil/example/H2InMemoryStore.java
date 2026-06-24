package org.saahil.example;

import org.saahil.store.PersistentStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class H2InMemoryStore<K, V> implements PersistentStore<K, V> {

    private static final Logger LOGGER = Logger.getLogger(H2InMemoryStore.class.getName());

    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS cache_store (
                store_key VARBINARY PRIMARY KEY,
                store_value VARBINARY
            )
            """;
    private static final String UPSERT_SQL = "MERGE INTO cache_store (store_key, store_value) KEY(store_key) VALUES (?, ?)";
    private static final String SELECT_SQL = "SELECT store_value FROM cache_store WHERE store_key = ?";
    private static final String DELETE_SQL = "DELETE FROM cache_store WHERE store_key = ?";
    private static final String EXISTS_SQL = "SELECT 1 FROM cache_store WHERE store_key = ?";

    private final String jdbcUrl;

    public H2InMemoryStore() {
        this.jdbcUrl = "jdbc:h2:mem:swift_cache_" + UUID.randomUUID() + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1";
        initializeSchema();
    }

    @Override
    public synchronized void save(K key, V value) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(UPSERT_SQL)) {
            statement.setBytes(1, serialize(key));
            statement.setBytes(2, serialize(value));
            statement.executeUpdate();
            LOGGER.log(Level.INFO, "[H2InMemoryStore] Saved: {0} -> {1}", new Object[]{key, value});
        } catch (SQLException | IOException e) {
            throw new IllegalStateException("Failed to save key in H2 store", e);
        }
    }

    @Override
    public synchronized V load(K key) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(SELECT_SQL)) {
            statement.setBytes(1, serialize(key));
            try (ResultSet resultSet = statement.executeQuery()) {
                V value = null;
                if (resultSet.next()) {
                    value = deserialize(resultSet.getBytes(1));
                }
                LOGGER.log(Level.INFO, "[H2InMemoryStore] Loaded: {0} -> {1}", new Object[]{key, value});
                return value;
            }
        } catch (SQLException | IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Failed to load key from H2 store", e);
        }
    }

    @Override
    public synchronized void delete(K key) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setBytes(1, serialize(key));
            statement.executeUpdate();
            LOGGER.log(Level.INFO, "[H2InMemoryStore] Deleted: {0}", key);
        } catch (SQLException | IOException e) {
            throw new IllegalStateException("Failed to delete key from H2 store", e);
        }
    }

    @Override
    public synchronized boolean exists(K key) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(EXISTS_SQL)) {
            statement.setBytes(1, serialize(key));
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException | IOException e) {
            throw new IllegalStateException("Failed to check key existence in H2 store", e);
        }
    }

    private void initializeSchema() {
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_SQL)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to initialize H2 schema", e);
        }
    }

    private byte[] serialize(Object value) throws IOException {
        if (value != null && !(value instanceof java.io.Serializable)) {
            throw new IllegalArgumentException("Key/value must implement Serializable for H2 store usage");
        }

        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             ObjectOutputStream objectStream = new ObjectOutputStream(byteStream)) {
            objectStream.writeObject(value);
            objectStream.flush();
            return byteStream.toByteArray();
        }
    }

    @SuppressWarnings("unchecked")
    private V deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectStream = new ObjectInputStream(byteStream)) {
            return (V) objectStream.readObject();
        }
    }
}
