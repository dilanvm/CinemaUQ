package edu.uniquindio.cinemauq.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ReporteProductosVendidos extends ReporteBase {
    public ReporteProductosVendidos(LocalDateTime fechaGeneracion, LocalDate desde, LocalDate hasta,
                                   List<String> columnas, List<List<String>> filas, Map<String, String> totales) {
        super("Reporte de productos y combos vendidos - CinemaUQ", fechaGeneracion, desde, hasta, columnas, filas, totales);
    }
}
