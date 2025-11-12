import controller.DataLoaderController;

public class App {

    public static void main(String[] args) {

        // Rutas de tus CSVs
        // String csvFile1 = "./resources/preciosEESS_es.csv";
        String csvFile2 = "./resources/embarcacionesPrecios_es.csv";

        // Controlador para cargar datos de los csv
        DataLoaderController controller = new DataLoaderController();

        // // controller.loadDataFromCsv(csvFile1);
        controller.loadDataFromCsv(csvFile2);

        System.out.println("Carga de datos finalizada");

    }
}