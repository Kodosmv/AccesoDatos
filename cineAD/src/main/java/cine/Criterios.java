package cine;

// Los cinco criterios de búsqueda. Texto vacío o null = ese criterio no filtra.
public class Criterios {

    private final String genero;
    private final String titulo;
    private final String pais;
    private final Double presupuestoMax;
    private final Double valoracionMin;

    public Criterios(String genero, String titulo, String pais, Double presupuestoMax, Double valoracionMin) {
        this.genero = genero;
        this.titulo = titulo;
        this.pais = pais;
        this.presupuestoMax = presupuestoMax;
        this.valoracionMin = valoracionMin;
    }

    public String getGenero() {
        return genero;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getPais() {
        return pais;
    }

    public Double getPresupuestoMax() {
        return presupuestoMax;
    }

    public Double getValoracionMin() {
        return valoracionMin;
    }
}