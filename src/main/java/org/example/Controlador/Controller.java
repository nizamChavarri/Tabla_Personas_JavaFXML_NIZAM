package org.example.Controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.example.Modelo.Persona;

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

    private int nextId = 6;

    @FXML
    private void initialize() {
        personas = FXCollections.observableArrayList(
                new Persona(1, "Ashwin", "Sharan", LocalDate.parse("2012-10-11")),
                new Persona(2, "Advik", "Sharan", LocalDate.parse("2012-10-11")),
                new Persona(3, "Layne", "Estes", LocalDate.parse("2011-12-16")),
                new Persona(4, "Mason", "Boyd", LocalDate.parse("2003-04-20")),
                new Persona(5, "Babalu", "Sharan", LocalDate.parse("1980-01-10"))
        );

        backupPersonas = FXCollections.observableArrayList(personas);

        colId.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getId()).asObject());
        colNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombre()));
        colApellido.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getApellido()));
        colFechaNacimiento.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getFechaNacimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        ));

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setItems(personas);

        btnAdd.setOnAction(e -> agregarPersona());
        btnDelete.setOnAction(e -> eliminarSeleccionados());
        btnRestore.setOnAction(e -> restaurarFilas());
    }

    private void agregarPersona() {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        LocalDate fecha = datePickerFechaNacimiento.getValue();

        if(nombre.isEmpty() || apellido.isEmpty() || fecha == null) {
            mostrarAlerta("Error", "Debe llenar todos los campos");
            return;
        }

        Persona nueva = new Persona(nextId++, nombre, apellido, fecha);
        personas.add(nueva);
        backupPersonas.add(nueva);
        limpiarCampos();
    }

    private void eliminarSeleccionados() {
        ObservableList<Persona> seleccionados = tableView.getSelectionModel().getSelectedItems();
        if(seleccionados.isEmpty()) {
            mostrarAlerta("Atención", "No hay filas seleccionadas para eliminar");
            return;
        }
        personas.removeAll(seleccionados);
    }

    private void restaurarFilas() {
        if(backupPersonas != null) {
            personas.setAll(backupPersonas);
            nextId = backupPersonas.stream().mapToInt(Persona::getId).max().orElse(0) + 1;
        }
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtApellido.clear();
        datePickerFechaNacimiento.setValue(null);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
