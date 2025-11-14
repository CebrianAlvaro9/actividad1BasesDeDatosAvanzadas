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
            header = header.replace("\uFEFF", ""); // eliminar BOM esto impedia que se insertaran provincias
            if (header == null) {
                System.err.println("X CSV vacío");
                return;
            }

            // Mapeado
            String[] cols = header.split(";", -1);
            Map<String, Integer> index = new HashMap<>();
            for (int i = 0; i < cols.length; i++) {
                index.put(cols[i].trim().toLowerCase(), i);
            }

            // CSV es marítimo??
            boolean esMaritimo = index.containsKey("precio gasóleo de uso marítimo");
            System.out.println("CSV detectado: " + (esMaritimo ? "MARÍTIMO" : "TERRESTRE"));

            String line;
            while ((line = br.readLine()) != null) {
                total++;
                String[] f = line.split(";", -1);

                try {
                    // Campos base
                    String provincia = getField(f, index, "provincia");
                    String municipio = getField(f, index, "municipio");
                    String localidad = getField(f, index, "localidad");
                    String codigoPostal = getField(f, index, "código postal");
                    String direccion = getField(f, index, "dirección");
                    String margen = getField(f, index, "margen");
                    BigDecimal longitud = parseBigDecimal(getField(f, index, "longitud"));
                    BigDecimal latitud = parseBigDecimal(getField(f, index, "latitud"));

                    String rotulo = getField(f, index, "rótulo");
                    String tipoVenta = getField(f, index, "tipo venta");
                    String rem = getField(f, index, "rem.");
                    String horario = getField(f, index, "horario");

                    // Precios
                    BigDecimal precioGasolina95E5 = parseBigDecimal(getField(f, index, "precio gasolina 95 e5"));
                    BigDecimal precioGasolina95E10 = parseBigDecimal(getField(f, index, "precio gasolina 95 e10"));
                    BigDecimal precioGasolina95E5Premium = parseBigDecimal(
                            getField(f, index, "precio gasolina 95 e5 premium"));
                    BigDecimal precioGasolina98E5 = parseBigDecimal(getField(f, index, "precio gasolina 98 e5"));
                    BigDecimal precioGasolina98E10 = parseBigDecimal(getField(f, index, "precio gasolina 98 e10"));
                    BigDecimal precioGasoilA = parseBigDecimal(getField(f, index, "precio gasóleo a"));
                    BigDecimal precioGasoilPremium = parseBigDecimal(getField(f, index, "precio gasóleo premium"));
                    BigDecimal precioGasoilB = parseBigDecimal(getField(f, index, "precio gasóleo b"));
                    BigDecimal precioGasoilC = parseBigDecimal(getField(f, index, "precio gasóleo c"));
                    BigDecimal precioGasoilMaritimo = parseBigDecimal(
                            getField(f, index, "precio gasóleo de uso marítimo"));
                    BigDecimal precioBioetanol = parseBigDecimal(getField(f, index, "precio bioetanol"));
                    BigDecimal porcentajeBioalcohol = parseBigDecimal(getField(f, index, "% bioalcohol"));
                    BigDecimal precioBiodiesel = parseBigDecimal(getField(f, index, "precio biodiésel"));
                    BigDecimal porcentajeEsterMetilico = parseBigDecimal(getField(f, index, "% éster metílico"));
                    BigDecimal precioGLP = parseBigDecimal(getField(f, index, "precio gases licuados del petróleo"));
                    BigDecimal precioGNC = parseBigDecimal(getField(f, index, "precio gas natural comprimido"));
                    BigDecimal precioGNL = parseBigDecimal(getField(f, index, "precio gas natural licuado"));
                    BigDecimal precioHidrogeno = parseBigDecimal(getField(f, index, "precio hidrógeno"));
                    BigDecimal precioGasolina95E25 = parseBigDecimal(getField(f, index, "precio gasolina 95 e25"));
                    BigDecimal precioGasolina95E85 = parseBigDecimal(getField(f, index, "precio gasolina 95 e85"));
                    BigDecimal precioAdBlue = parseBigDecimal(getField(f, index, "precio adblue"));
                    BigDecimal precioDieselRenovable = parseBigDecimal(getField(f, index, "precio diesel renovable"));
                    BigDecimal precioGasolinaRenovable = parseBigDecimal(
                            getField(f, index, "precio gasolina renovable"));
                    BigDecimal precioMetanol = parseBigDecimal(getField(f, index, "precio metanol"));
                    BigDecimal precioAmoniaco = parseBigDecimal(getField(f, index, "precio amoniaco"));
                    BigDecimal precioBGNC = parseBigDecimal(getField(f, index, "precio bgnc"));
                    BigDecimal precioBGNL = parseBigDecimal(getField(f, index, "precio bgnl"));

                    // Empresa
                    Empresa empresa = new Empresa();
                    empresa.setRotulo(rotulo);
                    int empresaId = empresaDAO.insert(empresa);

                    // Estación
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
                    estacion.setMargen(esMaritimo ? null : margen);

                    int estacionId = estacionDAO.insert(estacion);
                    if (estacionId == -1 || latitud == null || longitud == null) {
                        System.out.println(" Línea " + total + " ignorada: estación duplicada o sin coordenadas!");
                        fail++;
                        continue;
                    }

                    // Insertar precios
                    // Insertar precios

                    // Marítimo
                    if (esMaritimo) {
                        if (precioGasoilMaritimo != null)
                            precioDAO.insert(new Precio(estacionId, "Gasóleo de uso marítimo", precioGasoilMaritimo));
                    }

                    // Terrestre
                    else {
                        if (precioGasolina95E5Premium != null)
                            precioDAO.insert(
                                    new Precio(estacionId, "Gasolina 95 E5 Premium", precioGasolina95E5Premium));
                        if (precioGasolina98E5 != null)
                            precioDAO.insert(new Precio(estacionId, "Gasolina 98 E5", precioGasolina98E5));
                        if (precioGasolina98E10 != null)
                            precioDAO.insert(new Precio(estacionId, "Gasolina 98 E10", precioGasolina98E10));
                        if (precioGasoilPremium != null)
                            precioDAO.insert(new Precio(estacionId, "Gasóleo Premium", precioGasoilPremium));
                        if (precioGasoilC != null)
                            precioDAO.insert(new Precio(estacionId, "Gasóleo C", precioGasoilC));
                        if (precioBioetanol != null)
                            precioDAO.insert(new Precio(estacionId, "Bioetanol", precioBioetanol));
                        if (porcentajeBioalcohol != null)
                            precioDAO.insert(new Precio(estacionId, "% Bioalcohol", porcentajeBioalcohol));
                        if (precioBiodiesel != null)
                            precioDAO.insert(new Precio(estacionId, "Biodiésel", precioBiodiesel));
                        if (porcentajeEsterMetilico != null)
                            precioDAO.insert(new Precio(estacionId, "% Éster metílico", porcentajeEsterMetilico));
                        if (precioGLP != null)
                            precioDAO.insert(new Precio(estacionId, "Gases licuados del petróleo", precioGLP));
                        if (precioGNC != null)
                            precioDAO.insert(new Precio(estacionId, "Gas natural comprimido", precioGNC));
                        if (precioGNL != null)
                            precioDAO.insert(new Precio(estacionId, "Gas natural licuado", precioGNL));
                        if (precioHidrogeno != null)
                            precioDAO.insert(new Precio(estacionId, "Hidrógeno", precioHidrogeno));
                        if (precioBGNL != null)
                            precioDAO.insert(new Precio(estacionId, "BGNL", precioBGNL));
                    }

                    // Comunes
                    if (precioGasolina95E5 != null)
                        precioDAO.insert(new Precio(estacionId, "Gasolina 95 E5", precioGasolina95E5));
                    if (precioGasolina95E10 != null)
                        precioDAO.insert(new Precio(estacionId, "Gasolina 95 E10", precioGasolina95E10));
                    if (precioGasoilA != null)
                        precioDAO.insert(new Precio(estacionId, "Gasóleo A", precioGasoilA));
                    if (precioGasoilB != null)
                        precioDAO.insert(new Precio(estacionId, "Gasóleo B", precioGasoilB));
                    if (precioGasolina95E25 != null)
                        precioDAO.insert(new Precio(estacionId, "Gasolina 95 E25", precioGasolina95E25));
                    if (precioGasolina95E85 != null)
                        precioDAO.insert(new Precio(estacionId, "Gasolina 95 E85", precioGasolina95E85));
                    if (precioAdBlue != null)
                        precioDAO.insert(new Precio(estacionId, "AdBlue", precioAdBlue));
                    if (precioDieselRenovable != null)
                        precioDAO.insert(new Precio(estacionId, "Diesel renovable", precioDieselRenovable));
                    if (precioGasolinaRenovable != null)
                        precioDAO.insert(new Precio(estacionId, "Gasolina renovable", precioGasolinaRenovable));
                    if (precioMetanol != null)
                        precioDAO.insert(new Precio(estacionId, "Metanol", precioMetanol));
                    if (precioAmoniaco != null)
                        precioDAO.insert(new Precio(estacionId, "Amoniaco", precioAmoniaco));
                    if (precioBGNC != null)
                        precioDAO.insert(new Precio(estacionId, "BGNC", precioBGNC));

                    ok++;

                } catch (Exception e) {
                    System.err.println("X Error en línea " + total + ": " + e.getMessage());
                    fail++;
                }
            }

            // Resumen
            System.out.println("\n **Resumen de carga:");
            System.out.println("OK Insertados: " + ok);
            System.out.println("X Fallidos: " + fail);
            System.out.println("Total líneas: " + total);

        } catch (Exception e) {
            System.err.println("X Error al leer CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helpers
    // Recoge el campo del array al q se va acceder
    private String getField(String[] fields, Map<String, Integer> map, String name) {
        Integer i = map.get(name.toLowerCase());
        return (i != null && i < fields.length) ? fields[i].trim() : "";
    }

    // Convierte numeros a formato EN para poder procesarlo
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