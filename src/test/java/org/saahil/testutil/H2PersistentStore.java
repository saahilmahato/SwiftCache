package org.saahil.testutil;

import org.saahil.store.PersistentStore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class H2PersistentStore<K, V> implements PersistentStore<K, V> {

    private final String jdbcUrl;

    public H2PersistentStore() {
        this("cache_" + UUID.randomUUID());
    }

    public H2PersistentStore(String dbName) {
        this.jdbcUrl = "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1";
        initializeSchema();
    }

    @Override
    public void save(K key, V value) {
        String sql = "MERGE INTO cache_entries (cache_key, cache_value) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(key));
            statement.setString(2, value == null ? null : String.valueOf(value));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save value in H2 store", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public V load(K key) {
        String sql = "SELECT cache_value FROM cache_entries WHERE cache_key = ?";
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(key));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return (V) resultSet.getString(1);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to load value from H2 store", e);
        }
    }

    @Override
    public void delete(K key) {
        String sql = "DELETE FROM cache_entries WHERE cache_key = ?";
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(key));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete value from H2 store", e);
        }
    }

    @Override
    public boolean exists(K key) {
        String sql = "SELECT 1 FROM cache_entries WHERE cache_key = ?";
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(key));
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to check existence in H2 store", e);
        }
    }

    private void initializeSchema() {
        String sql = "CREATE TABLE IF NOT EXISTS cache_entries (cache_key VARCHAR PRIMARY KEY, cache_value VARCHAR)";
        try (Connection connection = DriverManager.getConnection(jdbcUrl);
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to initialize H2 schema", e);
        }
    }
}
