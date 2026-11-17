package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.Pelicula;
import edu.uniquindio.cinemauq.domain.RepositorioCompras;
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

public class FabricaReporteVentas extends FabricaReporte {
    private final RepositorioCompras repositorioCompras;
    private final RepositorioPeliculas repositorioPeliculas;
    private final Clock clock;

    public FabricaReporteVentas(RepositorioCompras repositorioCompras, RepositorioPeliculas repositorioPeliculas, Clock clock) {
        this.repositorioCompras = repositorioCompras;
        this.repositorioPeliculas = repositorioPeliculas;
        this.clock = clock;
    }

    @Override
    public Reporte crearReporte(LocalDate desde, LocalDate hasta) {
        LocalDateTime ahora = LocalDateTime.now(this.clock);
        List<Compra> compras = repositorioCompras.listar();
        List<Pelicula> peliculas = repositorioPeliculas.listar();

        Map<Long, Integer> entradasPorPelicula = new LinkedHashMap<>();
        Map<Long, BigDecimal> totalPorPelicula = new LinkedHashMap<>();

        for (Pelicula p : peliculas) {
            entradasPorPelicula.put(p.getId(), 0);
            totalPorPelicula.put(p.getId(), BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        }

        int totalEntradas = 0;
        BigDecimal totalVentas = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        for (Compra c : compras) {
            if (c.getEstado() instanceof EstadoConfirmada) {
                LocalDate fecha = c.getFechaCompra().toLocalDate();
                boolean dentro = (desde == null || !fecha.isBefore(desde)) && (hasta == null || !fecha.isAfter(hasta));
                if (dentro && c.getFuncion() != null && c.getFuncion().getPelicula() != null) {
                    Long idPelicula = c.getFuncion().getPelicula().getId();
                    int cant = c.getEntradas().size();
                    BigDecimal monto = c.getTotal();

                    entradasPorPelicula.put(idPelicula, entradasPorPelicula.getOrDefault(idPelicula, 0) + cant);
                    totalPorPelicula.put(idPelicula, totalPorPelicula.getOrDefault(idPelicula, BigDecimal.ZERO).add(monto).setScale(2, RoundingMode.HALF_UP));

                    totalEntradas += cant;
                    totalVentas = totalVentas.add(monto).setScale(2, RoundingMode.HALF_UP);
                }
            }
        }

        List<String> columnas = List.of("Pelicula", "Entradas", "Total");
        List<List<String>> filas = new ArrayList<>();

        for (Pelicula p : peliculas) {
            int entradas = entradasPorPelicula.getOrDefault(p.getId(), 0);
            BigDecimal total = totalPorPelicula.getOrDefault(p.getId(), BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            filas.add(List.of(p.getTitulo(), String.valueOf(entradas), "$" + total.toPlainString()));
        }

        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Total entradas", String.valueOf(totalEntradas));
        totales.put("Total ventas", "$" + totalVentas.toPlainString());

        return new ReporteVentas(ahora, desde, hasta, columnas, filas, totales);
    }
}
