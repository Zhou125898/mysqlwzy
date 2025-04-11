package com.mycompany.mysqlclient;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // 导入 FXMLLoader
import javafx.scene.Parent;     // 导入 Parent
import javafx.scene.Scene;       // 导入 Scene
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;  // 导入 Modality
import javafx.stage.Stage;       // 导入 Stage
import javafx.beans.value.ChangeListener; // 新增导入
import javafx.beans.value.ObservableValue; // 新增导入

import java.io.IOException; // 导入 IOException
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional; // 导入 Optional

// 导入连接对话框控制器及其内部类
import com.mycompany.mysqlclient.ConnectionDialogController.ConnectionDetails;
import com.mycompany.mysqlclient.AlertUtil; // 导入顶级的 AlertUtil

// 导入数据库相关类
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // 导入 Statement
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javafx.scene.control.Alert.AlertType; // 导入 AlertType
import javafx.scene.control.ButtonType; // 导入 ButtonType
import javafx.scene.input.ContextMenuEvent; // 导入 ContextMenuEvent
import javafx.scene.control.TextInputDialog; // 导入 TextInputDialog
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory; // For line numbers
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import java.time.Duration; // For delayed styling
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.application.Platform; // Keep necessary imports like Platform
import javafx.beans.property.ReadOnlyObjectWrapper; // Keep necessary imports
import javafx.fxml.Initializable;

public class MainController implements Initializable {

    // --- Define Syntax Highlighting Patterns as Static Final Members ---
    private static final String[] KEYWORDS = new String[]{
            "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "BEFORE", "BETWEEN",
            "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE",
            "CHANGE", "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION",
            "CONSTRAINT", "CONTINUE", "CONVERT", "CREATE", "CROSS", "CURRENT_DATE",
            "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE",
            "DATABASES", "DATE", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND",
            "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DESC",
            "DESCRIBE", "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE",
            "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "ENCLOSED", "ESCAPED", "EXISTS",
            "EXIT", "EXPLAIN", "FALSE", "FETCH", "FLOAT", "FLOAT4", "FLOAT8", "FOR",
            "FORCE", "FOREIGN", "FROM", "FULLTEXT", "GENERATED", "GET", "GRANT", "GROUP",
            "HAVING", "HIGH_PRIORITY", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND",
            "IF", "IGNORE", "IN", "INDEX", "INFILE", "INNER", "INOUT", "INSENSITIVE",
            "INSERT", "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERVAL",
            "INTO", "IO_AFTER_GTIDS", "IO_BEFORE_GTIDS", "IS", "ITERATE", "JOIN", "KEY",
            "KEYS", "KILL", "LEADING", "LEAVE", "LEFT", "LIKE", "LIMIT", "LINEAR",
            "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB",
            "LONGTEXT", "LOOP", "LOW_PRIORITY", "MASTER_BIND", "MASTER_SSL_VERIFY_SERVER_CERT",
            "MATCH", "MAXVALUE", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT",
            "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", "MODIFIES", "NATURAL", "NOT",
            "NO_WRITE_TO_BINLOG", "NULL", "NUMERIC", "ON", "OPTIMIZE", "OPTION",
            "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", "PARTITION",
            "PRECISION", "PRIMARY", "PROCEDURE", "PURGE", "RANGE", "READ", "READS",
            "READ_WRITE", "REAL", "REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPEAT",
            "REPLACE", "REQUIRE", "RESIGNAL", "RESTRICT", "RETURN", "REVOKE", "RIGHT",
            "RLIKE", "SCHEMA", "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE",
            "SEPARATOR", "SET", "SHOW", "SIGNAL", "SMALLINT", "SPATIAL", "SPECIFIC", "SQL",
            "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS",
            "SQL_SMALL_RESULT", "SSL", "STARTING", "STORED", "STRAIGHT_JOIN", "TABLE",
            "TERMINATED", "THEN", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING",
            "TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE",
            "USAGE", "USE", "USING", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES",
            "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", "VIRTUAL", "WHEN", "WHERE",
            "WHILE", "WITH", "WRITE", "XOR", "YEAR_MONTH", "ZEROFILL"
    };
    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\\\[|\\\\]";
    private static final String SEMICOLON_PATTERN = ";";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'";
    private static final String COMMENT_PATTERN = "--[^\r\n]*|#.*";
    private static final String MULTILINE_COMMENT_PATTERN = "/\\*(.|\\R)*?\\*/";
    private static final String NUMBER_PATTERN = "\\b\\d+([.]\\d+)?\\b";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<MLCOMMENT>" + MULTILINE_COMMENT_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
            , Pattern.CASE_INSENSITIVE);

    // --- END Pattern Definitions ---

    // --- FXML Injected Fields ---
    // 这些字段会自动关联到 main-view.fxml 中具有相同 fx:id 的控件

    @FXML
    private TreeView<String> dbTreeView; // 类型参数 <String> 表示树节点显示的是字符串，后续可能需要更复杂的类型

    @FXML
    private SplitPane mainSplitPane;

    @FXML
    private CodeArea sqlTextArea;

    @FXML
    private TableView<ObservableList<String>> resultTableView; // Ensure correct type

    @FXML
    private Label statusLabel;

    @FXML
    private Button executeSqlButton; // 新增执行按钮的注入

    // --- Member Variables ---
    private HikariDataSource dataSource; // 用于持有数据库连接池
    private String currentConnectionName = null; // Store current connection display name

    // 用于标记数据库节点的虚拟子节点，表示需要加载表
    private static final String PLACEHOLDER_NODE_VALUE = "Loading...";
    private static final String TABLE_PLACEHOLDER_NODE_VALUE = "Loading Tables...";
    private static final String COLUMN_PLACEHOLDER_NODE_VALUE = "Loading Columns...";

    private ContextMenu treeContextMenu; // 添加上下文菜单成员变量
    private MenuItem deleteDatabaseMenuItem;
    private MenuItem deleteTableMenuItem;

    // --- Initialization Method ---

    /**
     * FXML 控制器初始化方法。
     * 在 FXML 文件加载完成并且所有 @FXML 字段被注入后自动调用。
     * 可以在这里进行控件的初始设置。
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("MainController initializing...");
        statusLabel.setText("状态：就绪");
        TreeItem<String> rootItem = new TreeItem<>("Connections");
        rootItem.setExpanded(true);
        dbTreeView.setRoot(rootItem);

        // --- 创建并设置 TreeView 的上下文菜单 ---
        createTreeContextMenu();
        dbTreeView.setContextMenu(treeContextMenu);

        // --- 可选: 根据选择动态更新菜单项可用性 (更精细的控制) ---
        // dbTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        //     updateContextMenu(newValue);
        // });
        // --- 或者在显示菜单前更新 (更常用) ---
        dbTreeView.setOnContextMenuRequested(this::handleContextMenuRequest);

        // --- ADDED: Initialize CodeArea ---
        initializeSqlCodeArea();
        // --- END ADDED ---

        // Make the execute button the default button for the scene (e.g., Enter key)
        Platform.runLater(() -> {
            if (executeSqlButton != null && executeSqlButton.getScene() != null) {
                executeSqlButton.setDefaultButton(true);
                // Optional: Request focus on the CodeArea initially
                sqlTextArea.requestFocus();
            } else {
                System.err.println("Could not set default button or focus - scene/button not ready.");
            }
        });

        System.out.println("MainController initialization complete.");
    }

    private void createTreeContextMenu() {
        treeContextMenu = new ContextMenu();

        deleteDatabaseMenuItem = new MenuItem("删除数据库...");
        deleteDatabaseMenuItem.setOnAction(e -> handleDeleteDatabase());

        deleteTableMenuItem = new MenuItem("删除数据表...");
        deleteTableMenuItem.setOnAction(e -> handleDeleteTable());

        // 添加其他菜单项，例如 刷新、新建表、查看表数据等
        MenuItem refreshItem = new MenuItem("刷新");
        refreshItem.setOnAction(e -> {
            TreeItem<String> selected = dbTreeView.getSelectionModel().getSelectedItem();
            if (selected == dbTreeView.getRoot()) { // 刷新所有连接 (如果支持多连接)
                // TODO: Implement refresh all connections
            } else if (isConnectionNode(selected)) { // 刷新连接下的数据库
                 refreshDatabaseStructure(); // 调用现有方法
            } else if (isDatabaseNode(selected)) { // 刷新数据库下的表
                 selected.getChildren().clear(); // 清空强制刷新
                 selected.getChildren().add(new TreeItem<>(TABLE_PLACEHOLDER_NODE_VALUE));
                 selected.setExpanded(false);
                 selected.setExpanded(true);
            } else if (isTableNode(selected)) { // 刷新表的列
                 selected.getChildren().clear();
                 selected.getChildren().add(new TreeItem<>(COLUMN_PLACEHOLDER_NODE_VALUE));
                 selected.setExpanded(false);
                 selected.setExpanded(true);
            }
        });

        treeContextMenu.getItems().addAll(refreshItem, new SeparatorMenuItem(), deleteDatabaseMenuItem, deleteTableMenuItem);
    }

    private void handleContextMenuRequest(ContextMenuEvent event) {
        TreeItem<String> selectedItem = dbTreeView.getSelectionModel().getSelectedItem();
        updateContextMenu(selectedItem);
    }

    /** 更新上下文菜单项的可见性/可用性 */
    private void updateContextMenu(TreeItem<String> selectedItem) {
        boolean isDb = isDatabaseNode(selectedItem);
        boolean isTable = isTableNode(selectedItem);

        deleteDatabaseMenuItem.setVisible(isDb);
        deleteDatabaseMenuItem.setDisable(!isDb);

        deleteTableMenuItem.setVisible(isTable);
        deleteTableMenuItem.setDisable(!isTable);

        // 可以为其他菜单项设置类似逻辑
    }

    // --- Helper methods to identify node types --- 
    private boolean isConnectionNode(TreeItem<String> item) {
        return item != null && item.getParent() == dbTreeView.getRoot();
    }

    private boolean isDatabaseNode(TreeItem<String> item) {
        return item != null && isConnectionNode(item.getParent());
    }

     private boolean isTableNode(TreeItem<String> item) {
         return item != null && isDatabaseNode(item.getParent()) && 
                !TABLE_PLACEHOLDER_NODE_VALUE.equals(item.getValue()) && 
                !COLUMN_PLACEHOLDER_NODE_VALUE.equals(item.getValue()) && 
                !item.getValue().startsWith("("); 
     }
     //---------------------------------------------

    @FXML
    private void handleConnectDatabase() {
        System.out.println("Action: Connect Database triggered");
        try {
            // 1. 加载 FXML 文件
            FXMLLoader loader = new FXMLLoader();
            // 确保 FXML 文件在正确的资源路径下
            URL fxmlLocation = getClass().getResource("connection-dialog.fxml");
            if (fxmlLocation == null) {
                System.err.println("Cannot find FXML file: connection-dialog.fxml");
                AlertUtil.showError("错误", "无法加载对话框", "找不到 connection-dialog.fxml 文件。");
                return;
            }
            loader.setLocation(fxmlLocation);
            Parent dialogPane = loader.load();

            // 2. 创建对话框 Stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("连接到 MySQL 数据库");
            dialogStage.initModality(Modality.WINDOW_MODAL); // 设置为模态对话框
            // 可选: 设置所有者窗口 (如果需要)
            // dialogStage.initOwner(statusLabel.getScene().getWindow()); // 或者其他控件

            Scene scene = new Scene(dialogPane);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false); // 禁止调整大小

            // 3. 获取对话框控制器
            ConnectionDialogController controller = loader.getController();

            // 4. 显示对话框并等待用户操作
            dialogStage.showAndWait();

            // 5. 处理用户操作结果
            if (controller.isOkClicked()) {
                ConnectionDetails details = controller.getConnectionDetails();
                // 检查 details 是否为 null (虽然在当前逻辑下不太可能，但作为防御性编程)
                if (details == null) {
                     System.err.println("Error: ConnectionDetails is null after OK clicked.");
                     statusLabel.setText("状态：内部错误");
                     return;
                }
                statusLabel.setText("状态：正在连接到 " + details.getHost() + "...");
                System.out.println("Attempting to connect with details: ");
                System.out.println("  Host: " + details.getHost());
                System.out.println("  Port: " + details.getPort());
                System.out.println("  User: " + details.getUsername());
                System.out.println("  Database: " + details.getDatabase()); // 可能为 null
                System.out.println("  JDBC URL: " + details.getJdbcUrl());

                // 调用实际的数据库连接方法
                connectToDatabase(details);

            } else {
                System.out.println("Connection cancelled by user.");
                statusLabel.setText("状态：连接已取消");
            }

        } catch (IOException e) {
            System.err.println("Failed to load connection dialog FXML: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("错误", "加载对话框失败", "加载 FXML 文件时出错: " + e.getMessage());
        } catch (Exception e) { // 捕获其他潜在异常，例如控制器获取失败等
             System.err.println("An unexpected error occurred in handleConnectDatabase: " + e.getMessage());
             e.printStackTrace();
             AlertUtil.showError("错误", "发生意外错误", e.getMessage());
        }
    }

    /**
     * Handles the request to close the application window.
     * Called from MainApp when the stage's close button is clicked.
     */
    public void handleExitRequest() {
        System.out.println("Handling exit request: closing data source.");
        closeDataSource();
        // Platform.exit(); // Let the default close operation handle the actual exit
                           // Or call System.exit(0) if Platform.exit() doesn't work as expected
                           // System.exit(0);
    }

    @FXML
    private void handleExit() {
        System.out.println("Action: Exit triggered from menu");
        handleExitRequest(); // Perform cleanup
        Platform.exit();     // Exit the JavaFX application
        System.exit(0);    // Fallback exit
    }

    @FXML
    private void handleCopy() {
        System.out.println("Action: Copy triggered");
        sqlTextArea.copy();
    }

    @FXML
    private void handlePaste() {
        System.out.println("Action: Paste triggered");
        sqlTextArea.paste();
    }

    @FXML
    private void handleAbout() {
        System.out.println("Action: About triggered");
        showAlert("关于", "MySQL GUI 客户端 v1.0\n作者：王泽宇");
    }

    @FXML
    private void handleCreateDatabase() {
        System.out.println("Action: Create Database triggered");
        if (dataSource == null || dataSource.isClosed()) {
            AlertUtil.showError("错误", "无有效连接", "请先连接到数据库服务器。");
            return;
        }
        TextInputDialog dialog = new TextInputDialog("new_database");
        dialog.setTitle("新建数据库");
        dialog.setHeaderText("请输入新数据库的名称：");
        dialog.setContentText("名称:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(dbName -> {
            if (dbName.trim().isEmpty()) {
                AlertUtil.showWarning("输入无效", "数据库名称不能为空", null);
                return;
            }
            if (!dbName.matches("^[a-zA-Z0-9_]+$")) {
                 AlertUtil.showWarning("输入无效", "数据库名称只能包含字母、数字和下划线。", null);
                return;
            }
            executeCreateDatabaseStatement(dbName.trim());
        });
    }

    private void executeCreateDatabaseStatement(String dbName) {
         String createSql = "CREATE DATABASE `" + dbName + "`";
         System.out.println("Executing: " + createSql);
         statusLabel.setText("状态：正在创建数据库 " + dbName + "...");
         if (dataSource == null || dataSource.isClosed()) {
            handleGenericError("连接已关闭", new IllegalStateException("尝试执行创建数据库时连接池已关闭。"));
            statusLabel.setText("状态：创建失败 (连接错误)");
            return;
        }
         try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
             statement.executeUpdate(createSql);
             System.out.println("Database " + dbName + " created successfully.");
             statusLabel.setText("状态：数据库 " + dbName + " 创建成功");
             showAlert("成功", "数据库 '" + dbName + "' 已成功创建。");
             refreshDatabaseStructure();
         } catch (SQLException e) {
             handleDatabaseError("创建数据库 '" + dbName + "' 失败", e);
             statusLabel.setText("状态：创建数据库失败");
         } catch (Exception e) {
             handleGenericError("创建数据库 '" + dbName + "' 时发生意外错误", e);
             statusLabel.setText("状态：创建数据库失败");
         }
    }

    @FXML
    private void handleCreateTable() {
        System.out.println("Action: Create Table triggered");
        TreeItem<String> selectedItem = dbTreeView.getSelectionModel().getSelectedItem();
        if (!isDatabaseNode(selectedItem)) {
            AlertUtil.showWarning("请先选择", "请在左侧导航树中选择要创建表的数据库节点。", null);
            return;
        }
        String dbName = selectedItem.getValue();
        TreeItem<String> dbItem = selectedItem;
        if (dataSource == null || dataSource.isClosed()) {
             AlertUtil.showError("错误", "无有效连接", "数据库连接已关闭。");
            return;
        }
        TextInputDialog dialog = new TextInputDialog("new_table");
        dialog.setTitle("新建表");
        dialog.setHeaderText("在数据库 '" + dbName + "' 中创建新表");
        dialog.setContentText("表名:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(tableName -> {
            if (tableName.trim().isEmpty()) {
                AlertUtil.showWarning("输入无效", "表名称不能为空", null);
                return;
            }
            if (!tableName.matches("^[a-zA-Z0-9_]+$")) {
                 AlertUtil.showWarning("输入无效", "表名称只能包含字母、数字和下划线。", null);
                return;
            }
            executeCreateTableStatement(dbName, tableName.trim(), dbItem);
        });
    }

    private void executeCreateTableStatement(String dbName, String tableName, TreeItem<String> dbItem) {
        String createTableSql = String.format(
            "CREATE TABLE `%s`.`%s` (\n" +
            "  `id` INT NOT NULL AUTO_INCREMENT,\n" +
            "  `name` VARCHAR(255) NULL,\n" +
            "  PRIMARY KEY (`id`)\n" +
            ");", dbName, tableName );
        System.out.println("Executing: " + createTableSql);
        statusLabel.setText("状态：正在创建表 " + dbName + "." + tableName + "...");
        if (dataSource == null || dataSource.isClosed()) {
            handleGenericError("连接已关闭", new IllegalStateException("尝试执行创建表时连接池已关闭。"));
            statusLabel.setText("状态：创建表失败 (连接错误)");
            return;
        }
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSql);
            System.out.println("Table " + dbName + "." + tableName + " created successfully.");
            statusLabel.setText("状态：表 " + tableName + " 创建成功");
            showAlert("成功", "表 '" + tableName + "' 已在数据库 '" + dbName + "' 中成功创建。");
            System.out.println("Refreshing tables for database: " + dbName);
            dbItem.getChildren().clear();
            dbItem.getChildren().add(new TreeItem<>(TABLE_PLACEHOLDER_NODE_VALUE)); // Use correct placeholder
            dbItem.setExpanded(false);
            dbItem.setExpanded(true);
        } catch (SQLException e) {
            handleDatabaseError("创建表 '" + tableName + "' 失败", e);
            statusLabel.setText("状态：创建表失败");
        } catch (Exception e) {
            handleGenericError("创建表 '" + tableName + "' 时发生意外错误", e);
            statusLabel.setText("状态：创建表失败");
        }
    }
    
    @FXML
    private void handleDeleteDatabase() {
        TreeItem<String> selectedItem = dbTreeView.getSelectionModel().getSelectedItem();
        if (!isDatabaseNode(selectedItem)) {
             AlertUtil.showWarning("操作无效", "请先选择要删除的数据库节点。", null);
            return;
        }
        String dbName = selectedItem.getValue();

        // --- ADDED: Prevent deleting system databases ---
        if (isSystemDatabase(dbName)) {
            AlertUtil.showWarning("操作不允许", "无法删除系统数据库", "不能删除 '" + dbName + "'，它是 MySQL 系统数据库。删除它可能会导致 MySQL 实例损坏。");
            return;
        }
        // --- END ADDED --- 

        Alert confirmationDialog = new Alert(AlertType.CONFIRMATION);
        confirmationDialog.setTitle("确认删除数据库");
        confirmationDialog.setHeaderText("警告：删除数据库操作不可逆！");
        confirmationDialog.setContentText("确定要永久删除数据库 '" + dbName + "' 及其所有数据吗？");
        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            executeDropDatabaseStatement(dbName);
        } else {
            System.out.println("Delete database cancelled by user.");
        }
    }

    private void executeDropDatabaseStatement(String dbName) {
        String dropSql = "DROP DATABASE `" + dbName + "`";
        System.out.println("Executing: " + dropSql);
        statusLabel.setText("状态：正在删除数据库 " + dbName + "...");
         if (dataSource == null || dataSource.isClosed()) {
             handleGenericError("连接已关闭", new IllegalStateException("尝试执行删除数据库时连接池已关闭。"));
             statusLabel.setText("状态：删除失败 (连接错误)");
             return;
         }
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(dropSql);
            System.out.println("Database " + dbName + " dropped successfully.");
            statusLabel.setText("状态：数据库 " + dbName + " 已删除");
            showAlert("成功", "数据库 '" + dbName + "' 已成功删除。");
            refreshDatabaseStructure();
        } catch (SQLException e) {
            handleDatabaseError("删除数据库 '" + dbName + "' 失败", e);
            statusLabel.setText("状态：删除数据库失败");
        } catch (Exception e) {
            handleGenericError("删除数据库 '" + dbName + "' 时发生意外错误", e);
            statusLabel.setText("状态：删除数据库失败");
        }
    }

    @FXML
    private void handleDeleteTable() {
        TreeItem<String> selectedItem = dbTreeView.getSelectionModel().getSelectedItem();
        if (!isTableNode(selectedItem)) {
             AlertUtil.showWarning("操作无效", "请先选择要删除的数据表节点。", null);
            return;
        }
        String tableName = selectedItem.getValue();
        TreeItem<String> dbItem = selectedItem.getParent();
        String dbName = dbItem.getValue();
        Alert confirmationDialog = new Alert(AlertType.CONFIRMATION);
        confirmationDialog.setTitle("确认删除数据表");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.setContentText("确定要永久删除数据表 '" + dbName + "." + tableName + "' 吗？");
        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            executeDropTableStatement(dbName, tableName, dbItem);
        } else {
            System.out.println("Delete table cancelled by user.");
        }
    }

     private void executeDropTableStatement(String dbName, String tableName, TreeItem<String> dbItem) {
         String dropSql = String.format("DROP TABLE `%s`.`%s`", dbName, tableName);
         System.out.println("Executing: " + dropSql);
         statusLabel.setText("状态：正在删除表 " + dbName + "." + tableName + "...");
          if (dataSource == null || dataSource.isClosed()) {
              handleGenericError("连接已关闭", new IllegalStateException("尝试执行删除表时连接池已关闭。"));
              statusLabel.setText("状态：删除表失败 (连接错误)");
              return;
          }
         try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
             statement.executeUpdate(dropSql);
             System.out.println("Table " + dbName + "." + tableName + " dropped successfully.");
             statusLabel.setText("状态：表 " + tableName + " 已删除");
             showAlert("成功", "表 '" + tableName + "' 已成功删除。");
             System.out.println("Refreshing tables for database: " + dbName);
             dbItem.getChildren().clear();
             dbItem.getChildren().add(new TreeItem<>(TABLE_PLACEHOLDER_NODE_VALUE)); // Use correct placeholder
             dbItem.setExpanded(false);
             dbItem.setExpanded(true);
         } catch (SQLException e) {
             handleDatabaseError("删除表 '" + tableName + "' 失败", e);
             statusLabel.setText("状态：删除表失败");
         } catch (Exception e) {
             handleGenericError("删除表 '" + tableName + "' 时发生意外错误", e);
             statusLabel.setText("状态：删除表失败");
         }
     }

    // --- Helper Methods ---

    /**
     * 显示一个简单的信息提示框。
     * @param title 对话框标题
     * @param content 对话框内容
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // 不显示头部文本
        alert.setContentText(content);
        alert.showAndWait();
    }

    // --- 数据库连接逻辑 ---

    /**
     * 尝试使用提供的连接详情连接到数据库。
     * @param details 用户输入的连接信息
     */
    private void connectToDatabase(ConnectionDetails details) {
        closeDataSource(); // Close previous connection and clear state
        HikariConfig config = new HikariConfig();
        // 确保 JDBC 驱动类被加载 (对于现代 JDBC 驱动通常不需要，但有时显式加载有帮助)
        // try {
        //     Class.forName("com.mysql.cj.jdbc.Driver");
        // } catch (ClassNotFoundException e) {
        //      System.err.println("MySQL JDBC Driver not found!");
        //      AlertUtil.showError("驱动错误", "未找到 MySQL JDBC 驱动", "请确保 mysql-connector-j 依赖已添加到 pom.xml。");
        //      statusLabel.setText("状态：驱动错误");
        //      return;
        // }
        config.setJdbcUrl(details.getJdbcUrl());
        config.setUsername(details.getUsername());
        config.setPassword(details.getPassword());
        config.addDataSourceProperty("cachePrepStmts", "true"); // 推荐参数
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        // 可以设置连接池大小等参数
        config.setMaximumPoolSize(5); // 连接池最大连接数
        config.setMinimumIdle(1);      // 最小空闲连接数
        config.setConnectionTimeout(10000); // 连接超时时间 (毫秒, 增加到10秒)
        config.setIdleTimeout(600000); // 空闲连接超时时间 (毫秒, 10分钟)
        config.setMaxLifetime(1800000); // 连接最大生命周期 (毫秒, 30分钟)

        try {
            System.out.println("Creating HikariDataSource with URL: " + details.getJdbcUrl());
            dataSource = new HikariDataSource(config); // 创建连接池
            System.out.println("HikariDataSource created. Testing connection...");

            // 尝试获取一个连接来测试是否成功
            try (Connection connection = dataSource.getConnection()) { // try-with-resources 确保连接被关闭
                // --- Store connection name on successful connection --- 
                this.currentConnectionName = details.getHost() + ":" + details.getPort();
                System.out.println("Database connection successful! Name: " + this.currentConnectionName);
                statusLabel.setText("状态：已连接到 " + this.currentConnectionName);

                // 连接成功后，加载数据库结构到 TreeView
                loadDatabaseStructure(connection, this.currentConnectionName);

            } catch (SQLException testEx) {
                 handleDatabaseError("获取初始数据库连接失败", testEx);
                 statusLabel.setText("状态：连接失败");
                 closeDataSource(); // Clean up if test connection failed
            }

        } catch (Exception e) { // 捕获 HikariCP 初始化可能抛出的异常 (如配置错误)
            System.err.println("Failed to create HikariDataSource: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("连接失败", "无法初始化连接池", "错误: " + e.getMessage());
            statusLabel.setText("状态：连接失败");
            closeDataSource(); // Clean up if datasource creation failed
        }
    }

    /**
     * 关闭当前的数据库连接池（如果存在）。
     */
    private void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            System.out.println("Closing existing HikariDataSource...");
            dataSource.close();
            System.out.println("HikariDataSource closed.");
        }
        // --- Clear state regardless of whether pool was open --- 
        dataSource = null;
        this.currentConnectionName = null; // Clear stored name
        TreeItem<String> root = dbTreeView.getRoot();
        if (root != null) {
            root.getChildren().clear();
            root.setValue("Connections (Not Connected)");
        }
        statusLabel.setText("状态：未连接");
    }

    /**
     * 加载数据库结构到左侧的 TreeView。
     * @param connection 数据库连接 (注意：这个连接用完后不应关闭，由连接池管理)
     * @param connectionName 连接的显示名称
     */
    private void loadDatabaseStructure(Connection connection, String connectionName) {
        TreeItem<String> root = dbTreeView.getRoot();
        if (root == null) {
            root = new TreeItem<>("Connections");
            dbTreeView.setRoot(root);
        }
        root.getChildren().clear();

        TreeItem<String> connectionItem = new TreeItem<>(connectionName);
        root.getChildren().add(connectionItem);

        try {
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("Fetching database catalogs...");

            try (ResultSet catalogs = metaData.getCatalogs()) {
                while (catalogs.next()) {
                    String dbName = catalogs.getString(1);
                    System.out.println("Found catalog: " + dbName);
                    TreeItem<String> dbItem = new TreeItem<>(dbName);
                    TreeItem<String> placeholder = new TreeItem<>(TABLE_PLACEHOLDER_NODE_VALUE);
                    dbItem.getChildren().add(placeholder);
                    connectionItem.getChildren().add(dbItem);

                    // Add listener to load tables when expanded
                    addExpansionListener(dbItem, dbName, null);
                }
            }
            System.out.println("Finished fetching catalogs.");
            connectionItem.setExpanded(true);

        } catch (SQLException e) {
            handleDatabaseError("加载数据库列表失败", e);
            root.getChildren().remove(connectionItem);
        } catch (Exception e) {
             handleGenericError("加载数据库结构时出错", e);
            root.getChildren().remove(connectionItem);
        }
    }

    private void loadTablesForDatabase(String dbName, TreeItem<String> dbItem) {
        if (dataSource == null || dataSource.isClosed()) {
             handleGenericError("无法加载表", new IllegalStateException("数据库连接已关闭。"));
            return;
        }

        dbItem.getChildren().setAll(new TreeItem<>("正在加载表...")); // 替换占位符

        // TODO: 将此操作移至后台线程
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("Fetching tables for database: " + dbName);
            try (ResultSet tables = metaData.getTables(dbName, null, "%", new String[]{"TABLE"})) {
                dbItem.getChildren().clear();
                boolean foundTables = false;
                while (tables.next()) {
                    foundTables = true;
                    String tableName = tables.getString("TABLE_NAME");
                    TreeItem<String> tableItem = new TreeItem<>(tableName);
                     // TODO: Add icon for table
                     // TODO: 可以为表添加虚拟子节点以加载列
                    dbItem.getChildren().add(tableItem);
                }
                 if (!foundTables) {
                     dbItem.getChildren().add(new TreeItem<>("(无表)"));
                 }
            }
            System.out.println("Finished fetching tables for " + dbName);

        } catch (SQLException e) {
            handleDatabaseError("加载数据库 '" + dbName + "' 的表失败", e);
            dbItem.getChildren().setAll(new TreeItem<>("(加载表失败)"));
        } catch (Exception e) {
            handleGenericError("加载数据库 '" + dbName + "' 的表时发生意外错误", e);
            dbItem.getChildren().setAll(new TreeItem<>("(加载表失败)"));
        }
    }

    // --- Refactored Listener Adder (removed getProperties) --- 
    private void addExpansionListener(TreeItem<String> item, String dbName, String tableName) {
         // Removed getProperties check - listener might be added multiple times,
         // but the inner logic prevents redundant loading.
         item.expandedProperty().addListener((observable, oldValue, newValue) -> {
             if (newValue && !item.getChildren().isEmpty()) {
                 TreeItem<String> firstChild = item.getChildren().get(0);
                 if (firstChild != null && firstChild.getValue() != null) {
                     String placeholderValue = firstChild.getValue();
                     if (tableName == null && placeholderValue.equals(TABLE_PLACEHOLDER_NODE_VALUE)) {
                         System.out.println("Listener triggered for expanding database: " + dbName);
                         loadTablesForDatabase(dbName, item);
                     } else if (tableName != null && placeholderValue.equals(COLUMN_PLACEHOLDER_NODE_VALUE)) {
                         System.out.println("Listener triggered for expanding table: " + dbName + "." + tableName);
                         loadColumnsForTable(dbName, tableName, item);
                     }
                 }
             }
         });
    }

    // --- 统一错误处理方法 ---
    private void handleDatabaseError(String context, SQLException e) {
        System.err.println(context + ": " + e.getMessage() + " (SQLState: " + e.getSQLState() + ", ErrorCode: " + e.getErrorCode() + ")");
        e.printStackTrace(); // 打印完整堆栈跟踪以便调试
        // 使用 AlertUtil 显示错误给用户
        AlertUtil.showError("数据库错误", context, e.getMessage());
        // 更新状态栏，指示错误
        statusLabel.setText("状态：数据库错误");
    }

    private void handleGenericError(String context, Exception e) {
         System.err.println(context + ": " + e.getMessage());
         e.printStackTrace();
         // 使用 AlertUtil 显示错误给用户
         AlertUtil.showError("错误", context, e.getMessage());
          // 更新状态栏，指示错误
        statusLabel.setText("状态：操作失败");
    }

    // --- 新增 SQL 执行处理方法 ---
    @FXML
    private void handleExecuteSql() {
        String sql = sqlTextArea.getText();
        if (sql == null || sql.trim().isEmpty()) {
            showAlert("提示", "请输入要执行的 SQL 语句。");
            return;
        }
        if (dataSource == null || dataSource.isClosed()) {
            AlertUtil.showError("错误", "无法执行 SQL", "请先连接到数据库。");
            return;
        }

        System.out.println("Executing SQL: " + sql.substring(0, Math.min(sql.length(), 100)).replace("\n", " ") + "...");
        statusLabel.setText("状态：正在执行 SQL...");
        resultTableView.getColumns().clear(); // Clear table before execution
        resultTableView.getItems().clear();

        // TODO: 在后台线程执行 SQL
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            boolean hasResultSet = false;
             try {
                  hasResultSet = statement.execute(sql);
             } catch (SQLException ex) {
                 handleDatabaseError("执行 SQL 时出错", ex);
                 statusLabel.setText("状态：SQL 执行失败");
                 return;
             }

            if (hasResultSet) {
                System.out.println("SQL executed, result is a ResultSet.");
                try (ResultSet resultSet = statement.getResultSet()) {
                    // --- 调用 displayResultSet 显示结果 --- 
                    displayResultSet(resultSet);
                    statusLabel.setText("状态：查询完成");
                }
            } else {
                int updateCount = statement.getUpdateCount();
                System.out.println("SQL executed, result is an update count: " + updateCount);
                statusLabel.setText("状态：更新完成，影响行数：" + updateCount);
                showAlert("执行成功", "SQL 执行成功。\n影响行数：" + updateCount);
                // Table remains empty (already cleared)
            }

        } catch (SQLException e) {
            handleDatabaseError("执行 SQL 时出错", e);
            statusLabel.setText("状态：SQL 执行失败");
        } catch (Exception e) {
             handleGenericError("执行 SQL 时发生意外错误", e);
             statusLabel.setText("状态：SQL 执行失败 (意外错误)");
        }
    }

    // --- RE-ADD displayResultSet Method --- 
    private void displayResultSet(ResultSet rs) throws SQLException {
        resultTableView.getColumns().clear();
        resultTableView.getItems().clear();

        if (rs == null) {
            System.out.println("ResultSet is null, cannot display.");
            return;
        }

        java.sql.ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        // 1. Create TableColumns dynamically
        for (int i = 1; i <= columnCount; i++) {
            final int columnIndex = i - 1; 
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(rsmd.getColumnLabel(i));

            column.setCellValueFactory(param -> {
                ObservableList<String> rowData = param.getValue();
                if (rowData != null && columnIndex < rowData.size()) {
                    String cellValue = rowData.get(columnIndex);
                    return new SimpleStringProperty(cellValue != null ? cellValue : "(NULL)");
                } else {
                    return new SimpleStringProperty("");
                }
            });

            column.setPrefWidth(120); // Increase default width slightly
            resultTableView.getColumns().add(column);
        }

        // 2. Populate data into an ObservableList
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        int rowLimit = 1000; // Limit rows for performance
        int currentRow = 0;
        while (rs.next() && currentRow < rowLimit) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getString(i)); 
            }
            data.add(row);
            currentRow++;
        }

        // 3. Set items to the TableView
        resultTableView.setItems(data);
        System.out.println("Displayed " + data.size() + " rows in the table.");
        if (currentRow == rowLimit && rs.next()) { // Check if limit was reached and there are more rows
             statusLabel.setText("状态：查询完成 (仅显示前 " + rowLimit + " 行)");
             System.out.println("... (Results limited to " + rowLimit + " rows)");
        }
    }
    // --- END RE-ADD displayResultSet Method ---

    /**
     * 刷新左侧 TreeView 的数据库结构。
     */
    private void refreshDatabaseStructure() {
        if (dataSource == null || dataSource.isClosed() || currentConnectionName == null) { // Check currentConnectionName too
            System.out.println("Cannot refresh structure: No active data source or connection name.");
            TreeItem<String> root = dbTreeView.getRoot();
             if (root != null) {
                 root.getChildren().clear();
                 root.setValue("Connections (Not Connected)");
             }
            statusLabel.setText("状态：未连接");
            currentConnectionName = null; // Ensure consistency
            return;
        }

        System.out.println("Refreshing database structure for connection: " + currentConnectionName);
        try (Connection connection = dataSource.getConnection()) {
            // Use the stored connection name
            loadDatabaseStructure(connection, currentConnectionName);
            statusLabel.setText("状态：数据库列表已刷新");
        } catch (SQLException e) {
            handleDatabaseError("刷新数据库结构时获取连接失败", e);
            statusLabel.setText("状态：刷新失败 (连接错误)");
        } catch (Exception e) {
             handleGenericError("刷新数据库结构时发生意外错误", e);
             statusLabel.setText("状态：刷新失败");
        }
    }

    // --- ADDED/RE-ADDED Methods ---
    private void loadColumnsForTable(String dbName, String tableName, TreeItem<String> tableItem) {
         if (dataSource == null || dataSource.isClosed()) {
             handleGenericError("无法加载列", new IllegalStateException("数据库连接已关闭。"));
             return;
         }

         tableItem.getChildren().setAll(new TreeItem<>("正在加载列..."));

         // TODO: Background task
         try (Connection connection = dataSource.getConnection()) {
             DatabaseMetaData metaData = connection.getMetaData();
             System.out.println("Fetching columns for table: " + dbName + "." + tableName);

             try (ResultSet columns = metaData.getColumns(dbName, null, tableName, "%" )) {
                 tableItem.getChildren().clear();
                 boolean foundColumns = false;
                 while (columns.next()) {
                     foundColumns = true;
                     String columnName = columns.getString("COLUMN_NAME");
                     String columnType = columns.getString("TYPE_NAME");
                     int columnSize = columns.getInt("COLUMN_SIZE");
                     // String isNullable = columns.getString("IS_NULLABLE"); // "YES" or "NO"
                     
                     String displayString = String.format("%s (%s%s)",
                                                      columnName,
                                                      columnType,
                                                      (columnSize > 0 && (columnType.contains("CHAR") || columnType.contains("VARCHAR") || columnType.contains("BINARY") || columnType.contains("TEXT")) ? "(" + columnSize + ")" : ""));

                     TreeItem<String> columnItem = new TreeItem<>(displayString);
                     tableItem.getChildren().add(columnItem);
                 }
                  if (!foundColumns) {
                      tableItem.getChildren().add(new TreeItem<>("(无列)"));
                  }
             }
             System.out.println("Finished fetching columns for " + dbName + "." + tableName);

         } catch (SQLException e) {
             handleDatabaseError("加载表 '" + tableName + "' 的列失败", e);
             tableItem.getChildren().setAll(new TreeItem<>("(加载列失败)"));
         } catch (Exception e) {
             handleGenericError("加载表 '" + tableName + "' 的列时发生意外错误", e);
             tableItem.getChildren().setAll(new TreeItem<>("(加载列失败)"));
         }
    }

    private boolean isSystemDatabase(String dbName) {
        return dbName.equalsIgnoreCase("information_schema") ||
               dbName.equalsIgnoreCase("mysql") ||
               dbName.equalsIgnoreCase("performance_schema") ||
               dbName.equalsIgnoreCase("sys");
    }
    // --- END ADDED/RE-ADDED Methods ---

    // --- ADDED: CodeArea Initialization and Highlighting Logic ---
    private void initializeSqlCodeArea() {
        System.out.println("Initializing CodeArea for SQL highlighting...");
        sqlTextArea.setParagraphGraphicFactory(LineNumberFactory.get(sqlTextArea));

        // Apply styling when text changes
        sqlTextArea.multiPlainChanges()
                   .successionEnds(Duration.ofMillis(200))
                   .subscribe(ignore -> sqlTextArea.setStyleSpans(0, computeHighlighting(sqlTextArea.getText())));

        // Initial highlighting
        if (!sqlTextArea.getText().isEmpty()) {
            sqlTextArea.setStyleSpans(0, computeHighlighting(sqlTextArea.getText()));
        }
        System.out.println("CodeArea highlighting initialized.");
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("MLCOMMENT") != null ? "comment" :
                    matcher.group("COMMENT") != null ? "comment" :
                    matcher.group("NUMBER") != null ? "number" :
                    null; /* should not happen */
            if (styleClass == null) continue; // Avoid assertion error if a group doesn't match expectedly

            // Add non-styled text before the match
            if (matcher.start() > lastKwEnd) {
                spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            }
            // Add styled text for the match
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        // Add any remaining non-styled text at the end
        if (text.length() > lastKwEnd) {
            spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        }
        return spansBuilder.create();
    }
    // --- END ADDED CodeArea Logic ---
}