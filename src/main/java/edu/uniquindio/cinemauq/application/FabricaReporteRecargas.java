package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.MovimientoTarjeta;
import edu.uniquindio.cinemauq.domain.RepositorioUsuarios;
import edu.uniquindio.cinemauq.domain.TipoMovimiento;

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

public class FabricaReporteRecargas extends FabricaReporte {
    private final RepositorioUsuarios repositorioUsuarios;
    private final Clock clock;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public FabricaReporteRecargas(RepositorioUsuarios repositorioUsuarios, Clock clock) {
        this.repositorioUsuarios = repositorioUsuarios;
        this.clock = clock;
    }

    @Override
    public Reporte crearReporte(LocalDate desde, LocalDate hasta) {
        LocalDateTime ahora = LocalDateTime.now(this.clock);
        List<Cliente> clientes = repositorioUsuarios.listarClientes();

        List<String> columnas = List.of("Fecha", "Cliente", "Monto", "Descripcion");
        List<List<String>> filas = new ArrayList<>();

        int totalRecargas = 0;
        BigDecimal montoTotal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        for (Cliente cliente : clientes) {
            if (cliente.getTarjetaVirtual() != null) {
                for (MovimientoTarjeta mov : cliente.getTarjetaVirtual().consultarMovimientos()) {
                    if (mov.getTipo() == TipoMovimiento.RECARGA) {
                        LocalDate fechaMov = mov.getFechaHora().toLocalDate();
                        boolean dentro = (desde == null || !fechaMov.isBefore(desde)) && (hasta == null || !fechaMov.isAfter(hasta));
                        if (dentro) {
                            totalRecargas++;
                            montoTotal = montoTotal.add(mov.getMonto()).setScale(2, RoundingMode.HALF_UP);
                            filas.add(List.of(
                                    mov.getFechaHora().format(FORMATO_FECHA),
                                    cliente.getNombre(),
                                    "$" + mov.getMonto().toPlainString(),
                                    mov.getDescripcion()
                            ));
                        }
                    }
                }
            }
        }

        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Total recargas", String.valueOf(totalRecargas));
        totales.put("Monto total recargado", "$" + montoTotal.toPlainString());

        return new ReporteRecargas(ahora, desde, hasta, columnas, filas, totales);
    }
}
