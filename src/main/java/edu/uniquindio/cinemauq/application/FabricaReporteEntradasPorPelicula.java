package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.Entrada;
import edu.uniquindio.cinemauq.domain.Funcion;
import edu.uniquindio.cinemauq.domain.Pelicula;
import edu.uniquindio.cinemauq.domain.RepositorioCompras;
import edu.uniquindio.cinemauq.domain.RepositorioFunciones;
import edu.uniquindio.cinemauq.domain.RepositorioPeliculas;
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

public class FabricaReporteEntradasPorPelicula extends FabricaReporte {
    private final RepositorioCompras repositorioCompras;
    private final RepositorioPeliculas repositorioPeliculas;
    private final RepositorioFunciones repositorioFunciones;
    private final Clock clock;

    public FabricaReporteEntradasPorPelicula(RepositorioCompras repositorioCompras,
                                            RepositorioPeliculas repositorioPeliculas,
                                            RepositorioFunciones repositorioFunciones,
                                            Clock clock) {
        this.repositorioCompras = repositorioCompras;
        this.repositorioPeliculas = repositorioPeliculas;
        this.repositorioFunciones = repositorioFunciones;
        this.clock = clock;
    }

    @Override
    public Reporte crearReporte(LocalDate desde, LocalDate hasta) {
        LocalDateTime ahora = LocalDateTime.now(this.clock);
        List<Pelicula> peliculas = repositorioPeliculas.listar();
        List<Funcion> funciones = repositorioFunciones.listar();
        List<Compra> compras = repositorioCompras.listar();

        Map<Long, Integer> funcionesPorPelicula = new LinkedHashMap<>();
        Map<Long, Integer> entradasPorPelicula = new LinkedHashMap<>();
        Map<Long, BigDecimal> recaudadoPorPelicula = new LinkedHashMap<>();

        for (Pelicula p : peliculas) {
            funcionesPorPelicula.put(p.getId(), 0);
            entradasPorPelicula.put(p.getId(), 0);
            recaudadoPorPelicula.put(p.getId(), BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        }

        for (Funcion f : funciones) {
            if (f.getPelicula() != null) {
                Long idP = f.getPelicula().getId();
                funcionesPorPelicula.put(idP, funcionesPorPelicula.getOrDefault(idP, 0) + 1);
            }
        }

        int totalEntradas = 0;
        BigDecimal totalRecaudado = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        for (Compra c : compras) {
            if (c.getEstado() instanceof EstadoConfirmada) {
                LocalDate fecha = c.getFechaCompra().toLocalDate();
                boolean dentro = (desde == null || !fecha.isBefore(desde)) && (hasta == null || !fecha.isAfter(hasta));
                if (dentro && c.getFuncion() != null && c.getFuncion().getPelicula() != null) {
                    Long idP = c.getFuncion().getPelicula().getId();
                    int cant = c.getEntradas().size();
                    BigDecimal subtotalEntradas = BigDecimal.ZERO;
                    for (Entrada e : c.getEntradas()) {
                        subtotalEntradas = subtotalEntradas.add(e.getPrecio());
                    }
                    subtotalEntradas = subtotalEntradas.setScale(2, RoundingMode.HALF_UP);

                    entradasPorPelicula.put(idP, entradasPorPelicula.getOrDefault(idP, 0) + cant);
                    recaudadoPorPelicula.put(idP, recaudadoPorPelicula.getOrDefault(idP, BigDecimal.ZERO).add(subtotalEntradas).setScale(2, RoundingMode.HALF_UP));

                    totalEntradas += cant;
                    totalRecaudado = totalRecaudado.add(subtotalEntradas).setScale(2, RoundingMode.HALF_UP);
                }
            }
        }

        List<String> columnas = List.of("Pelicula", "Funciones", "Entradas Vendidas", "Recaudado");
        List<List<String>> filas = new ArrayList<>();

        for (Pelicula p : peliculas) {
            int numFunciones = funcionesPorPelicula.getOrDefault(p.getId(), 0);
            int numEntradas = entradasPorPelicula.getOrDefault(p.getId(), 0);
            BigDecimal recaudado = recaudadoPorPelicula.getOrDefault(p.getId(), BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            filas.add(List.of(p.getTitulo(), String.valueOf(numFunciones), String.valueOf(numEntradas), "$" + recaudado.toPlainString()));
        }

        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Total entradas", String.valueOf(totalEntradas));
        totales.put("Total recaudado entradas", "$" + totalRecaudado.toPlainString());

        return new ReporteEntradasPorPelicula(ahora, desde, hasta, columnas, filas, totales);
    }
}
