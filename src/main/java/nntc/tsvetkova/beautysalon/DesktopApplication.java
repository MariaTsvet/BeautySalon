package nntc.tsvetkova.beautysalon;

import javafx.application.Platform;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.prefs.Preferences;

public class DesktopApplication extends Application {

    private static final double MIN_WIDTH = 600.0;
    private static final double MIN_HEIGHT = 300.0;

    @Override
    public void start(Stage stage) throws IOException {

        DatabaseManager dbManager = new DatabaseManager();

        try {
            showMainWindow(stage, dbManager);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            showLoginWindow();
        }
    }

        private void showLoginWindow() throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            VBox infoContent = fxmlLoader.load();

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Настройки доступа к СУБД");
            dialog.getDialogPane().setContent(infoContent);
            Stage stageLogin = (Stage) dialog.getDialogPane().getScene().getWindow();

            stageLogin.setOnCloseRequest(event -> {
                System.out.println("Закрытие окна настроек доступа к СУБД...");
                dialog.close(); // Закрыть диалог
            });

            dialog.showAndWait();
        }

    private void showMainWindow(Stage stage, DatabaseManager dbManager) throws IOException, SQLException {

        dbManager.connect();
        dbManager.ensureTablesExists();

        FXMLLoader fxmlLoader = new FXMLLoader(DesktopApplication.class.getResource("main-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        // Получаем контроллер и передаем Stage
        DesktopController controller = fxmlLoader.getController();
        controller.setPrimaryStage(stage);

        controller.setPrimaryDatabaseManager(dbManager);

        // Получаем настройки
        Preferences prefs = Preferences.userNodeForPackage(DesktopApplication.class);

        // Загружаем сохраненные параметры окна
        double x = prefs.getDouble("windowX", 100); // Значение по умолчанию - 100
        double y = prefs.getDouble("windowY", 100);
        double width = prefs.getDouble("windowWidth", 600);
        double height = prefs.getDouble("windowHeight", 400);

        // Устанавливаем размеры и положение окна
        stage.setX(x);
        stage.setY(y);
        stage.setWidth(width);
        stage.setHeight(height);

        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);

        stage.setTitle("MyBeautySalon");
        stage.setScene(scene);

        // Событие при закрытии окна: сохраняем параметры
        stage.setOnCloseRequest(event -> {
            event.consume(); // предотвращаем закрытие по умолчанию
            controller.handleMenuClose(null); // вызываем коллбэк закрытия из меню
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}