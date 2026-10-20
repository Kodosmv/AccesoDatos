package cine;

import java.util.Scanner;
import java.util.prefs.Preferences;

// Pregunta los cinco criterios por consola y recuerda las últimas respuestas con Preferences
public class PeticionCriterios {

    private final Preferences pf = Preferences.userRoot().node("cinebox");
    private final Scanner teclado;

    public PeticionCriterios(Scanner teclado) {
        this.teclado = teclado;
    }

    public Criterios pedir() {
        System.out.println("=== BUSCADOR DE PELÍCULAS ===");
        System.out.println("(Enter: mantener la última respuesta [entre corchetes]; -: deja sin filtrar)");

        String genero = preguntarTexto("genero", "Género (Drama, Crime, Comedy...)");
        String titulo = preguntarTexto("titulo", "Título (contiene)");
        String pais = preguntarTexto("pais", "País (USA, France, Spain...)");
        Double presupuesto = preguntarNumero("presupuesto", "Presupuesto máximo");
        Double valoracion = preguntarNumero("valoracion", "Valoración media mínima");

        return new Criterios(genero, titulo, pais, presupuesto, valoracion);
    }

    // Lee una respuesta aplicando las reglas: Enter = última, "-" = vacío, otro texto = nuevo valor
    private String leerRespuesta(String clave, String etiqueta) {
        String ultima = pf.get(clave, "");
        System.out.print(etiqueta + (ultima.isEmpty() ? "" : " [" + ultima + "]") + ": ");
        String respuesta = teclado.nextLine().trim();

        if (respuesta.isEmpty()) {
            return ultima;
        }
        if (respuesta.equals("-")) {
            return "";
        }
        return respuesta;
    }

    private String preguntarTexto(String clave, String etiqueta) {
        String valor = leerRespuesta(clave, etiqueta);
        pf.put(clave, valor);
        return valor;
    }

    private Double preguntarNumero(String clave, String etiqueta) {
        while (true) {
            String valor = leerRespuesta(clave, etiqueta).replace(',', '.'); // acepta 7,5 y 7.5
            if (valor.isEmpty()) {
                pf.put(clave, "");
                return null;
            }
            if (valor.matches("\\d+(\\.\\d+)?")) {
                pf.put(clave, valor);
                return Double.valueOf(valor);
            }
            System.out.println("Valor numérico no válido, inténtalo de nuevo.");
        }
    }
}