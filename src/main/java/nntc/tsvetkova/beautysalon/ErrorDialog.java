package nntc.tsvetkova.beautysalon;

import javafx.scene.control.Alert;
import javafx.stage.Modality;

public class ErrorDialog {

    public static void showError (String title, String message){
        // Создаем модальное окно
        Alert alert =  new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        //Делаем окно модальным
        alert.initModality(Modality.APPLICATION_MODAL);

        //Отображаем окно и ждем, пока пользователь закроет его
        alert.showAndWait();
    }
}
