package config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DataBaseConnection {
    // Claves de Conexion a bd esto en algo rela iria en un .ENV
    private static final String URL = "jdbc:mysql://localhost:3306/gasolineras_miteco?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "root123";

    private static boolean initialized = false;

    // Conexion a bd
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

        if (!initialized) {
            try {
                executeDDL("./resources/schema.sql");
                initialized = true;
                System.out.println("OKEY Tablas creadas");
            } catch (Exception e) {
                throw new RuntimeException("Error al ejecutar DDL", e);
            }
        }

        return conn;
    }

    /**
     * Ejecuta un archivo SQL (DDL) para crear tablas o inicializar la base de
     * datos.
     */
    public static void executeDDL(String ddlFile) throws Exception {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                BufferedReader br = new BufferedReader(new FileReader(ddlFile))) {

            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--"))
                    continue;
                sql.append(line).append(" ");
                if (line.endsWith(";")) {
                    try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                        ps.execute();
                    }
                    sql.setLength(0);
                }
            }
        }
    }
}