package nntc.tsvetkova.beautysalon;

import javafx.application.Platform;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DesktopApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader =
                new FXMLLoader(DesktopApplication.class
                        .getResource("main-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        DatabaseManager dbManager = new DatabaseManager();
        dbManager.connect();

        try {
            // Проверка таблицы
            dbManager.ensureTableExists();
        } catch (Exception e) {
            System.out.println("Ошибка при проверке/создании таблицы: " + e.getMessage());
            // Показываем модальное окно с ошибкой
            Platform.runLater(() -> ErrorDialog.showError("Ошибка при проверке/создании таблицы: ", e.getMessage()));
            dbManager.disconnect();
        }

        DesktopController controller = fxmlLoader.getController();
        controller.setPrimaryStage(stage);

        controller.setPrimaryDatabaseManager(dbManager);

        stage.setOnCloseRequest(event->{
            event.consume();
            controller.handleMenuClose(null);
        });

        stage.setTitle("MyBeautySalon");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}