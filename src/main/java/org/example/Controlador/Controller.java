package org.example.Controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.example.Modelo.Persona;
import org.example.dao.DaoPersonas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Controller {

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private DatePicker datePickerFechaNacimiento;

    @FXML private Button btnAdd;
    @FXML private Button btnDelete;
    @FXML private Button btnRestore;

    @FXML private TableView<Persona> tableView;
    @FXML private TableColumn<Persona, Integer> colId;
    @FXML private TableColumn<Persona, String> colNombre;
    @FXML private TableColumn<Persona, String> colApellido;
    @FXML private TableColumn<Persona, String> colFechaNacimiento;

    private ObservableList<Persona> personas;
    private ObservableList<Persona> backupPersonas;

    private int nextId = 1;

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    @FXML
    private void initialize() {
        // Configuración columnas TableView
        colId.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getId()).asObject());
        colNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombre()));
        colApellido.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getApellido()));
        colFechaNacimiento.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getFechaNacimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        ));

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Carga de datos asincrónica
        cargarDatosAsincrono();

        // Eventos botones
        btnAdd.setOnAction(e -> agregarPersonaAsincrono());
        btnDelete.setOnAction(e -> eliminarSeleccionadosAsincrono());
        btnRestore.setOnAction(e -> restaurarFilas());
    }

    // =================== CARGA ASINCRONA ===================
    private void cargarDatosAsincrono() {
        Task<ObservableList<Persona>> task = new Task<>() {
            @Override
            protected ObservableList<Persona> call() throws Exception {
                return DaoPersonas.cargarPersonas();
            }
        };

        task.setOnSucceeded(e -> {
            personas = task.getValue();
            backupPersonas = FXCollections.observableArrayList(personas);
            tableView.setItems(personas);
            nextId = personas.stream().mapToInt(Persona::getId).max().orElse(0) + 1;
            logger.info("Datos cargados correctamente. Total registros: {}", personas.size());
        });

        task.setOnFailed(e -> {
            mostrarAlerta("Error", "No se pudo cargar datos:\n" + task.getException().getMessage());
            logger.error("Error cargando datos", task.getException());
        });

        new Thread(task).start();
    }

    // =================== AGREGAR ASINCRONO ===================
    private void agregarPersonaAsincrono() {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        LocalDate fecha = datePickerFechaNacimiento.getValue();

        if (nombre.isEmpty() || apellido.isEmpty() || fecha == null) {
            mostrarAlerta("Error", "Debe llenar todos los campos");
            logger.warn("Intento de agregar persona con campos incompletos");
            return;
        }

        Persona nueva = new Persona(0, nombre, apellido, fecha);

        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                return DaoPersonas.insertarPersona(nueva);
            }
        };

        task.setOnSucceeded(e -> {
            int idGenerado = task.getValue();
            if (idGenerado > 0) {
                Persona p = new Persona(idGenerado, nombre, apellido, fecha);
                personas.add(p);
                backupPersonas.add(p);
                limpiarCampos();
                logger.info("Persona agregada: {} {} (ID: {})", nombre, apellido, idGenerado);
            } else {
                mostrarAlerta("Error", "No se pudo agregar la persona");
                logger.error("No se insertó la persona: {} {}", nombre, apellido);
            }
        });

        task.setOnFailed(e -> {
            mostrarAlerta("Error", "Error al insertar en la DB:\n" + task.getException().getMessage());
            logger.error("Error insertando persona", task.getException());
        });

        new Thread(task).start();
    }

    // =================== ELIMINAR ASINCRONO ===================
    private void eliminarSeleccionadosAsincrono() {
        ObservableList<Persona> seleccionados = tableView.getSelectionModel().getSelectedItems();
        if (seleccionados.isEmpty()) {
            mostrarAlerta("Atención", "No hay filas seleccionadas para eliminar");
            logger.warn("Intento de eliminar sin seleccionar filas");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (Persona p : seleccionados) {
                    DaoPersonas.eliminarPersona(p.getId());
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            personas.removeAll(seleccionados);
            logger.info("Registros eliminados: {}", seleccionados.size());
        });

        task.setOnFailed(e -> {
            mostrarAlerta("Error", "Error al eliminar registros:\n" + task.getException().getMessage());
            logger.error("Error eliminando registros", task.getException());
        });

        new Thread(task).start();
    }

    // =================== RESTAURAR ===================
    private void restaurarFilas() {
        if (backupPersonas != null) {
            personas.setAll(backupPersonas);
            nextId = backupPersonas.stream().mapToInt(Persona::getId).max().orElse(0) + 1;
            logger.info("Filas restauradas desde backup. Total: {}", backupPersonas.size());
        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtApellido.clear();
        datePickerFechaNacimiento.setValue(null);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) tableView.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void mostrarAcercaDe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acerca de");
        alert.setHeaderText("Información del programa");
        alert.setContentText("Este programa fue creado por Nizam\nVersión 1.0");
        alert.showAndWait();
    }
}
