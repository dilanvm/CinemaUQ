package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.Funcion;
import edu.uniquindio.cinemauq.domain.RepositorioCompras;
import edu.uniquindio.cinemauq.domain.RepositorioFunciones;
import edu.uniquindio.cinemauq.domain.RepositorioSalas;
import edu.uniquindio.cinemauq.domain.Sala;
import edu.uniquindio.cinemauq.domain.estado.EstadoConfirmada;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FabricaReporteOcupacionSalas extends FabricaReporte {
    private final RepositorioCompras repositorioCompras;
    private final RepositorioSalas repositorioSalas;
    private final RepositorioFunciones repositorioFunciones;
    private final Clock clock;

    public FabricaReporteOcupacionSalas(RepositorioCompras repositorioCompras,
                                        RepositorioSalas repositorioSalas,
                                        RepositorioFunciones repositorioFunciones,
                                        Clock clock) {
        this.repositorioCompras = repositorioCompras;
        this.repositorioSalas = repositorioSalas;
        this.repositorioFunciones = repositorioFunciones;
        this.clock = clock;
    }

    @Override
    public Reporte crearReporte(LocalDate desde, LocalDate hasta) {
        LocalDateTime ahora = LocalDateTime.now(this.clock);
        List<Sala> salas = repositorioSalas.listar();
        List<Funcion> funciones = repositorioFunciones.listar();
        List<Compra> compras = repositorioCompras.listar();

        Map<Long, Integer> funcionesPorSala = new LinkedHashMap<>();
        Map<Long, Integer> entradasPorSala = new LinkedHashMap<>();

        for (Sala s : salas) {
            funcionesPorSala.put(s.getId(), 0);
            entradasPorSala.put(s.getId(), 0);
        }

        for (Funcion f : funciones) {
            LocalDate fechaF = f.getFechaHora().toLocalDate();
            boolean dentro = (desde == null || !fechaF.isBefore(desde)) && (hasta == null || !fechaF.isAfter(hasta));
            if (dentro && f.getSala() != null) {
                Long idSala = f.getSala().getId();
                funcionesPorSala.put(idSala, funcionesPorSala.getOrDefault(idSala, 0) + 1);
            }
        }

        int totalEntradas = 0;
        int totalCapacidadOfertada = 0;

        for (Compra c : compras) {
            if (c.getEstado() instanceof EstadoConfirmada) {
                LocalDate fecha = c.getFechaCompra().toLocalDate();
                boolean dentro = (desde == null || !fecha.isBefore(desde)) && (hasta == null || !fecha.isAfter(hasta));
                if (dentro && c.getFuncion() != null && c.getFuncion().getSala() != null) {
                    Long idSala = c.getFuncion().getSala().getId();
                    int cant = c.getEntradas().size();
                    entradasPorSala.put(idSala, entradasPorSala.getOrDefault(idSala, 0) + cant);
                    totalEntradas += cant;
                }
            }
        }

        List<String> columnas = List.of("Sala", "Capacidad", "Funciones", "Entradas Vendidas", "Ocupacion");
        List<List<String>> filas = new ArrayList<>();

        for (Sala s : salas) {
            int cap = s.getCapacidad();
            int nFunciones = funcionesPorSala.getOrDefault(s.getId(), 0);
            int nEntradas = entradasPorSala.getOrDefault(s.getId(), 0);
            int capacidadTotalSala = cap * nFunciones;
            totalCapacidadOfertada += capacidadTotalSala;

            String porcentaje;
            if (capacidadTotalSala > 0) {
                BigDecimal pct = BigDecimal.valueOf(nEntradas)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(capacidadTotalSala), 2, RoundingMode.HALF_UP);
                porcentaje = pct.toPlainString() + "%";
            } else {
                porcentaje = "0.00%";
            }

            filas.add(List.of(s.getNombre(), String.valueOf(cap), String.valueOf(nFunciones), String.valueOf(nEntradas), porcentaje));
        }

        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Total entradas vendidas", String.valueOf(totalEntradas));
        if (totalCapacidadOfertada > 0) {
            BigDecimal prom = BigDecimal.valueOf(totalEntradas)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalCapacidadOfertada), 2, RoundingMode.HALF_UP);
            totales.put("Promedio ocupacion general", prom.toPlainString() + "%");
        } else {
            totales.put("Promedio ocupacion general", "0.00%");
        }

        return new ReporteOcupacionSalas(ahora, desde, hasta, columnas, filas, totales);
    }
}
