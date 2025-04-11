package com.mycompany.mysqlclient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert; // 导入 Alert 类

public class ConnectionDialogController {

    @FXML
    private TextField hostField;

    @FXML
    private TextField portField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField databaseField;

    @FXML
    private Button connectButton;

    @FXML
    private Button cancelButton;

    // 用于存储用户输入的连接信息，或者只是一个标志表示用户是否点击了连接
    private ConnectionDetails connectionDetails = null;
    private boolean okClicked = false;

    /**
     * 初始化方法，可以在这里添加输入验证等
     */
    @FXML
    private void initialize() {
        // 可以添加监听器来实时验证输入，例如确保端口是数字
        portField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                portField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    /**
     * 处理连接按钮点击事件
     */
    @FXML
    private void handleConnect(ActionEvent event) {
        if (isInputValid()) {
            connectionDetails = new ConnectionDetails(
                    hostField.getText(),
                    portField.getText(),
                    usernameField.getText(),
                    passwordField.getText(),
                    databaseField.getText() // 可能为空字符串
            );
            okClicked = true;
            closeDialog(event);
        } else {
            // 显示错误提示 (或者在 isInputValid 内部显示)
             AlertUtil.showError("输入错误", "请检查输入字段。", "主机、端口和用户名不能为空。端口必须是数字。");
        }
    }

    /**
     * 处理取消按钮点击事件
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        okClicked = false;
        closeDialog(event);
    }

    /**
     * 验证用户输入是否有效 (基本验证)
     * @return 如果输入有效返回 true，否则 false
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (hostField.getText() == null || hostField.getText().trim().isEmpty()) {
            errorMessage += "主机不能为空!\n";
        }
        if (portField.getText() == null || portField.getText().trim().isEmpty()) {
            errorMessage += "端口不能为空!\n";
        } else {
            try {
                Integer.parseInt(portField.getText().trim());
            } catch (NumberFormatException e) {
                errorMessage += "端口必须是有效的数字!\n";
            }
        }
        if (usernameField.getText() == null || usernameField.getText().trim().isEmpty()) {
            errorMessage += "用户名不能为空!\n";
        }
        // 密码可以为空

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            // 可以选择在这里显示错误提示
             // AlertUtil.showError("输入无效", "请修正以下错误:", errorMessage);
            return false;
        }
    }


    /**
     * 获取用户是否点击了连接按钮
     * @return 如果点击了连接按钮返回 true
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * 获取用户输入的连接详情
     * @return ConnectionDetails 对象，如果用户点击取消则为 null
     */
    public ConnectionDetails getConnectionDetails() {
        return connectionDetails;
    }

    /**
     * 关闭对话框窗口
     */
    private void closeDialog(ActionEvent event) {
         // 获取按钮所在的 Stage 并关闭它
        Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
        stage.close();
    }

    // --- 内部类用于封装连接信息 ---
    public static class ConnectionDetails {
        private final String host;
        private final String port;
        private final String username;
        private final String password;
        private final String database; // 可能为空

        public ConnectionDetails(String host, String port, String username, String password, String database) {
            this.host = host;
            this.port = port;
            this.username = username;
            this.password = password;
            this.database = (database == null || database.trim().isEmpty()) ? null : database.trim();
        }

        public String getHost() { return host; }
        public String getPort() { return port; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getDatabase() { return database; } // 可能返回 null

        public String getJdbcUrl() {
            StringBuilder url = new StringBuilder("jdbc:mysql://");
            url.append(host).append(":").append(port);
            if (database != null && !database.isEmpty()) {
                url.append("/").append(database);
            }
            // 添加推荐的连接参数
            url.append("?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
            return url.toString();
        }
    }

    // --- AlertUtil 已移至单独文件 ---

}