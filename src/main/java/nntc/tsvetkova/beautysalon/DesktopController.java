package nntc.tsvetkova.beautysalon;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.util.Optional;

public class DesktopController {

    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> nameColumn;

    @FXML
    public TextField fieldID;

    @FXML
    public TextField fieldName;

    private Stage primaryStage;
    private DatabaseManager primaryDatabaseManager;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void setPrimaryDatabaseManager(DatabaseManager dm) {
        this.primaryDatabaseManager = dm;
    }

    public void handleMenuClose(ActionEvent event){
        if (primaryStage != null && showCloseConfirmationDialog()){
            primaryDatabaseManager.disconnect();
            primaryStage.close();
        }
    }
    private boolean showCloseConfirmationDialog(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure?");
        Optional <ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private boolean showConfirmationDialog(String message){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(message);
        Optional <ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public void updateTable(){
        ObservableList<User> testData = FXCollections.observableArrayList();
        testData.add(new User (1,"Ivan"));
        testData.add(new User(2,"Masha"));

        tableView.setItems(testData);
        idColumn.setCellValueFactory(new PropertyValueFactory<User, Integer>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("name"));

        System.out.println("Вызван метод updateTable");
    }

    @FXML
    public void addRow(){
        //добавление данных в бд
        updateTable();
        System.out.println("Вызван метод addRow");
    }

    @FXML
    public void editRow(){
        //добавление данных в бд
        updateTable();
        System.out.println("Вызван метод editRow");
    }

    @FXML
    public void deleteRow(){
        if(showConfirmationDialog(String.format("Действительно удалить запись %s c ID=%s",
                fieldName.getText(),fieldID.getText()))
        ){
            //Удаление данных из бд
            updateTable();
            System.out.println("Вызван метод deleteRow");
        }
    }

    @FXML
    private void onRowClick(MouseEvent event){
        if (event.getClickCount() == 1){
            User selectedUser = tableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                fieldID.setText(String.format("%d", selectedUser.getId()));
                fieldName.setText(selectedUser.getName());
            }
        }
    }

//    public void showInfoWindow(ActionEvent actionEvent) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("info-view.fxml"));
//        VBox infoContent = fxmlLoader.load();
//        Dialog<Void> dialog = new Dialog<>();
//        dialog.setTitle("О программе");
//        dialog.getDialogPane().setContent(infoContent);
//
//        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
//
//        stage.setOnCloseRequest(event ->{
//            dialog.close();
//        });
//
//        dialog.showAndWait();
//    }


}