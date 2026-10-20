package cine;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Buscador {

    // XPath 1.0 no tiene lower-case(): se simula con translate()
    private static final String MAYUS = "ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞ";
    private static final String MINUS = "abcdefghijklmnopqrstuvwxyzàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþ";

    // ---------------------------------------------------------------
    // BÚSQUEDA CON STREAMS sobre el catálogo en memoria
    // ---------------------------------------------------------------
    public static void buscarConStreams(Catalogo catalogo, Criterios c) {
        System.out.println("Buscador con Streams...");
        long inicio = System.nanoTime();

        List<String> resultado = catalogo.getPeliculas().stream()
                .filter(p -> c.getGenero().isEmpty() || contiene(p.getGeneros(), c.getGenero()))
                .filter(p -> c.getTitulo().isEmpty()
                        || p.getTitulo().toLowerCase().contains(c.getTitulo().toLowerCase()))
                .filter(p -> c.getPais().isEmpty() || contiene(p.getPaises(), c.getPais()))
                // sin presupuesto en el CSV, la película no cumple "presupuesto <= valor"
                .filter(p -> c.getPresupuestoMax() == null
                        || (p.getPresupuesto() != null && p.getPresupuesto() <= c.getPresupuestoMax()))
                .filter(p -> c.getValoracionMin() == null || p.getValoracionMedia() >= c.getValoracionMin())
                .map(p -> p.getIdImdb() + " - " + p.getTitulo() + " (valoración: " + p.getValoracionMedia() + ")")
                .collect(Collectors.toList());

        long ms = (System.nanoTime() - inicio) / 1_000_000;
        mostrar(resultado, ms);
    }

    // ¿La lista tiene ese texto (coincidencia exacta, sin distinguir mayúsculas)?
    private static boolean contiene(List<String> lista, String texto) {
        String buscado = texto.toLowerCase();
        for (String elemento : lista) {
            if (elemento.toLowerCase().equals(buscado)) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------
    // BÚSQUEDA CON XPATH sobre catalogo.xml (una única consulta)
    // ---------------------------------------------------------------
    public static void buscarConXPath(String rutaXml, Criterios c)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        System.out.println("Buscador con XPath...");
        long inicio = System.nanoTime();

        // 1. Cargamos el XML en un árbol DOM (entra en el tiempo, porque forma parte de la búsqueda)
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new File(rutaXml));

        // 2. Construimos la consulta con los criterios rellenados
        String consulta = construirConsulta(c);

        // 3. Ejecutamos
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList nodos = (NodeList) xpath.evaluate(consulta, doc, XPathConstants.NODESET);

        List<String> resultado = new ArrayList<>();
        for (int i = 0; i < nodos.getLength(); i++) {
            String id = xpath.evaluate("idImdb", nodos.item(i));
            String titulo = xpath.evaluate("titulo", nodos.item(i));
            String valoracion = xpath.evaluate("valoracionMedia", nodos.item(i));
            resultado.add(id + " - " + titulo + " (valoración: " + valoracion + ")");
        }

        long ms = (System.nanoTime() - inicio) / 1_000_000;
        mostrar(resultado, ms);
    }

    // Ejemplo de resultado:
    // /catalogo/pelicula[generos/genero[translate(.,...) = 'action'] and contains(translate(titulo,...), 'matrix')]
    private static String construirConsulta(Criterios c) {
        List<String> condiciones = new ArrayList<>();

        if (!c.getGenero().isEmpty()) {
            condiciones.add("generos/genero[" + minusculas(".") + " = " + literal(c.getGenero().toLowerCase()) + "]");
        }
        if (!c.getTitulo().isEmpty()) {
            condiciones.add("contains(" + minusculas("titulo") + ", " + literal(c.getTitulo().toLowerCase()) + ")");
        }
        if (!c.getPais().isEmpty()) {
            condiciones.add("paises/pais[" + minusculas(".") + " = " + literal(c.getPais().toLowerCase()) + "]");
        }
        // Si la película no tiene <presupuesto>, la comparación da falso: igual que en Streams
        if (c.getPresupuestoMax() != null) {
            condiciones.add("presupuesto <= " + numero(c.getPresupuestoMax()));
        }
        if (c.getValoracionMin() != null) {
            condiciones.add("valoracionMedia >= " + numero(c.getValoracionMin()));
        }

        String consulta = "/catalogo/pelicula";
        if (!condiciones.isEmpty()) {
            consulta += "[" + String.join(" and ", condiciones) + "]";
        }
        return consulta;
    }

    private static String minusculas(String expresion) {
        return "translate(" + expresion + ", '" + MAYUS + "', '" + MINUS + "')";
    }

    // Texto como literal XPath. Si lleva apóstrofo se usan comillas dobles.
    private static String literal(String texto) {
        if (texto.contains("'")) {
            return "\"" + texto + "\"";
        }
        return "'" + texto + "'";
    }

    // Número sin notación científica (XPath 1.0 no la entiende)
    private static String numero(double valor) {
        return BigDecimal.valueOf(valor).toPlainString();
    }

    // ---------------------------------------------------------------
    private static void mostrar(List<String> lineas, long ms) {
        System.out.println("Total películas encontradas: " + lineas.size());
        for (String linea : lineas) {
            System.out.println("  " + linea);
        }
        System.out.println("Tiempo de búsqueda: " + ms + " ms");
    }
}