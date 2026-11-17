package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Asiento;
import edu.uniquindio.cinemauq.domain.AsientoNoDisponibleException;
import edu.uniquindio.cinemauq.domain.Funcion;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ServicioDisponibilidad {
    private final Map<Long, Set<Long>> ocupadosPorFuncion;

    public ServicioDisponibilidad() {
        this.ocupadosPorFuncion = new HashMap<>();
    }

    public synchronized boolean estaDisponible(Funcion funcion, Asiento asiento) {
        if (funcion == null || asiento == null || funcion.getId() == null || asiento.getId() == null) {
            return false;
        }
        Set<Long> ocupados = this.ocupadosPorFuncion.get(funcion.getId());
        return ocupados == null || !ocupados.contains(asiento.getId());
    }

    public synchronized void reservar(Funcion funcion, Asiento asiento) {
        if (funcion == null || asiento == null || funcion.getId() == null || asiento.getId() == null) {
            throw new IllegalArgumentException("La funcion y el asiento con identificador son obligatorios");
        }
        if (!estaDisponible(funcion, asiento)) {
            throw new AsientoNoDisponibleException("El asiento " + asiento.getFila() + asiento.getNumero() + " no esta disponible");
        }
        this.ocupadosPorFuncion.computeIfAbsent(funcion.getId(), k -> new HashSet<>()).add(asiento.getId());
    }

    public synchronized void liberar(Funcion funcion, Asiento asiento) {
        if (funcion != null && asiento != null && funcion.getId() != null && asiento.getId() != null) {
            Set<Long> ocupados = this.ocupadosPorFuncion.get(funcion.getId());
            if (ocupados != null) {
                ocupados.remove(asiento.getId());
            }
        }
    }

    public synchronized Set<Long> obtenerOcupados(Funcion funcion) {
        if (funcion == null || funcion.getId() == null) {
            return Collections.emptySet();
        }
        Set<Long> ocupados = this.ocupadosPorFuncion.get(funcion.getId());
        return ocupados == null ? Collections.emptySet() : Collections.unmodifiableSet(new HashSet<>(ocupados));
    }
}
