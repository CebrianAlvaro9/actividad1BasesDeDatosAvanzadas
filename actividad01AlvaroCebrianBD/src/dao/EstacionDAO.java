package dao;

import model.Estacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EstacionDAO {

    private final Connection conn;

    public EstacionDAO(Connection conn) {
        this.conn = conn;
    }
    // EN: dao/EstacionDAO.java

    public int insert(Estacion e) throws SQLException {

        String sql = """
                    INSERT INTO estacion_servicio
                    (id_empresa, tipo_estacion, provincia, municipio, localidad, codigo_postal, direccion, longitud, latitud, tipo_venta, rem, horario, margen)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)
                    ON DUPLICATE KEY UPDATE id_estacion=LAST_INSERT_ID(id_estacion)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getIdEmpresa());
            ps.setString(2, e.getTipoEstacion());
            ps.setString(3, e.getProvincia());
            ps.setString(4, e.getMunicipio());
            ps.setString(5, e.getLocalidad());
            ps.setString(6, e.getCodigoPostal());
            ps.setString(7, e.getDireccion());
            ps.setObject(8, e.getLongitud());
            ps.setObject(9, e.getLatitud());
            ps.setString(10, e.getTipoVenta());
            ps.setString(11, e.getRem());
            ps.setString(12, e.getHorario());
            ps.setString(13, e.getMargen());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        }
        throw new SQLException("No se pudo insertar la estación");
    }
}