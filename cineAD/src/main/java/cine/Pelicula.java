package cine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;

// Orden en el que se escriben los elementos en el XML (y en el JSON, que sigue el orden de los campos)
@XmlType(propOrder = { "idImdb", "titulo", "tituloOriginal", "anio", "fechaPublicacion", "generos",
        "duracion", "paises", "idiomas", "directores", "actores", "descripcion", "valoracionMedia",
        "votos", "presupuesto", "resenasUsuarios", "resenasCriticos" })
public class Pelicula implements Serializable {

    private static final long serialVersionUID = 1L;

    // Los campos se llaman igual que las claves del JSON (Gson usa el nombre del campo)
    private String idImdb;
    private String titulo;
    private String tituloOriginal;
    private int anio;
    private String fechaPublicacion; // formato aaaa-mm-dd
    private List<String> generos = new ArrayList<>();
    private int duracion;
    private List<String> paises = new ArrayList<>();
    private List<String> idiomas = new ArrayList<>();
    private List<Persona> directores = new ArrayList<>();
    private List<Persona> actores = new ArrayList<>();
    private String descripcion;
    private double valoracionMedia;
    private int votos;
    private Double presupuesto; // Double (con mayúscula): puede ser null si no viene en el CSV
    private double resenasUsuarios;
    private double resenasCriticos;

    public Pelicula() {
    }

    public String getIdImdb() {
        return idImdb;
    }

    public void setIdImdb(String idImdb) {
        this.idImdb = idImdb;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTituloOriginal() {
        return tituloOriginal;
    }

    public void setTituloOriginal(String tituloOriginal) {
        this.tituloOriginal = tituloOriginal;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(String fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    // Lista con contenedor en plural y elemento en singular: <generos><genero>
    @XmlElementWrapper(name = "generos")
    @XmlElement(name = "genero")
    public List<String> getGeneros() {
        return generos;
    }

    public void setGeneros(List<String> generos) {
        this.generos = generos;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    @XmlElementWrapper(name = "paises")
    @XmlElement(name = "pais")
    public List<String> getPaises() {
        return paises;
    }

    public void setPaises(List<String> paises) {
        this.paises = paises;
    }

    @XmlElementWrapper(name = "idiomas")
    @XmlElement(name = "idioma")
    public List<String> getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(List<String> idiomas) {
        this.idiomas = idiomas;
    }

    @XmlElementWrapper(name = "directores")
    @XmlElement(name = "director")
    public List<Persona> getDirectores() {
        return directores;
    }

    public void setDirectores(List<Persona> directores) {
        this.directores = directores;
    }

    @XmlElementWrapper(name = "actores")
    @XmlElement(name = "actor")
    public List<Persona> getActores() {
        return actores;
    }

    public void setActores(List<Persona> actores) {
        this.actores = actores;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getValoracionMedia() {
        return valoracionMedia;
    }

    public void setValoracionMedia(double valoracionMedia) {
        this.valoracionMedia = valoracionMedia;
    }

    public int getVotos() {
        return votos;
    }

    public void setVotos(int votos) {
        this.votos = votos;
    }

    public Double getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(Double presupuesto) {
        this.presupuesto = presupuesto;
    }

    public double getResenasUsuarios() {
        return resenasUsuarios;
    }

    public void setResenasUsuarios(double resenasUsuarios) {
        this.resenasUsuarios = resenasUsuarios;
    }

    public double getResenasCriticos() {
        return resenasCriticos;
    }

    public void setResenasCriticos(double resenasCriticos) {
        this.resenasCriticos = resenasCriticos;
    }
}