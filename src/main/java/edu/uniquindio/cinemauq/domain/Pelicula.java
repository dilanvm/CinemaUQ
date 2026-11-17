package edu.uniquindio.cinemauq.domain;

public class Pelicula {
    private Long id;
    private String titulo;
    private String sinopsis;
    private int duracionMinutos;
    private String clasificacionEdad;
    private String genero;
    private boolean activa;

    public Pelicula(Long id, String titulo, String sinopsis, int duracionMinutos, String clasificacionEdad, String genero) {
        this(id, titulo, sinopsis, duracionMinutos, clasificacionEdad, genero, true);
    }

    public Pelicula(Long id, String titulo, String sinopsis, int duracionMinutos, String clasificacionEdad, String genero, boolean activa) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El titulo no puede estar vacio");
        }
        if (duracionMinutos <= 0) {
            throw new IllegalArgumentException("La duracion debe ser mayor a 0");
        }
        this.id = id;
        this.titulo = titulo.trim();
        this.sinopsis = sinopsis != null ? sinopsis.trim() : "";
        this.duracionMinutos = duracionMinutos;
        this.clasificacionEdad = clasificacionEdad != null ? clasificacionEdad.trim() : "";
        this.genero = genero != null ? genero.trim() : "";
        this.activa = activa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public String getClasificacionEdad() {
        return clasificacionEdad;
    }

    public void setClasificacionEdad(String clasificacionEdad) {
        this.clasificacionEdad = clasificacionEdad;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}
