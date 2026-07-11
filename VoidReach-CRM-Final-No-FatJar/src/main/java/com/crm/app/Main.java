package com.crm.app;

import com.crm.controller.SplashScreenController;
import com.crm.controller.LoginController;
import com.crm.controller.MainController;
import com.crm.model.UserAccount;
import com.crm.repository.LocalUserRepository;
import com.crm.service.SessionService;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.Taskbar;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class Main extends Application {

    private static final String APP_ICON_PATH = "/images/app-icon.png";

    private Stage mainStage;
    private Stage splashStage;
    private SplashScreenController splashController;
    private Parent loginRoot;
    private LoginController loginController;
    private MainController rememberedAppController;
    private UserAccount rememberedUser;
    private final SessionService sessionService = new SessionService(new LocalUserRepository());

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.mainStage = primaryStage;
        configureMacDockIcon();
        showSplashScreen();
    }

    private void showSplashScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/crm/view/SplashScreen.fxml"));
        Parent root = loader.load();
        splashController = loader.getController();

        splashStage = new Stage();
        splashStage.initStyle(StageStyle.TRANSPARENT);
        splashStage.setAlwaysOnTop(true);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/css/style-dark.css").toExternalForm());
        splashStage.setScene(scene);
        
        addAppIcon(splashStage);
        splashStage.centerOnScreen();
        splashStage.show();
        splashStage.toFront();
        splashStage.requestFocus();

        startLoadingTask();
    }

    private void startLoadingTask() {
        Task<UserAccount> loadTask = new Task<>() {
            @Override
            protected UserAccount call() throws Exception {
                // 1. Core Initialization
                updateMessage("Core Initialization...");
                updateProgress(0.1, 1.0);
                Thread.sleep(400); 

                // 2. Authentication and session loading
                updateMessage("Checking saved session...");
                updateProgress(0.3, 1.0);
                UserAccount savedUser = sessionService.getRememberedUser().orElse(null);
                if (savedUser != null) updateMessage("Loading your workspace...");
                updateProgress(0.6, 1.0);
                Thread.sleep(300);

                // 3. Database and Model Setup
                updateMessage("Preparing local account storage...");
                updateProgress(0.8, 1.0);
                Thread.sleep(400);

                // 4. Finalization
                updateMessage("Interface Optimization...");
                updateProgress(1.0, 1.0);
                Thread.sleep(300);

                return savedUser;
            }
        };

        loadTask.messageProperty().addListener((obs, old, msg) -> splashController.setStatus(msg));
        loadTask.progressProperty().addListener((obs, old, prog) -> splashController.setProgress(prog.doubleValue()));

        loadTask.setOnSucceeded(e -> {
            try {
                rememberedUser = loadTask.getValue();
                loadLoginView();
                if (rememberedUser != null) loadAndShowRememberedApp();
                else transitionToLogin();
            } catch (Exception ex) {
                throw new IllegalStateException("Impossibile caricare l'interfaccia", ex);
            }
        });

        Thread loaderThread = new Thread(loadTask, "voidreach-startup-loader");
        loaderThread.setDaemon(true);
        loaderThread.start();
    }

    private void loadLoginView() throws Exception {
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/com/crm/view/LoginView.fxml"));
        loginRoot = loginLoader.load();
        loginController = loginLoader.getController();
        configureLoginHandler();
    }

    private void loadAndShowRememberedApp() throws Exception {
        FXMLLoader appLoader = new FXMLLoader(getClass().getResource("/com/crm/view/MainView.fxml"));
        Parent root = appLoader.load();
        rememberedAppController = appLoader.getController();
        transitionToRememberedApp(root);
    }

    private void transitionToLogin() {
        if (loginRoot == null) return;
        loginController.resetForLoginScreen();

        // 1. Main Stage Setup
        mainStage.setTitle("VoidReach CRM — Accesso");
        addAppIcon(mainStage);
        
        Scene scene = new Scene(loginRoot);
        scene.setFill(Color.web("#0f172a"));
        scene.getStylesheets().add(getClass().getResource("/css/style-dark.css").toExternalForm());
        
        mainStage.setScene(scene);
        mainStage.setMaximized(false);
        mainStage.setWidth(580);
        mainStage.setHeight(700);
        mainStage.centerOnScreen();
        showMainStage(loginController::requestInitialFocus);
    }

    private void transitionToRememberedApp(Parent root) {
        if (root == null || rememberedAppController == null) return;
        mainStage.setTitle("VoidReach CRM");
        addAppIcon(mainStage);
        rememberedAppController.setCurrentUser(rememberedUser, this::logout);
        Scene scene = new Scene(root);
        scene.setFill(Color.web("#0f172a"));
        scene.getStylesheets().add(getClass().getResource("/css/style-dark.css").toExternalForm());
        mainStage.setScene(scene);
        mainStage.setMaximized(true);
        showMainStage(rememberedAppController::requestInitialFocus);
    }

    private void configureLoginHandler() {
        loginController.setOnAuthenticated((user, remember) -> {
            if (remember) sessionService.remember(user);
            else sessionService.forget();
            showMainApplication(user);
        });
    }

    private void showMainApplication(UserAccount user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/crm/view/MainView.fxml"));
            Parent root = loader.load();
            MainController controller = loader.getController();
            controller.setCurrentUser(user, this::logout);
            Scene scene = new Scene(root);
            scene.setFill(Color.web("#0f172a"));
            scene.getStylesheets().add(getClass().getResource("/css/style-dark.css").toExternalForm());
            mainStage.setTitle("VoidReach CRM");
            addAppIcon(mainStage);
            mainStage.setScene(scene);
            mainStage.setMaximized(true);
            mainStage.show();
            mainStage.toFront();
            mainStage.requestFocus();
            Platform.runLater(controller::requestInitialFocus);
        } catch (Exception e) {
            throw new IllegalStateException("Impossibile aprire l'applicazione", e);
        }
    }

    private void showLoginScreen() {
        mainStage.setTitle("VoidReach CRM — Accesso");
        // The login scene is retained for the application's lifetime; reset its form state on every logout.
        loginController.resetForLoginScreen();
        configureLoginHandler();
        Scene loginScene = new Scene(loginRoot);
        loginScene.setFill(Color.web("#0f172a"));
        loginScene.getStylesheets().add(getClass().getResource("/css/style-dark.css").toExternalForm());
        mainStage.setScene(loginScene);
        mainStage.setMaximized(false);
        mainStage.setWidth(580);
        mainStage.setHeight(700);
        mainStage.centerOnScreen();
        bringToFront(loginController::requestInitialFocus);
    }

    private void logout() {
        sessionService.forget();
        showLoginScreen();
    }

    private void showMainStage(Runnable focusTarget) {
        if (splashStage != null) splashStage.close();
        mainStage.setOpacity(1.0);
        bringToFront(focusTarget);
    }

    /** Focuses the JavaFX window without starting a second native GUI toolkit. */
    private void bringToFront(Runnable focusTarget) {
        mainStage.setIconified(false);
        mainStage.show();
        mainStage.toFront();
        mainStage.requestFocus();
        Platform.runLater(() -> {
            if (!mainStage.isShowing()) return;
            mainStage.toFront();
            mainStage.requestFocus();
            focusTarget.run();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void addAppIcon(Stage stage) {
        if (stage == null || stage.getIcons().isEmpty()) {
            Image icon = new Image(getClass().getResourceAsStream(APP_ICON_PATH));
            if (stage != null) stage.getIcons().add(icon);
        }
    }

    private void configureMacDockIcon() {
        if (!System.getProperty("os.name", "").toLowerCase().contains("mac")) return;
        if (!Taskbar.isTaskbarSupported()) return;

        Taskbar taskbar = Taskbar.getTaskbar();
        if (!taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) return;

        try (InputStream icon = getClass().getResourceAsStream(APP_ICON_PATH)) {
            if (icon != null) taskbar.setIconImage(ImageIO.read(icon));
        } catch (Exception ignored) {
            // The JavaFX stage icon still applies if macOS does not allow changing the Dock icon.
        }
    }
}
