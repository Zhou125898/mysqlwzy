package com.example.mysqlclient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private String url;
    private String user;
    private String password;
    private Connection connection;

    public DatabaseManager(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Connects to the database.
     * @throws DatabaseConnectionException if connection fails.
     */
    public void connect() throws DatabaseConnectionException {
        try {
            // 加载 MySQL 驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connecting to database...");
            this.connection = DriverManager.getConnection(url, user, password);
            if (this.connection == null || this.connection.isClosed()) {
                 // Theoretically, getConnection throws SQLException if it fails, 
                 // but adding an extra check for robustness.
                 throw new SQLException("DriverManager.getConnection returned null or closed connection.");
            }
            System.out.println("Database connected!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found! Make sure the driver JAR is in the classpath.");
            // Wrap and rethrow as a connection specific exception
            throw new DatabaseConnectionException("MySQL JDBC Driver not found!", e);
        } catch (SQLException e) {
            System.err.println("Connection Failed! Check console output, URL, username, password, and database status.");
            // Wrap and rethrow
            throw new DatabaseConnectionException("Database connection failed: " + e.getMessage(), e);
        }
    }

    /**
     * Disconnects from the database.
     */
    public void disconnect() {
        if (this.connection != null) {
            try {
                if (!this.connection.isClosed()) {
                    this.connection.close();
                    System.out.println("Database connection closed.");
                }
            } catch (SQLException e) {
                // Log error, but maybe don't prevent the rest of the app from closing
                System.err.println("Error occurred while closing the database connection.");
                e.printStackTrace();
            } finally {
                this.connection = null; // Ensure connection is null after attempting to close
            }
        }
    }

    /**
     * 执行 DDL (Data Definition Language) SQL 语句，例如 CREATE, ALTER, DROP.
     * @param ddlSql 要执行的 DDL 语句
     * @throws SQLExecutionException 如果执行失败或没有连接.
     */
    public void executeDDL(String ddlSql) throws SQLExecutionException {
        if (!isConnected()) { // Use helper method to check connection status
             throw new SQLExecutionException("Cannot execute DDL: No active database connection.");
        }

        try (Statement stmt = this.connection.createStatement()) {
            System.out.println("Executing DDL: " + ddlSql);
            stmt.executeUpdate(ddlSql); // DDL 使用 executeUpdate
            System.out.println("DDL statement executed successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to execute DDL statement: " + ddlSql);
            // Wrap and rethrow as a more specific execution exception
            throw new SQLExecutionException("Failed to execute DDL: " + e.getMessage(), e);
        }
    }

    // 稍后可以添加执行 DML (SELECT, INSERT, UPDATE, DELETE) 的方法
    // public ResultSet executeQuery(String sql) throws SQLExecutionException { ... }
    // public int executeUpdate(String sql) throws SQLExecutionException { ... }

    /**
     * Checks if the connection is active.
     * @return true if connected, false otherwise.
     */
    public boolean isConnected() {
        try {
            return this.connection != null && !this.connection.isClosed();
        } catch (SQLException e) {
            // Log this? An error checking closed status is unusual.
            System.err.println("Error checking connection status: " + e.getMessage());
            return false; // Treat as not connected if status check fails
        }
    }
}
