package edu.uniquindio.cinemauq.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ReporteRecargas extends ReporteBase {
    public ReporteRecargas(LocalDateTime fechaGeneracion, LocalDate desde, LocalDate hasta,
                           List<String> columnas, List<List<String>> filas, Map<String, String> totales) {
        super("Reporte de recargas - CinemaUQ", fechaGeneracion, desde, hasta, columnas, filas, totales);
    }
}
