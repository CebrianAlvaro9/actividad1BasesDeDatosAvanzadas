package dao;

import model.Empresa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmpresaDAO {

    private final Connection conn;

    public EmpresaDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inserta la empresa si no existe (según UNIQUE en BD)
     * Devuelve el id generado o -1 si ya existía
     */
    public int insert(Empresa e) throws SQLException {
        String insert = "INSERT INTO empresa (rotulo) VALUES (?) ON DUPLICATE KEY UPDATE id_empresa=LAST_INSERT_ID(id_empresa)";
        try (PreparedStatement ps = conn.prepareStatement(insert, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getRotulo());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    return rs.getInt(1); // devuelve ID nuevo o existente
            }
        }
        throw new SQLException("No se pudo insertar la empresa");
    }
}