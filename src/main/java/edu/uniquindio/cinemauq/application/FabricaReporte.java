package edu.uniquindio.cinemauq.application;

import java.time.LocalDate;

public abstract class FabricaReporte {
    public abstract Reporte crearReporte(LocalDate desde, LocalDate hasta);
}
