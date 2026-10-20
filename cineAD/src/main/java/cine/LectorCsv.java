package cine;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVParser;
import com.opencsv.exceptions.CsvValidationException;

public class LectorCsv {

    private static final int COLUMNAS_MINIMAS = 17;

    private int descartadas = 0;

    public int getDescartadas() {
        return descartadas;
    }

    // Lectura línea a línea con mapeo manual. Las filas que fallan se apartan en
    // un fichero con una columna MOTIVO y la lectura continúa.
    public Catalogo leer(String rutaCsv, String rutaDescartadas) throws IOException, CsvValidationException {

        Catalogo catalogo = new Catalogo();
        descartadas = 0;

        // Separador '|' y SIN comillas ni escape: las descripciones llevan comillas
        // y barras que no son de formato
        CSVParser parser = new CSVParserBuilder()
                .withSeparator('|')
                .withQuoteChar(ICSVParser.NULL_CHARACTER)
                .withEscapeChar(ICSVParser.NULL_CHARACTER)
                .build();

        try (CSVReader lector = new CSVReaderBuilder(new FileReader(rutaCsv, StandardCharsets.UTF_8))
                .withCSVParser(parser).build();
                CSVWriter escritor = new CSVWriter(new FileWriter(rutaDescartadas, StandardCharsets.UTF_8), '|',
                        CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER, "\n")) {

            // La cabecera se copia igual y se le añade la columna MOTIVO
            String[] cabecera = lector.readNext();
            escritor.writeNext(conMotivo(cabecera, "MOTIVO"));

            String[] fila;
            while ((fila = lector.readNext()) != null) {
                long numeroLinea = lector.getLinesRead();
                try {
                    catalogo.getPeliculas().add(filaAPelicula(fila));
                } catch (Exception e) {
                    descartadas++;
                    String motivo = "Línea " + numeroLinea + ": " + e;
                    // el motivo no puede llevar | ni saltos de línea
                    motivo = motivo.replace('|', '/').replace('\n', ' ');
                    escritor.writeNext(conMotivo(fila, motivo));
                }
            }
        }
        return catalogo;
    }

    // Convierte una fila en una Pelicula. Si algo no cuadra, lanza una excepción.
    private Pelicula filaAPelicula(String[] f) {
        if (f.length < COLUMNAS_MINIMAS) {
            throw new IllegalArgumentException("Columnas insuficientes: " + f.length);
        }
        if (!f[0].trim().startsWith("tt")) {
            throw new IllegalArgumentException("Id de película no válido: " + f[0]);
        }

        Pelicula p = new Pelicula();
        p.setIdImdb(f[0].trim());
        p.setTitulo(f[1].trim());
        p.setTituloOriginal(f[2].trim());
        p.setAnio(Integer.parseInt(f[3].trim()));
        p.setFechaPublicacion(aFecha(f[4].trim()));
        p.setGeneros(aLista(f[5]));
        p.setDuracion(Integer.parseInt(f[6].trim()));
        p.setPaises(aLista(f[7]));
        p.setIdiomas(aLista(f[8]));
        p.setDirectores(aPersonas(f[9]));
        p.setActores(aPersonas(f[10]));
        p.setDescripcion(f[11].trim());
        p.setValoracionMedia(Double.parseDouble(f[12].trim())); // aquí falla en las filas desplazadas
        p.setVotos(Integer.parseInt(f[13].trim()));
        p.setPresupuesto(aPresupuesto(f[14].trim()));
        p.setResenasUsuarios(aDecimalOCero(f[15]));
        p.setResenasCriticos(aDecimalOCero(f[16]));
        return p;
    }

    // "2004" -> "2004-01-01" ; "1999-06-11" -> igual. Si la fecha no es válida, lanza excepción.
    private String aFecha(String texto) {
        if (texto.length() == 4) {
            return LocalDate.of(Integer.parseInt(texto), 1, 1).toString();
        }
        return LocalDate.parse(texto).toString();
    }

    // "Action, Sci-Fi" -> ["Action", "Sci-Fi"]
    private List<String> aLista(String texto) {
        List<String> lista = new ArrayList<>();
        for (String parte : texto.split(",")) {
            String limpio = parte.trim();
            if (!limpio.isEmpty()) {
                lista.add(limpio);
            }
        }
        return lista;
    }

    private List<Persona> aPersonas(String texto) {
        List<Persona> lista = new ArrayList<>();
        for (String nombre : aLista(texto)) {
            lista.add(Persona.desdeTexto(nombre));
        }
        return lista;
    }

    // "$1226000" -> 1226000.0 ; vacío -> null
    private Double aPresupuesto(String texto) {
        if (texto.isEmpty()) {
            return null;
        }
        if (!texto.startsWith("$")) {
            throw new IllegalArgumentException("Presupuesto sin símbolo $: " + texto);
        }
        return Double.parseDouble(texto.substring(1));
    }

    // Las reseñas vienen vacías en muchas filas: no es un error, se toman como 0
    private double aDecimalOCero(String texto) {
        String t = texto.trim();
        return t.isEmpty() ? 0.0 : Double.parseDouble(t);
    }

    // Copia de la fila con un elemento más al final
    private String[] conMotivo(String[] fila, String motivo) {
        String[] nueva = new String[fila.length + 1];
        System.arraycopy(fila, 0, nueva, 0, fila.length);
        nueva[fila.length] = motivo;
        return nueva;
    }
}