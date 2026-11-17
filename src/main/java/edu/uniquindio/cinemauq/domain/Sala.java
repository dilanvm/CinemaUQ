package edu.uniquindio.cinemauq.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sala {
    private String nombre;
    private Long id;
    private List<Asiento> asientos;
    private boolean activa;

    public Sala(String nombre, Long id, List<Asiento> asientos, boolean activa) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }
        this.nombre = nombre.trim();
        this.id = id;
        this.asientos = asientos != null ? new ArrayList<>(asientos) : new ArrayList<>();
        this.activa = activa;
    }

    public void agregarAsiento(Asiento asiento) {
        if (asiento == null) {
            throw new IllegalArgumentException("El asiento es obligatorio");
        }
        for (Asiento a : asientos) {
            if (a.getFila().equalsIgnoreCase(asiento.getFila()) && a.getNumero() == asiento.getNumero()) {
                throw new IllegalArgumentException("Ya existe ese asiento en la sala");
            }
        }
        asientos.add(asiento);
    }

    public int getCapacidad() {
        return asientos.size();
    }

    public List<Asiento> getAsientos() {
        return Collections.unmodifiableList(asientos);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}
