package cine;

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "nombre", "apellidos" })
public class Persona implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private String apellidos;

    public Persona() {
    }

    public Persona(String nombre, String apellidos) {
        this.nombre = nombre;
        this.apellidos = apellidos;
    }

    // "Lana Wachowski" -> nombre = Lana, apellidos = Wachowski
    public static Persona desdeTexto(String texto) {
        String t = texto.trim();
        int i = t.indexOf(' ');
        if (i < 0) {
            return new Persona(t, "");
        }
        return new Persona(t.substring(0, i), t.substring(i + 1).trim());
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
}