package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.Compra;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificadorCompra implements ObservadorCompra {
    private final Map<Long, List<String>> notificacionesPorCliente;

    public NotificadorCompra() {
        this.notificacionesPorCliente = new HashMap<>();
    }

    @Override
    public void alConfirmarse(Compra compra) {
        if (compra != null && compra.getCliente() != null && compra.getCliente().getId() != null) {
            Long clienteId = compra.getCliente().getId();
            String titulo = compra.getFuncion() != null && compra.getFuncion().getPelicula() != null
                    ? compra.getFuncion().getPelicula().getTitulo()
                    : "";
            String mensaje = "Compra #" + compra.getId() + " confirmada para " + titulo;
            this.notificacionesPorCliente.computeIfAbsent(clienteId, k -> new ArrayList<>()).add(mensaje);
        }
    }

    @Override
    public void alCancelarse(Compra compra, BigDecimal porcentajeReembolsado) {
        if (compra != null && compra.getCliente() != null && compra.getCliente().getId() != null) {
            Long clienteId = compra.getCliente().getId();
            BigDecimal porcentaje = porcentajeReembolsado != null
                    ? porcentajeReembolsado.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            String mensaje = "Compra #" + compra.getId() + " cancelada con reembolso del " + porcentaje + "%";
            this.notificacionesPorCliente.computeIfAbsent(clienteId, k -> new ArrayList<>()).add(mensaje);
        }
    }

    public List<String> obtenerNotificaciones(Cliente cliente) {
        if (cliente == null || cliente.getId() == null) {
            return Collections.emptyList();
        }
        List<String> lista = this.notificacionesPorCliente.get(cliente.getId());
        return lista != null ? Collections.unmodifiableList(lista) : Collections.emptyList();
    }
}
