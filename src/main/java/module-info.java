module com.example.beautysalon {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens nntc.tsvetkova.beautysalon to javafx.fxml;
    exports nntc.tsvetkova.beautysalon;
}