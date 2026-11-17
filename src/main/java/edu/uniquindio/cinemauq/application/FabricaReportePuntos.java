package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.MovimientoPuntos;
import edu.uniquindio.cinemauq.domain.RepositorioUsuarios;
import edu.uniquindio.cinemauq.domain.TipoMovimientoPuntos;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FabricaReportePuntos extends FabricaReporte {
    private final RepositorioUsuarios repositorioUsuarios;
    private final Clock clock;

    public FabricaReportePuntos(RepositorioUsuarios repositorioUsuarios, Clock clock) {
        this.repositorioUsuarios = repositorioUsuarios;
        this.clock = clock;
    }

    @Override
    public Reporte crearReporte(LocalDate desde, LocalDate hasta) {
        LocalDateTime ahora = LocalDateTime.now(this.clock);
        LocalDate hoy = ahora.toLocalDate();
        List<Cliente> clientes = repositorioUsuarios.listarClientes();

        List<String> columnas = List.of("Cliente", "Puntos Acumulados", "Puntos Redimidos", "Saldo Vigente");
        List<List<String>> filas = new ArrayList<>();

        int totalAcumulados = 0;
        int totalRedimidos = 0;
        int totalSaldoVigente = 0;

        for (Cliente cliente : clientes) {
            int acumulados = 0;
            int redimidos = 0;
            if (cliente.getCuentaPuntos() != null) {
                for (MovimientoPuntos mov : cliente.getCuentaPuntos().consultarMovimientos()) {
                    LocalDate fecha = mov.getFecha();
                    boolean dentro = (desde == null || !fecha.isBefore(desde)) && (hasta == null || !fecha.isAfter(hasta));
                    if (dentro) {
                        if (mov.getTipo() == TipoMovimientoPuntos.ACUMULACION) {
                            acumulados += mov.getCantidad();
                        } else if (mov.getTipo() == TipoMovimientoPuntos.REDENCION) {
                            redimidos += mov.getCantidad();
                        }
                    }
                }
                int saldoVigente = cliente.getCuentaPuntos().consultarSaldoVigente(hoy);
                totalAcumulados += acumulados;
                totalRedimidos += redimidos;
                totalSaldoVigente += saldoVigente;

                filas.add(List.of(
                        cliente.getNombre(),
                        String.valueOf(acumulados),
                        String.valueOf(redimidos),
                        String.valueOf(saldoVigente)
                ));
            }
        }

        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Total puntos acumulados", String.valueOf(totalAcumulados));
        totales.put("Total puntos redimidos", String.valueOf(totalRedimidos));
        totales.put("Total saldo vigente", String.valueOf(totalSaldoVigente));

        return new ReportePuntos(ahora, desde, hasta, columnas, filas, totales);
    }
}
