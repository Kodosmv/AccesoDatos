package cine;

import java.io.File;
import java.util.Scanner;

public class Main {

    private static final String CSV = "resources/IMDb_movies_1999-2020.csv";
    private static final String DESCARTADAS = "resources/descartadas.csv";
    private static final String XML = "resources/catalogo.xml";
    private static final String JSON = "resources/catalogo.json";
    private static final String DAT = "resources/catalogo.dat";

    public static void main(String[] args) {
        Catalogo catalogo = leerCsv();
        if (catalogo == null) {
            System.out.println("No se ha podido leer el CSV. Fin del programa.");
            return;
        }

        exportarXml(catalogo);
        exportarJson(catalogo);
        exportarBinario(catalogo);
        buscar(catalogo);
    }

    private static Catalogo leerCsv() {
        System.out.println("Leyendo CSV: " + CSV + " ...");
        long inicio = System.nanoTime();
        LectorCsv lector = new LectorCsv();
        try {
            Catalogo catalogo = lector.leer(CSV, DESCARTADAS);
            System.out.println("Fin lectura CSV (" + duracion(inicio) + ")");
            System.out.println("  Películas cargadas: " + catalogo.getPeliculas().size());
            System.out.println("  Filas descartadas: " + lector.getDescartadas() + " (ver " + DESCARTADAS + ")");
            return catalogo;
        } catch (Exception e) {
            System.out.println("Error leyendo el CSV: " + e.getMessage());
            return null;
        }
    }

    // Si una exportación falla, se informa del error y se continúa con la siguiente
    private static void exportarXml(Catalogo catalogo) {
        System.out.println("Exportando a XML: " + XML + " ...");
        long inicio = System.nanoTime();
        try {
            Exportador.exportarXml(catalogo, XML);
            System.out.println("Fin exportación XML (" + duracion(inicio) + ", " + tamanio(XML) + ")");
        } catch (Exception e) {
            System.out.println("Error en la exportación XML: " + e.getMessage());
        }
    }

    private static void exportarJson(Catalogo catalogo) {
        System.out.println("Exportando a JSON: " + JSON + " ...");
        long inicio = System.nanoTime();
        try {
            Exportador.exportarJson(catalogo, JSON);
            System.out.println("Fin exportación JSON (" + duracion(inicio) + ", " + tamanio(JSON) + ")");
        } catch (Exception e) {
            System.out.println("Error en la exportación JSON: " + e.getMessage());
        }
    }

    private static void exportarBinario(Catalogo catalogo) {
        System.out.println("Exportando a binario: " + DAT + " ...");
        long inicio = System.nanoTime();
        try {
            Exportador.exportarBinario(catalogo, DAT);
            System.out.println("Fin exportación binario (" + duracion(inicio) + ", " + tamanio(DAT) + ")");
        } catch (Exception e) {
            System.out.println("Error en la exportación binaria: " + e.getMessage());
        }
    }

    private static void buscar(Catalogo catalogo) {
        Scanner teclado = new Scanner(System.in);
        Criterios criterios = new PeticionCriterios(teclado).pedir();

        Buscador.buscarConStreams(catalogo, criterios);
        try {
            Buscador.buscarConXPath(XML, criterios);
        } catch (Exception e) {
            System.out.println("Error en la búsqueda con XPath: " + e.getMessage());
        }
    }

    private static String duracion(long inicioNanos) {
        double segundos = (System.nanoTime() - inicioNanos) / 1_000_000_000.0;
        return String.format("%.2f s", segundos);
    }

    private static String tamanio(String ruta) {
        double mb = new File(ruta).length() / (1024.0 * 1024.0);
        return String.format("%.2f MB", mb);
    }
}