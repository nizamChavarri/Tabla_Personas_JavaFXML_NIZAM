package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.File;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        // ✅ Crear carpeta logs si no existe
        try {
            File logDir = new File("logs");
            if (!logDir.exists()) {
                boolean created = logDir.mkdirs();
                if (created) {
                    System.out.println("Carpeta 'logs' creada correctamente.");
                } else {
                    System.out.println("No se pudo crear la carpeta 'logs'.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error creando carpeta logs: " + e.getMessage());
        }

        // Cargar interfaz JavaFX
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("FXML/Pers.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);
            stage.setScene(scene);
            stage.setMinWidth(700);
            stage.setMinHeight(400);
            stage.setTitle("Tabla de Personas");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo cargar la interfaz");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
