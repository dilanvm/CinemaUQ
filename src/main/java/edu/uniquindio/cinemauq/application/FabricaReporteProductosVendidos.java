package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.DetalleCompra;
import edu.uniquindio.cinemauq.domain.RepositorioCompras;
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

public class FabricaReporteProductosVendidos extends FabricaReporte {
    private final RepositorioCompras repositorioCompras;
    private final Clock clock;

    public FabricaReporteProductosVendidos(RepositorioCompras repositorioCompras, Clock clock) {
        this.repositorioCompras = repositorioCompras;
        this.clock = clock;
    }

    @Override
    public Reporte crearReporte(LocalDate desde, LocalDate hasta) {
        LocalDateTime ahora = LocalDateTime.now(this.clock);
        List<Compra> compras = repositorioCompras.listar();

        Map<String, Integer> cantidades = new LinkedHashMap<>();
        Map<String, BigDecimal> totalesPorItem = new LinkedHashMap<>();

        int totalUnidades = 0;
        BigDecimal totalDinero = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        for (Compra c : compras) {
            if (c.getEstado() instanceof EstadoConfirmada) {
                LocalDate fecha = c.getFechaCompra().toLocalDate();
                boolean dentro = (desde == null || !fecha.isBefore(desde)) && (hasta == null || !fecha.isAfter(hasta));
                if (dentro) {
                    for (DetalleCompra detalle : c.getDetalles()) {
                        String nombre = detalle.getElemento().getNombre();
                        int cant = detalle.getCantidad();
                        BigDecimal subtotal = detalle.getSubtotal();

                        cantidades.put(nombre, cantidades.getOrDefault(nombre, 0) + cant);
                        totalesPorItem.put(nombre, totalesPorItem.getOrDefault(nombre, BigDecimal.ZERO).add(subtotal).setScale(2, RoundingMode.HALF_UP));

                        totalUnidades += cant;
                        totalDinero = totalDinero.add(subtotal).setScale(2, RoundingMode.HALF_UP);
                    }
                }
            }
        }

        List<String> columnas = List.of("Producto / Combo", "Cantidad Vendida", "Total Ventas");
        List<List<String>> filas = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : cantidades.entrySet()) {
            String nombre = entry.getKey();
            int cant = entry.getValue();
            BigDecimal subtotal = totalesPorItem.getOrDefault(nombre, BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            filas.add(List.of(nombre, String.valueOf(cant), "$" + subtotal.toPlainString()));
        }

        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Total unidades vendidas", String.valueOf(totalUnidades));
        totales.put("Total ventas confiteria", "$" + totalDinero.toPlainString());

        return new ReporteProductosVendidos(ahora, desde, hasta, columnas, filas, totales);
    }
}
