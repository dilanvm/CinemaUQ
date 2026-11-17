package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.EstadoFuncion;
import edu.uniquindio.cinemauq.domain.Funcion;
import edu.uniquindio.cinemauq.domain.Pelicula;
import edu.uniquindio.cinemauq.domain.Sala;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class PlantillaFuncion {
    private Pelicula pelicula;
    private Sala sala;
    private BigDecimal precioBase;
    private int duracionMinutos;

    public PlantillaFuncion(Pelicula pelicula, Sala sala, BigDecimal precioBase, int duracionMinutos) {
        if (pelicula == null || sala == null || precioBase == null || precioBase.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Pelicula, Sala y precioBase valido son obligatorios");
        }
        this.pelicula = pelicula;
        this.sala = sala;
        this.precioBase = precioBase.setScale(2, RoundingMode.HALF_UP);
        this.duracionMinutos = duracionMinutos > 0 ? duracionMinutos : pelicula.getDuracionMinutos();
    }

    public Funcion clonarParaFecha(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha y hora son obligatorias");
        }
        return new Funcion(null, this.pelicula, this.sala, fechaHora, this.precioBase, EstadoFuncion.PROGRAMADA);
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase != null ? precioBase.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }
}
