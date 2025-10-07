module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;

    exports org.example;
    exports org.example.Modelo;
    exports org.example.Controlador;
    opens org.example.Modelo to javafx.fxml;
    opens org.example.Controlador to javafx.fxml;
}
