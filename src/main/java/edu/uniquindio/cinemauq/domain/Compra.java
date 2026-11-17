package edu.uniquindio.cinemauq.domain;

import edu.uniquindio.cinemauq.domain.estado.EstadoCompra;
import edu.uniquindio.cinemauq.domain.estado.EstadoPendiente;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Compra {
    private Long id;
    private Cliente cliente;
    private Funcion funcion;
    private LocalDateTime fechaCompra;
    private List<Entrada> entradas;
    private List<DetalleCompra> detalles;
    private BigDecimal total;
    private int puntosUsados;
    private BigDecimal valorPuntos;
    private BigDecimal montoPagadoTarjeta;
    private int puntosGanados;
    private LotePuntos loteGanado;
    private List<ConsumoPuntos> consumosPuntos;
    private EstadoCompra estado;

    public Compra() {
        this.entradas = new ArrayList<>();
        this.detalles = new ArrayList<>();
        this.consumosPuntos = new ArrayList<>();
        this.total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.valorPuntos = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.montoPagadoTarjeta = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.estado = new EstadoPendiente();
    }

    public void confirmar() {
        this.estado.confirmar(this);
    }

    public void cancelar() {
        this.estado.cancelar(this);
    }

    public void marcarReembolsada() {
        this.estado.marcarReembolsada(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public void setFuncion(Funcion funcion) {
        this.funcion = funcion;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public List<Entrada> getEntradas() {
        return Collections.unmodifiableList(entradas);
    }

    public void setEntradas(List<Entrada> entradas) {
        this.entradas = entradas != null ? new ArrayList<>(entradas) : new ArrayList<>();
    }

    public List<DetalleCompra> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public void setDetalles(List<DetalleCompra> detalles) {
        this.detalles = detalles != null ? new ArrayList<>(detalles) : new ArrayList<>();
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total != null ? total.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public int getPuntosUsados() {
        return puntosUsados;
    }

    public void setPuntosUsados(int puntosUsados) {
        this.puntosUsados = Math.max(0, puntosUsados);
    }

    public BigDecimal getValorPuntos() {
        return valorPuntos;
    }

    public void setValorPuntos(BigDecimal valorPuntos) {
        this.valorPuntos = valorPuntos != null ? valorPuntos.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getMontoPagadoTarjeta() {
        return montoPagadoTarjeta;
    }

    public void setMontoPagadoTarjeta(BigDecimal montoPagadoTarjeta) {
        this.montoPagadoTarjeta = montoPagadoTarjeta != null ? montoPagadoTarjeta.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public int getPuntosGanados() {
        return puntosGanados;
    }

    public void setPuntosGanados(int puntosGanados) {
        this.puntosGanados = Math.max(0, puntosGanados);
    }

    public LotePuntos getLoteGanado() {
        return loteGanado;
    }

    public void setLoteGanado(LotePuntos loteGanado) {
        this.loteGanado = loteGanado;
    }

    public List<ConsumoPuntos> getConsumosPuntos() {
        return Collections.unmodifiableList(consumosPuntos);
    }

    public void setConsumosPuntos(List<ConsumoPuntos> consumosPuntos) {
        this.consumosPuntos = consumosPuntos != null ? new ArrayList<>(consumosPuntos) : new ArrayList<>();
    }

    public EstadoCompra getEstado() {
        return estado;
    }

    public void setEstado(EstadoCompra estado) {
        this.estado = estado;
    }
}
