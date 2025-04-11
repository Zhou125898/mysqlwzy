package com.mycompany.mysqlclient;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Utility class for showing common JavaFX Alerts.
 */
public class AlertUtil {

    /**
     * Shows an error alert dialog.
     * @param title The title of the dialog window.
     * @param header The header text (can be null).
     * @param content The main content message.
     */
    public static void showError(String title, String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Shows an information alert dialog.
     * @param title The title of the dialog window.
     * @param header The header text (can be null).
     * @param content The main content message.
     */
    public static void showInfo(String title, String header, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

     /**
     * Shows a warning alert dialog.
     * @param title The title of the dialog window.
     * @param header The header text (can be null).
     * @param content The main content message.
     */
    public static void showWarning(String title, String header, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Можно добавить и другие типы оповещений (CONFIRMATION и т.д.)
} 