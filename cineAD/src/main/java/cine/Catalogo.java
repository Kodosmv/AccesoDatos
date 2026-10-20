package cine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

// Elemento raíz del XML: <catalogo> con las <pelicula> directamente dentro
@XmlRootElement(name = "catalogo")
public class Catalogo implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Pelicula> peliculas = new ArrayList<>();

    public Catalogo() {
    }

    @XmlElement(name = "pelicula")
    public List<Pelicula> getPeliculas() {
        return peliculas;
    }

    public void setPeliculas(List<Pelicula> peliculas) {
        this.peliculas = peliculas;
    }
}