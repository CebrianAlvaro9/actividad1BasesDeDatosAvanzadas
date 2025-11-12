package controller;

import config.DataBaseConnection;
import dao.EmpresaDAO;
import dao.EstacionDAO;
import dao.PrecioDAO;
import model.Empresa;
import model.Estacion;
import model.Precio;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class DataLoaderController {

    private final EmpresaDAO empresaDAO;
    private final EstacionDAO estacionDAO;
    private final PrecioDAO precioDAO;

    public DataLoaderController() {
        try {
            Connection conn = DataBaseConnection.getConnection();
            this.empresaDAO = new EmpresaDAO(conn);
            this.estacionDAO = new EstacionDAO(conn);
            this.precioDAO = new PrecioDAO(conn);
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con la base de datos", e);
        }
    }

    public void loadDataFromCsv(String csvFile) {
        int total = 0, ok = 0, fail = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String header = br.readLine();
            if (header == null) {
                System.err.println("❌ CSV vacío");
                return;
            }

            // === Crear mapa de nombres de columnas ===
            String[] cols = header.split(";", -1);
            Map<String, Integer> index = new HashMap<>();
            for (int i = 0; i < cols.length; i++) {
                index.put(cols[i].trim().toLowerCase(), i);
            }

            // === Detectar si el CSV es marítimo ===
            boolean esMaritimo = index.containsKey("precio gasóleo de uso marítimo");
            System.out.println("📂 Tipo de CSV detectado: " + (esMaritimo ? "MARÍTIMO" : "TERRESTRE"));

            String line;
            while ((line = br.readLine()) != null) {
                total++;
                String[] f = line.split(";", -1);

                try {
                    // ===== Campos base =====
                    String provincia = getField(f, index, "provincia");
                    String municipio = getField(f, index, "municipio");
                    String localidad = getField(f, index, "localidad");
                    String codigoPostal = getField(f, index, "código postal");
                    String direccion = getField(f, index, "dirección");
                    BigDecimal longitud = parseBigDecimal(getField(f, index, "longitud"));
                    BigDecimal latitud = parseBigDecimal(getField(f, index, "latitud"));

                    String rotulo = getField(f, index, "rótulo");
                    String tipoVenta = getField(f, index, "tipo venta");
                    String rem = getField(f, index, "rem.");
                    String horario = getField(f, index, "horario");

                    // ===== Precios =====
                    BigDecimal precioGas95 = parseBigDecimal(getField(f, index, "precio gasolina 95 e5"));
                    BigDecimal precioGas98 = parseBigDecimal(getField(f, index, "precio gasolina 98 e5"));
                    BigDecimal precioDieselA = parseBigDecimal(getField(f, index, "precio gasóleo a"));
                    BigDecimal precioDieselPremium = parseBigDecimal(getField(f, index, "precio gasóleo premium"));
                    BigDecimal precioMaritimo = parseBigDecimal(getField(f, index, "precio gasóleo de uso marítimo"));

                    // ===== Empresa =====
                    Empresa empresa = new Empresa();
                    empresa.setRotulo(rotulo);
                    int empresaId = empresaDAO.insert(empresa);

                    // ===== Estación =====
                    Estacion estacion = new Estacion();
                    estacion.setIdEmpresa(empresaId);
                    estacion.setProvincia(provincia);
                    estacion.setMunicipio(municipio);
                    estacion.setLocalidad(localidad);
                    estacion.setCodigoPostal(codigoPostal);
                    estacion.setDireccion(direccion);
                    estacion.setLongitud(longitud);
                    estacion.setLatitud(latitud);
                    estacion.setTipoVenta(tipoVenta);
                    estacion.setRem(rem);
                    estacion.setHorario(horario);
                    estacion.setTipoEstacion(esMaritimo ? "MARITIMA" : "TERRESTRE");

                    int estacionId = estacionDAO.insert(estacion);
                    if (estacionId == -1 || latitud == null || longitud == null) {
                        System.out.println("⚠️ Línea " + total + " ignorada: estación duplicada o sin coordenadas");
                        fail++;
                        continue;
                    }

                    // ===== Insertar precios =====
                    if (precioGas95 != null)
                        precioDAO.insert(new Precio(estacionId, "Gasolina 95 E5", precioGas95));
                    if (precioGas98 != null)
                        precioDAO.insert(new Precio(estacionId, "Gasolina 98 E5", precioGas98));
                    if (precioDieselA != null)
                        precioDAO.insert(new Precio(estacionId, "Gasóleo A", precioDieselA));
                    if (precioDieselPremium != null)
                        precioDAO.insert(new Precio(estacionId, "Gasóleo Premium", precioDieselPremium));
                    if (esMaritimo && precioMaritimo != null)
                        precioDAO.insert(new Precio(estacionId, "Gasóleo Marítimo", precioMaritimo));

                    ok++;

                } catch (Exception e) {
                    System.err.println("❌ Error en línea " + total + ": " + e.getMessage());
                    fail++;
                }
            }

            // === Resumen ===
            System.out.println("\n📊 Resumen de carga:");
            System.out.println("✅ Insertados: " + ok);
            System.out.println("❌ Fallidos: " + fail);
            System.out.println("📈 Total líneas: " + total);

        } catch (Exception e) {
            System.err.println("❌ Error al leer CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================= Helpers =================

    private String getField(String[] fields, Map<String, Integer> map, String name) {
        Integer i = map.get(name.toLowerCase());
        return (i != null && i < fields.length) ? fields[i].trim() : "";
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isEmpty())
            return null;
        try {
            return new BigDecimal(value.replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}