package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.RepositorioCompras;
import edu.uniquindio.cinemauq.domain.estado.EstadoCancelada;
import edu.uniquindio.cinemauq.domain.estado.EstadoReembolsada;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FabricaReporteCancelacionesReembolsos extends FabricaReporte {
    private final RepositorioCompras repositorioCompras;
    private final Clock clock;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public FabricaReporteCancelacionesReembolsos(RepositorioCompras repositorioCompras, Clock clock) {
        this.repositorioCompras = repositorioCompras;
        this.clock = clock;
    }

    @Override
    public Reporte crearReporte(LocalDate desde, LocalDate hasta) {
        LocalDateTime ahora = LocalDateTime.now(this.clock);
        List<Compra> compras = repositorioCompras.listar();

        List<String> columnas = List.of("Id Compra", "Fecha Compra", "Cliente", "Pelicula", "Monto Pagado", "Estado");
        List<List<String>> filas = new ArrayList<>();

        int totalCancelaciones = 0;
        BigDecimal totalDinero = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        for (Compra c : compras) {
            if (c.getEstado() instanceof EstadoCancelada || c.getEstado() instanceof EstadoReembolsada) {
                LocalDate fecha = c.getFechaCompra().toLocalDate();
                boolean dentro = (desde == null || !fecha.isBefore(desde)) && (hasta == null || !fecha.isAfter(hasta));
                if (dentro) {
                    totalCancelaciones++;
                    BigDecimal monto = c.getMontoPagadoTarjeta();
                    totalDinero = totalDinero.add(monto).setScale(2, RoundingMode.HALF_UP);

                    String tituloPelicula = c.getFuncion() != null && c.getFuncion().getPelicula() != null ?
                            c.getFuncion().getPelicula().getTitulo() : "-";
                    String nombreCliente = c.getCliente() != null ? c.getCliente().getNombre() : "-";
                    String estadoTexto = c.getEstado() instanceof EstadoReembolsada ? "REEMBOLSADA" : "CANCELADA";

                    filas.add(List.of(
                            String.valueOf(c.getId()),
                            c.getFechaCompra().format(FORMATO_FECHA),
                            nombreCliente,
                            tituloPelicula,
                            "$" + monto.toPlainString(),
                            estadoTexto
                    ));
                }
            }
        }

        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Total cancelaciones", String.valueOf(totalCancelaciones));
        totales.put("Total monto pagado compras canceladas", "$" + totalDinero.toPlainString());

        return new ReporteCancelacionesReembolsos(ahora, desde, hasta, columnas, filas, totales);
    }
}
