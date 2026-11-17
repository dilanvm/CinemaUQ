package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Asiento;
import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.CompraInvalidaException;
import edu.uniquindio.cinemauq.domain.DetalleCompra;
import edu.uniquindio.cinemauq.domain.ElementoVendible;
import edu.uniquindio.cinemauq.domain.Entrada;
import edu.uniquindio.cinemauq.domain.EstadoFuncion;
import edu.uniquindio.cinemauq.domain.Funcion;
import edu.uniquindio.cinemauq.domain.estado.EstadoPendiente;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class CompraBuilder {
    private final Cliente cliente;
    private final Clock clock;
    private final List<Entrada> entradas;
    private final List<DetalleCompra> detalles;
    private int puntosUsados;

    public CompraBuilder(Cliente cliente) {
        this(cliente, Clock.system(ZoneId.of("America/Bogota")));
    }

    public CompraBuilder(Cliente cliente, Clock clock) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        this.cliente = cliente;
        this.clock = clock != null ? clock : Clock.system(ZoneId.of("America/Bogota"));
        this.entradas = new ArrayList<>();
        this.detalles = new ArrayList<>();
        this.puntosUsados = 0;
    }

    public CompraBuilder agregarEntrada(Funcion funcion, Asiento asiento) {
        if (funcion == null || asiento == null) {
            throw new IllegalArgumentException("La funcion y el asiento son obligatorios");
        }
        this.entradas.add(new Entrada(funcion, asiento));
        return this;
    }

    public CompraBuilder agregarElemento(ElementoVendible elemento, int cantidad) {
        if (elemento == null) {
            throw new IllegalArgumentException("El elemento vendible es obligatorio");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        this.detalles.add(new DetalleCompra(elemento, cantidad));
        return this;
    }

    public CompraBuilder usarPuntos(int puntos) {
        this.puntosUsados = Math.max(0, puntos);
        return this;
    }

    public Compra construir() {
        if (this.entradas.isEmpty()) {
            throw new CompraInvalidaException("La compra debe tener al menos una entrada");
        }
        Funcion primeraFuncion = this.entradas.get(0).getFuncion();
        for (Entrada entrada : this.entradas) {
            if (!entrada.getFuncion().getId().equals(primeraFuncion.getId())) {
                throw new CompraInvalidaException("Todas las entradas deben ser de la misma funcion");
            }
        }
        if (primeraFuncion.getEstadoFuncion() != EstadoFuncion.PROGRAMADA) {
            throw new CompraInvalidaException("La funcion no esta programada");
        }
        LocalDateTime ahora = LocalDateTime.now(this.clock);
        if (!primeraFuncion.getFechaHora().isAfter(ahora)) {
            throw new CompraInvalidaException("La fecha de la funcion debe ser futura");
        }

        BigDecimal totalEntradas = BigDecimal.ZERO;
        for (Entrada entrada : this.entradas) {
            totalEntradas = totalEntradas.add(entrada.getPrecio());
        }

        BigDecimal totalDetalles = BigDecimal.ZERO;
        for (DetalleCompra detalle : this.detalles) {
            totalDetalles = totalDetalles.add(detalle.getSubtotal());
        }

        BigDecimal total = totalEntradas.add(totalDetalles).setScale(2, RoundingMode.HALF_UP);

        BigDecimal valorPorPunto = BigDecimal.valueOf(100);
        int maxPuntos = total.divideToIntegralValue(valorPorPunto).intValue();
        int puntosAplicados = Math.min(this.puntosUsados, maxPuntos);
        BigDecimal valorPuntos = BigDecimal.valueOf(puntosAplicados).multiply(valorPorPunto).setScale(2, RoundingMode.HALF_UP);
        BigDecimal montoTarjeta = total.subtract(valorPuntos).setScale(2, RoundingMode.HALF_UP);

        Compra compra = new Compra();
        compra.setCliente(this.cliente);
        compra.setFuncion(primeraFuncion);
        compra.setFechaCompra(ahora);
        compra.setEntradas(this.entradas);
        compra.setDetalles(this.detalles);
        compra.setTotal(total);
        compra.setPuntosUsados(puntosAplicados);
        compra.setValorPuntos(valorPuntos);
        compra.setMontoPagadoTarjeta(montoTarjeta);
        compra.setEstado(new EstadoPendiente());

        return compra;
    }
}
