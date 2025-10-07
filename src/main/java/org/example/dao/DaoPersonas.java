package org.example.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.Modelo.Persona;
import org.example.bbdd.Conexion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;

public class DaoPersonas {

    private static final Logger logger = LoggerFactory.getLogger(DaoPersonas.class);

    public static ObservableList<Persona> cargarPersonas() {
        ObservableList<Persona> lista = FXCollections.observableArrayList();
        String sql = "SELECT id, nombre, apellido, fecha_nacimiento FROM personas";

        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                Date fecha = rs.getDate("fecha_nacimiento");
                LocalDate fechaNacimiento = fecha != null ? fecha.toLocalDate() : null;

                lista.add(new Persona(id, nombre, apellido, fechaNacimiento));
            }

        } catch (SQLException e) {
            logger.error("Error cargando personas", e);
        }

        return lista;
    }

    public static int insertarPersona(Persona persona) {
        String sql = "INSERT INTO personas (nombre, apellido, fecha_nacimiento) VALUES (?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, persona.getNombre());
            stmt.setString(2, persona.getApellido());
            stmt.setDate(3, Date.valueOf(persona.getFechaNacimiento()));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

        } catch (SQLException e) {
            logger.error("Error insertando persona", e);
        }
        return -1;
    }

    public static boolean eliminarPersona(int id) {
        String sql = "DELETE FROM personas WHERE id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error eliminando persona", e);
            return false;
        }
    }
}
