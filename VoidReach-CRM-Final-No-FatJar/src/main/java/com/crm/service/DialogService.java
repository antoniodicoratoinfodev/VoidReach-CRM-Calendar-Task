package com.crm.service;

import javafx.scene.Node;
import javafx.scene.control.Alert;

import java.util.Objects;

/** Centralizes themed informational and error alerts. */
public final class DialogService {
    private final ThemeService themeService;

    public DialogService(ThemeService themeService) {
        this.themeService = Objects.requireNonNull(themeService);
    }

    public void showError(String title, String content) {
        Alert alert = create(Alert.AlertType.ERROR, title, content);
        Node contentLabel = alert.getDialogPane().lookup(".content.label");
        if (contentLabel != null) contentLabel.setStyle("-fx-text-fill: #fca5a5;");
        alert.showAndWait();
    }

    public void showInfo(String title, String content) {
        create(Alert.AlertType.INFORMATION, title, content).showAndWait();
    }

    private Alert create(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        themeService.applyTo(alert);
        return alert;
    }
}
