package com.example.mysqlclient;

public class Main {

    public static void main(String[] args) {
        System.out.println("MySQL GUI Client Initializing...");

        // 配置数据库连接信息
        String url = "jdbc:mysql://localhost:3306/your_database"; // 替换为你的数据库 URL
        String user = "your_username"; // 替换为你的数据库用户名
        String password = "your_password"; // 替换为你的数据库密码

        DatabaseManager dbManager = new DatabaseManager(url, user, password);

        try {
            // 连接数据库
            dbManager.connect();

            // --- 修改表结构示例 --- 
            try {
                // 假设我们有一个名为 'users' 的表，我们想添加一个 'email' 列
                String alterTableSQL = "ALTER TABLE users ADD COLUMN email VARCHAR(255)";

                System.out.println("\nAttempting to modify table structure...");
                dbManager.executeDDL(alterTableSQL);
                System.out.println("Table structure modified successfully.");

            } catch (SQLExecutionException e) {
                System.err.println("Failed to modify table structure: " + e.getMessage());
                // 在 GUI 中，这里可以显示一个错误对话框
                e.printStackTrace(); // 打印详细堆栈信息以供调试
            }
            // --- 示例结束 ---

            // 可以在这里添加更多数据库操作...

        } catch (DatabaseConnectionException e) {
            System.err.println("Database connection error: " + e.getMessage());
            // 在 GUI 中，这里可以显示连接错误对话框
            e.printStackTrace(); // 打印详细堆栈信息以供调试
        } finally {
            // 无论是否发生异常，都尝试断开连接
            // 注意：如果 connect() 失败，dbManager 可能没有活动连接，
            // 但 disconnect() 方法内部会处理这种情况。
            dbManager.disconnect();
        }

        System.out.println("\nMySQL GUI Client finished.");
    }
}
