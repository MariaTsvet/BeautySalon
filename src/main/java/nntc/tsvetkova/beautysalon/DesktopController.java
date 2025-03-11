package nntc.tsvetkova.beautysalon;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.Optional;
import java.util.prefs.Preferences;

public class DesktopController {

    private Stage primaryStage;
    private DatabaseManager primaryDatabaseManager;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void setPrimaryDatabaseManager(DatabaseManager dm) {
        this.primaryDatabaseManager = dm;
    }

    public void handleMenuClose(ActionEvent event){
        if (primaryStage != null) {
            boolean shouldClose = showCloseConfirmationDialog();
            if (shouldClose) {
                System.out.println("Пользователь подтвердил закрытие. Окно будет закрыто.");
                // Получаем настройки
                Preferences prefs = Preferences.userNodeForPackage(DesktopApplication.class);
                prefs.putDouble("windowX", primaryStage.getX());
                prefs.putDouble("windowY", primaryStage.getY());
                prefs.putDouble("windowWidth", primaryStage.getWidth());
                prefs.putDouble("windowHeight", primaryStage.getHeight());

                if (primaryDatabaseManager != null) {
                    this.primaryDatabaseManager.disconnect();
                }

                primaryStage.close(); // Закрытие окна
            } else {
                System.out.println("Пользователь отменил закрытие.");
            }
        } else {
            System.err.println("Stage не был установлен!");
        }
    }
    private boolean showCloseConfirmationDialog(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение закрытия");
        alert.setHeaderText("Вы уверены, что хотите выйти?");
        alert.setContentText("Все несохраненные данные будут потеряны.");
        Optional <ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public void showLoginWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        VBox infoContent = fxmlLoader.load();

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Настройки доступа к СУБД");
        dialog.getDialogPane().setContent(infoContent);
        Stage stageLogin = (Stage) dialog.getDialogPane().getScene().getWindow();

        stageLogin.setOnCloseRequest(event -> {
            System.out.println("Закрытие окна настроек доступа к СУБД...");
            dialog.close();
        });

        dialog.showAndWait();
    }

//    public void showServicesWindow(ActionEvent actionEvent) throws IOException {
//        // Загружаем FXML файл для окна справки
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("services-view.fxml"));
//
//        VBox infoContent = fxmlLoader.load(); // Загружаем содержимое окна справки
//
//        ServicesController controller = fxmlLoader.getController();
//        controller.setPrimaryDatabaseManager(primaryDatabaseManager);
//
//        // Создаем диалоговое окно
//        Dialog<Void> dialog = new Dialog<>();
//        dialog.setTitle("Управление услугами");
//
//        dialog.getDialogPane().setContent(infoContent); // Добавляем содержимое в диалоговое окно
//
//        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
//
//
//
//        // Обработчик закрытия окна
//        stage.setOnCloseRequest(event -> {
//            System.out.println("Закрытие окна с услугами...");
//            dialog.close(); // Закрыть диалог
//        });
//
//        // Показываем диалог в модальном режиме
//        dialog.showAndWait();
//    }


//    public void showInfoWindow() throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("info-view.fxml"));
//        VBox infoContent = fxmlLoader.load();
//        Dialog<Void> dialog = new Dialog<>();
//       dialog.setTitle("О программе");
//        dialog.getDialogPane().setContent(infoContent);
//
//        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
//
//        stage.setOnCloseRequest(event ->{
//           dialog.close();
//        });
//
//        dialog.showAndWait();
//    }
//
//    private void cleanupFields() {
//        fieldID.clear();
//        fieldName.clear();
//    }


}