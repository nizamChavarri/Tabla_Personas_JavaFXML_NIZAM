package org.example.Controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.example.Modelo.Persona;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.stage.Stage;

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

    // Logger SLF4J
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    // Configuración conexión (cambia tus datos)
    private final String URL = "jdbc:mariadb://localhost:3306/Personas_mariadb";
    private final String USER = "root";
    private final String PASSWORD = "admin";

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

        // Carga datos desde la DB
        personas = cargarDatosDesdeDB();

        // Backup con la lista original para restaurar
        backupPersonas = FXCollections.observableArrayList(personas);

        tableView.setItems(personas);

        // Eventos botones
        btnAdd.setOnAction(e -> agregarPersona());
        btnDelete.setOnAction(e -> eliminarSeleccionados());
        btnRestore.setOnAction(e -> restaurarFilas());
    }

    private ObservableList<Persona> cargarDatosDesdeDB() {
        ObservableList<Persona> lista = FXCollections.observableArrayList();
        String sql = "SELECT id, nombre, apellido, fecha_nacimiento FROM personas";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                Date fecha = rs.getDate("fecha_nacimiento");
                LocalDate fechaNacimiento = fecha != null ? fecha.toLocalDate() : null;

                Persona p = new Persona(id, nombre, apellido, fechaNacimiento);
                lista.add(p);
            }

            logger.info("Datos cargados correctamente desde la base de datos. Total registros: {}", lista.size());

        } catch (SQLException e) {
            logger.error("Error cargando datos desde la base de datos", e);
            mostrarAlerta("Error", "No se pudo cargar datos desde la base de datos:\n" + e.getMessage());
        }

        // Ajusta nextId para nuevos registros
        nextId = lista.stream().mapToInt(Persona::getId).max().orElse(0) + 1;

        return lista;
    }

    private void agregarPersona() {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        LocalDate fecha = datePickerFechaNacimiento.getValue();

        if (nombre.isEmpty() || apellido.isEmpty() || fecha == null) {
            mostrarAlerta("Error", "Debe llenar todos los campos");
            logger.warn("Intento de agregar persona con campos incompletos");
            return;
        }

        String sqlInsert = "INSERT INTO personas (nombre, apellido, fecha_nacimiento) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, apellido);
            pstmt.setDate(3, Date.valueOf(fecha));
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                mostrarAlerta("Error", "No se pudo insertar la persona en la base de datos");
                logger.error("No se insertó la persona: {} {}", nombre, apellido);
                return;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    Persona nueva = new Persona(id, nombre, apellido, fecha);
                    personas.add(nueva);
                    backupPersonas.add(nueva);  // También agregar al backup para mantenerlo actualizado
                    limpiarCampos();
                    logger.info("Persona agregada: {} {} (ID: {})", nombre, apellido, id);
                } else {
                    mostrarAlerta("Error", "No se pudo obtener el ID generado");
                    logger.error("No se pudo obtener el ID generado para la persona: {} {}", nombre, apellido);
                }
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al insertar en la base de datos:\n" + e.getMessage());
            logger.error("Error al insertar persona en la base de datos", e);
        }
    }

    private void eliminarSeleccionados() {
        ObservableList<Persona> seleccionados = tableView.getSelectionModel().getSelectedItems();
        if (seleccionados.isEmpty()) {
            mostrarAlerta("Atención", "No hay filas seleccionadas para eliminar");
            logger.warn("Intento de eliminar sin seleccionar filas");
            return;
        }

        String sqlDelete = "DELETE FROM personas WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {

            for (Persona p : seleccionados) {
                pstmt.setInt(1, p.getId());
                pstmt.executeUpdate();

                // Solo eliminar de la lista visible, no del backup
                personas.remove(p);
                logger.info("Persona eliminada: {} {} (ID: {})", p.getNombre(), p.getApellido(), p.getId());
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al eliminar en la base de datos:\n" + e.getMessage());
            logger.error("Error al eliminar personas en la base de datos", e);
        }
    }

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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
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
