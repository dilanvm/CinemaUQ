package edu.uniquindio.cinemauq.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class ReporteBase implements Reporte {
    private final String titulo;
    private final LocalDateTime fechaGeneracion;
    private final LocalDate desde;
    private final LocalDate hasta;
    private final List<String> columnas;
    private final List<List<String>> filas;
    private final Map<String, String> totales;

    public ReporteBase(String titulo, LocalDateTime fechaGeneracion, LocalDate desde, LocalDate hasta,
                       List<String> columnas, List<List<String>> filas, Map<String, String> totales) {
        this.titulo = titulo;
        this.fechaGeneracion = fechaGeneracion;
        this.desde = desde;
        this.hasta = hasta;
        this.columnas = columnas != null ? Collections.unmodifiableList(columnas) : Collections.emptyList();
        this.filas = filas != null ? Collections.unmodifiableList(filas) : Collections.emptyList();
        this.totales = totales != null ? Collections.unmodifiableMap(totales) : Collections.emptyMap();
    }

    @Override
    public String getTitulo() {
        return titulo;
    }

    @Override
    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    @Override
    public LocalDate getDesde() {
        return desde;
    }

    @Override
    public LocalDate getHasta() {
        return hasta;
    }

    @Override
    public List<String> getColumnas() {
        return columnas;
    }

    @Override
    public List<List<String>> getFilas() {
        return filas;
    }

    @Override
    public Map<String, String> getTotales() {
        return totales;
    }
}
