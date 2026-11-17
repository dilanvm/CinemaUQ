package edu.uniquindio.cinemauq.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ReporteCancelacionesReembolsos extends ReporteBase {
    public ReporteCancelacionesReembolsos(LocalDateTime fechaGeneracion, LocalDate desde, LocalDate hasta,
                                          List<String> columnas, List<List<String>> filas, Map<String, String> totales) {
        super("Reporte de cancelaciones y reembolsos - CinemaUQ", fechaGeneracion, desde, hasta, columnas, filas, totales);
    }
}
