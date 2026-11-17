package edu.uniquindio.cinemauq.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class Funcion {
    private Long id;
    private Pelicula pelicula;
    private Sala sala;
    private LocalDateTime fechaHora;
    private BigDecimal precioBase;
    private EstadoFuncion estadoFuncion;

    public Funcion(Long id, Pelicula pelicula, Sala sala, LocalDateTime fechaHora, BigDecimal precioBase, EstadoFuncion estadoFuncion) {
        if (pelicula == null || sala == null || fechaHora == null || estadoFuncion == null) {
            throw new IllegalArgumentException("Pelicula, Sala, FechaHora y EstadoFuncion son obligatorios");
        }
        if (precioBase == null || precioBase.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
        this.id = id;
        this.pelicula = pelicula;
        this.sala = sala;
        this.fechaHora = fechaHora;
        this.precioBase = precioBase.setScale(2, RoundingMode.HALF_UP);
        this.estadoFuncion = estadoFuncion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        if (pelicula == null) {
            throw new IllegalArgumentException("La pelicula es obligatoria");
        }
        this.pelicula = pelicula;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        if (sala == null) {
            throw new IllegalArgumentException("La sala es obligatoria");
        }
        this.sala = sala;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha y hora son obligatorias");
        }
        this.fechaHora = fechaHora;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        if (precioBase == null || precioBase.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
        this.precioBase = precioBase.setScale(2, RoundingMode.HALF_UP);
    }

    public EstadoFuncion getEstadoFuncion() {
        return estadoFuncion;
    }

    public void setEstadoFuncion(EstadoFuncion estadoFuncion) {
        if (estadoFuncion == null) {
            throw new IllegalArgumentException("El estado de la funcion es obligatorio");
        }
        this.estadoFuncion = estadoFuncion;
    }
}
