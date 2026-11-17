package edu.uniquindio.cinemauq.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TarjetaVirtual {
    private Long id;
    private BigDecimal saldo;
    private boolean activa;
    private final List<MovimientoTarjeta> movimientos;

    public TarjetaVirtual() {
        this(null, BigDecimal.ZERO, true);
    }

    public TarjetaVirtual(Long id, BigDecimal saldoInicial, boolean activa) {
        this.id = id;
        this.saldo = saldoInicial != null ? saldoInicial.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.activa = activa;
        this.movimientos = new ArrayList<>();
    }

    public void recargar(BigDecimal monto) {
        recargar(monto, "Recarga de saldo");
    }

    public void recargar(BigDecimal monto, String descripcion) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a recargar debe ser mayor que cero");
        }
        BigDecimal montoNormalizado = monto.setScale(2, RoundingMode.HALF_UP);
        this.saldo = this.saldo.add(montoNormalizado).setScale(2, RoundingMode.HALF_UP);
        this.movimientos.add(new MovimientoTarjeta(TipoMovimiento.RECARGA, montoNormalizado, LocalDateTime.now(), descripcion));
    }

    public void pagar(BigDecimal monto, String descripcion) {
        if (!this.activa) {
            throw new TarjetaInactivaException("La tarjeta virtual esta inactiva");
        }
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a pagar debe ser mayor que cero");
        }
        BigDecimal montoNormalizado = monto.setScale(2, RoundingMode.HALF_UP);
        if (this.saldo.compareTo(montoNormalizado) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente en la tarjeta virtual");
        }
        this.saldo = this.saldo.subtract(montoNormalizado).setScale(2, RoundingMode.HALF_UP);
        this.movimientos.add(new MovimientoTarjeta(TipoMovimiento.PAGO, montoNormalizado, LocalDateTime.now(), descripcion));
    }

    public void reembolsar(BigDecimal monto, String descripcion) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a reembolsar debe ser mayor que cero");
        }
        BigDecimal montoNormalizado = monto.setScale(2, RoundingMode.HALF_UP);
        this.saldo = this.saldo.add(montoNormalizado).setScale(2, RoundingMode.HALF_UP);
        this.movimientos.add(new MovimientoTarjeta(TipoMovimiento.REEMBOLSO, montoNormalizado, LocalDateTime.now(), descripcion));
    }

    public BigDecimal consultarSaldo() {
        return this.saldo;
    }

    public List<MovimientoTarjeta> consultarMovimientos() {
        return Collections.unmodifiableList(this.movimientos);
    }

    public void activar() {
        this.activa = true;
    }

    public void desactivar() {
        this.activa = false;
    }

    public boolean isActiva() {
        return this.activa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
