package edu.uniquindio.cinemauq.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ReporteOcupacionSalas extends ReporteBase {
    public ReporteOcupacionSalas(LocalDateTime fechaGeneracion, LocalDate desde, LocalDate hasta,
                                 List<String> columnas, List<List<String>> filas, Map<String, String> totales) {
        super("Reporte de ocupacion de salas - CinemaUQ", fechaGeneracion, desde, hasta, columnas, filas, totales);
    }
}
