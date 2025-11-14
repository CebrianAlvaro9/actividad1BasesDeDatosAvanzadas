package dao;

import model.Precio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PrecioDAO {

    private final Connection conn;

    public PrecioDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserta un precio. Si ya existe (por UNIQUE en BD), devuelve false y muestra
     * mensaje.
     * 
     * @param p Precio a insertar
     * @return true si se insertó correctamente, false si ya existía
     * @throws Exception
     */
    public boolean insert(Precio p) throws Exception {
        String sql = "INSERT INTO precio (id_estacion, tipo_combustible, precio) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getIdEstacion());
            ps.setString(2, p.getTipoCombustible());
            ps.setObject(3, p.getPrecio());
            ps.executeUpdate();
            return true; // insertado correctamente
        } catch (SQLException ex) {
            // Código de violación de UNIQUE en MySQL
            if ("23000".equals(ex.getSQLState())) {
                System.out.println("⚠️ El precio para esta estación y tipo de combustible ya estaba registrado: "
                        + p.getIdEstacion() + " - " + p.getTipoCombustible());
                return false; // ya existía
            }
            throw ex;
        }
    }
}