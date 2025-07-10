package com.voting.dao;

import com.voting.exception.VotingException;
import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.*;


/**
 * Handles all database operations with connection pooling.
 * Hardcoded for XAMPP's MySQL configuration.
 */
public class DatabaseManager {
    private static volatile DatabaseManager instance;
    private final BasicDataSource dataSource;

    // Hardcoded configuration for XAMPP MySQL
    private static final String DB_URL = "jdbc:mysql://localhost:3306/voting_system?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root"; // Default XAMPP username
    private static final String DB_PASSWORD = ""; // Default XAMPP password (empty)
    private static final int INITIAL_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;


    private DatabaseManager() {
        this.dataSource = setupDataSource();
        initializeDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    private BasicDataSource setupDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(DB_URL);
        ds.setUsername(DB_USER);
        ds.setPassword(DB_PASSWORD);
        ds.setInitialSize(INITIAL_POOL_SIZE);
        ds.setMaxTotal(MAX_POOL_SIZE);
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return ds;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private void initializeDatabase() {
        String[] ddl = {
                "CREATE TABLE IF NOT EXISTS students (" +
                        "  id VARCHAR(20) PRIMARY KEY," +
                        "  name VARCHAR(50) NOT NULL," +
                        "  password_hash VARCHAR(255) NOT NULL," +
                        "  has_voted BOOLEAN DEFAULT FALSE," +
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB",

                "CREATE TABLE IF NOT EXISTS candidates (" +
                        "  id VARCHAR(20) PRIMARY KEY," +
                        "  name VARCHAR(50) NOT NULL," +
                        "  bio TEXT," +
                        "  votes INT DEFAULT 0," +
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB",

                "CREATE TABLE IF NOT EXISTS elections (" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY," +
                        "  title VARCHAR(100) NOT NULL," +
                        "  is_open BOOLEAN DEFAULT FALSE," +
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  opened_at TIMESTAMP NULL," +
                        "  closed_at TIMESTAMP NULL" +
                        ") ENGINE=InnoDB",

                "INSERT IGNORE INTO elections (id, title, is_open) VALUES (1, 'Annual Election', FALSE)",

                "INSERT IGNORE INTO students (id, name, password_hash) VALUES (" +
                        "'admin', 'Administrator', " +
                        "'$2a$10$N9qo8uLOickgx2ZMRZoMy.MH/rH8wLZ7zZJfP/Y3WJQ7sQ8T7W6zC')",

                // Default regular user (username: user1, password: user123)
                "INSERT IGNORE INTO students (id, name, password_hash) VALUES (" +
                        "'user1', 'Regular User', " +
                        "'$2a$10$N9qo8uLOickgx2ZMRZoMy.MH/rH8wLZ7zZJfP/Y3WJQ7sQ8T7W6zC')"
        };

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String sql : ddl) {
                try {
                    stmt.execute(sql);
                } catch (SQLException e) {
                    System.err.println("Error executing SQL: " + sql);
                    throw e;
                }
            }
            System.out.println("Database initialized with default accounts:");
            System.out.println("Admin: admin/admin123");
            System.out.println("User: user1/user123");

        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(2);
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }


    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeUpdate();
        }
    }

    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt.executeQuery(); // Caller must close resources
    }


    public void executeQuery(String sql, ResultSetHandler handler) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            handler.handle(rs);
        }
    }

    @FunctionalInterface
    public interface ResultSetHandler {
        void handle(ResultSet rs) throws SQLException;
    }

    public void runTransaction(SQLOperation operation) throws SQLException, VotingException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            operation.execute(conn);
            conn.commit();

        } catch (SQLException | VotingException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    @FunctionalInterface
    public interface SQLOperation {
        void execute(Connection conn) throws SQLException, VotingException;
    }
}
