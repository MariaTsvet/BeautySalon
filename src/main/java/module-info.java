module com.example.beautysalon {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.beautysalon to javafx.fxml;
    exports com.example.beautysalon;
}