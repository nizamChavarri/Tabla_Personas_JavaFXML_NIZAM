module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;



    exports org.example;
    exports org.example.Modelo;
    exports org.example.Controlador;
    opens org.example.Modelo to javafx.fxml, javafx.base;
    opens org.example.Controlador to javafx.fxml, javafx.base;
}
