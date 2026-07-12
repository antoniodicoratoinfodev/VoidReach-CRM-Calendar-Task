package com.crm.controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Objects;

/** Owns sidebar selection and switching between the main application views. */
public final class NavigationController {
    private final VBox contactsView;
    private final VBox calendarView;
    private final VBox genericView;
    private final Label genericTitle;
    private final FontIcon genericIcon;
    private final VBox sidebarContainer;

    public NavigationController(VBox contactsView, VBox calendarView, VBox genericView,
                                Label genericTitle, FontIcon genericIcon, VBox sidebarContainer) {
        this.contactsView = Objects.requireNonNull(contactsView);
        this.calendarView = Objects.requireNonNull(calendarView);
        this.genericView = Objects.requireNonNull(genericView);
        this.genericTitle = Objects.requireNonNull(genericTitle);
        this.genericIcon = Objects.requireNonNull(genericIcon);
        this.sidebarContainer = Objects.requireNonNull(sidebarContainer);
    }

    public void initialize() {
        bindManagedToVisible(contactsView);
        bindManagedToVisible(calendarView);
        bindManagedToVisible(genericView);
    }

    public void navigate(ActionEvent event) {
        Button button = (Button) event.getSource();
        updateActiveStyles(button);
        String id = button.getId();
        hideAll();
        if (id.contains("Contacts")) contactsView.setVisible(true);
        else if (id.contains("Calendar")) calendarView.setVisible(true);
        else showPlaceholder(id);
    }

    public void showCalendar() {
        hideAll();
        calendarView.setVisible(true);
        updateActiveStyles(null);
    }

    private void bindManagedToVisible(Node view) {
        view.managedProperty().bind(view.visibleProperty());
    }

    private void hideAll() {
        contactsView.setVisible(false);
        calendarView.setVisible(false);
        genericView.setVisible(false);
    }

    private void showPlaceholder(String id) {
        genericView.setVisible(true);
        if (id.contains("Home")) setPlaceholder("Home", "fas-home");
        else if (id.contains("Dashboard")) setPlaceholder("Dashboard", "fas-th-large");
        else if (id.contains("Leads")) setPlaceholder("Leads", "fas-bullseye");
        else if (id.contains("Deals") || id.contains("Opportunities")) setPlaceholder("Opportunities", "fas-handshake");
        else if (id.contains("Accounts")) setPlaceholder("Accounts", "fas-building");
        else if (id.contains("Tasks")) setPlaceholder("Tasks", "fas-tasks");
        else if (id.contains("Settings")) setPlaceholder("Settings", "fas-cog");
    }

    private void setPlaceholder(String title, String icon) {
        genericTitle.setText(title);
        genericIcon.setIconLiteral(icon);
    }

    private void updateActiveStyles(Button selected) {
        for (Node node : sidebarContainer.getChildren()) {
            if (node instanceof Button) node.getStyleClass().remove("sidebar-button-active");
        }
        if (selected != null && selected.getStyleClass().contains("sidebar-button")) {
            selected.getStyleClass().add("sidebar-button-active");
        }
    }
}
