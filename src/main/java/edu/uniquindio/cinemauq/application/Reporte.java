package edu.uniquindio.cinemauq.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface Reporte {
    String getTitulo();
    LocalDateTime getFechaGeneracion();
    LocalDate getDesde();
    LocalDate getHasta();
    List<String> getColumnas();
    List<List<String>> getFilas();
    Map<String, String> getTotales();
}
