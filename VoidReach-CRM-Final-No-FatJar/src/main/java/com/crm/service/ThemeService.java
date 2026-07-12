package com.crm.service;

import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Window;

import java.util.Objects;
import java.util.function.Supplier;

/** Owns the active application theme and applies it consistently to scenes and dialogs. */
public final class ThemeService {
    private static final String DARK_STYLESHEET = "/css/style-dark.css";
    private static final String LIGHT_STYLESHEET = "/css/style.css";

    private final Supplier<Window> ownerSupplier;
    private boolean darkMode = true;

    public ThemeService(Supplier<Window> ownerSupplier) {
        this.ownerSupplier = Objects.requireNonNull(ownerSupplier);
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void toggle() {
        darkMode = !darkMode;
    }

    public void applyTo(Scene scene) {
        if (scene == null) return;
        scene.getStylesheets().setAll(stylesheet());
    }

    public void applyTo(Dialog<?> dialog) {
        if (dialog == null) return;
        Window owner = ownerSupplier.get();
        if (owner != null && dialog.getOwner() == null) dialog.initOwner(owner);
        DialogPane pane = dialog.getDialogPane();
        pane.getStylesheets().setAll(stylesheet());
        if (!pane.getStyleClass().contains("dialog-pane")) pane.getStyleClass().add("dialog-pane");
    }

    private String stylesheet() {
        return Objects.requireNonNull(
                ThemeService.class.getResource(darkMode ? DARK_STYLESHEET : LIGHT_STYLESHEET),
                "Theme stylesheet is missing").toExternalForm();
    }
}
