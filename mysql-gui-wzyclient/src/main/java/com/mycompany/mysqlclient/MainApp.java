package com.mycompany.mysqlclient; // 修改成你的包名

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * JavaFX Main Application Class
 */
public class MainApp extends Application {

    private static Scene scene; // Make scene accessible if needed later

    @Override
    public void start(Stage stage) throws IOException {
        // 注意：这里使用 getResource 相对于类的路径来加载 FXML
        // 确保 main-view.fxml 在 src/main/resources/com/mycompany/mysqlclient/ 目录下
        URL fxmlLocation = getClass().getResource("main-view.fxml");
        if (fxmlLocation == null) {
            System.err.println("Cannot find FXML file: main-view.fxml");
            System.err.println("Ensure it's in the correct resources directory matching the package structure.");
            // 也可以加载相对于资源根目录的路径
            // fxmlLocation = getClass().getClassLoader().getResource("com/mycompany/mysqlclient/main-view.fxml");
            // if (fxmlLocation == null) {
            //     System.err.println("Cannot find FXML file even in root: com/mycompany/mysqlclient/main-view.fxml");
            //     return;
            // }
            return; // 如果找不到文件，直接返回避免 NullPointerException
        }
        System.out.println("FXML Location: " + fxmlLocation);
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load(); // 加载 FXML

        // Create the scene
        scene = new Scene(root, 800, 600); // Initial size

        // --- ADDED: Load the CSS stylesheet for SQL syntax highlighting ---
        URL cssUrl = getClass().getResource("sql-syntax.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
            System.out.println("Loaded CSS: " + cssUrl.toExternalForm());
        } else {
            System.err.println("Warning: Could not find sql-syntax.css");
        }
        // --- END ADDED ---

        // Set the scene on the stage
        stage.setScene(scene);
        stage.setTitle("MySQL GUI Client"); // 设置窗口标题

        // Optional: Add logic to properly close resources on exit
        MainController controller = loader.getController();
        stage.setOnCloseRequest(event -> {
            System.out.println("Window close requested.");
            controller.handleExitRequest(); // Call a method in controller to clean up
            // Note: System.exit(0) is called within handleExitRequest now
        });

        // Show the stage
        stage.show(); // 显示窗口
    }

    public static void main(String[] args) {
        launch(args); // 启动 JavaFX 应用
    }

} 