package edu.uniquindio.cinemauq.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CuentaPuntos {
    private final List<LotePuntos> lotes;
    private final List<MovimientoPuntos> movimientos;

    public CuentaPuntos() {
        this.lotes = new ArrayList<>();
        this.movimientos = new ArrayList<>();
    }

    public LotePuntos acumular(int puntos, LocalDate fecha, String descripcion) {
        if (puntos <= 0) {
            throw new IllegalArgumentException("Los puntos a acumular deben ser mayores que cero");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        LotePuntos lote = new LotePuntos(puntos, fecha);
        this.lotes.add(lote);
        this.movimientos.add(new MovimientoPuntos(TipoMovimientoPuntos.ACUMULACION, puntos, fecha, descripcion));
        return lote;
    }

    public List<ConsumoPuntos> redimir(int puntos, LocalDate hoy, String descripcion) {
        if (puntos <= 0) {
            throw new IllegalArgumentException("Los puntos a redimir deben ser mayores que cero");
        }
        if (hoy == null) {
            throw new IllegalArgumentException("La fecha actual es obligatoria");
        }
        int saldoVigente = consultarSaldoVigente(hoy);
        if (saldoVigente < puntos) {
            throw new PuntosInsuficientesException("Saldo de puntos insuficiente");
        }
        List<LotePuntos> vigentes = new ArrayList<>();
        for (LotePuntos l : lotes) {
            if (!l.estaVencido(hoy) && l.getCantidadRestante() > 0) {
                vigentes.add(l);
            }
        }
        vigentes.sort(Comparator.comparing(LotePuntos::getFechaVencimiento));

        List<ConsumoPuntos> consumos = new ArrayList<>();
        int restante = puntos;
        for (LotePuntos l : vigentes) {
            if (restante <= 0) {
                break;
            }
            int tomado = l.descontar(restante);
            if (tomado > 0) {
                consumos.add(new ConsumoPuntos(l, tomado));
                restante -= tomado;
            }
        }
        this.movimientos.add(new MovimientoPuntos(TipoMovimientoPuntos.REDENCION, puntos, hoy, descripcion));
        return consumos;
    }

    public void reintegrar(List<ConsumoPuntos> consumos, BigDecimal porcentaje, LocalDate hoy, String descripcion) {
        if (consumos == null || porcentaje == null || hoy == null) {
            return;
        }
        int totalReintegrado = 0;
        for (ConsumoPuntos c : consumos) {
            int usados = c.getCantidad();
            int aReintegrar = BigDecimal.valueOf(usados)
                    .multiply(porcentaje)
                    .setScale(0, RoundingMode.DOWN)
                    .intValue();
            if (aReintegrar > 0) {
                LotePuntos lote = c.getLote();
                if (!lote.estaVencido(hoy)) {
                    lote.reintegrar(aReintegrar);
                    totalReintegrado += aReintegrar;
                }
            }
        }
        if (totalReintegrado > 0) {
            this.movimientos.add(new MovimientoPuntos(TipoMovimientoPuntos.REINTEGRO, totalReintegrado, hoy, descripcion));
        }
    }

    public void revertir(LotePuntos lote, int puntos, LocalDate hoy, String descripcion) {
        if (lote == null || puntos <= 0 || hoy == null) {
            return;
        }
        int aRevertir = Math.min(puntos, lote.getCantidadRestante());
        if (aRevertir > 0) {
            lote.revertir(aRevertir);
            this.movimientos.add(new MovimientoPuntos(TipoMovimientoPuntos.REVERSION, aRevertir, hoy, descripcion));
        }
    }

    public int consultarSaldoVigente(LocalDate hoy) {
        if (hoy == null) {
            throw new IllegalArgumentException("La fecha actual es obligatoria");
        }
        int total = 0;
        for (LotePuntos l : lotes) {
            if (!l.estaVencido(hoy)) {
                total += l.getCantidadRestante();
            }
        }
        return total;
    }

    public List<LotePuntos> consultarLotes() {
        return Collections.unmodifiableList(this.lotes);
    }

    public List<MovimientoPuntos> consultarMovimientos() {
        return Collections.unmodifiableList(this.movimientos);
    }

    public void agregarLote(LotePuntos lote) {
        if (lote != null) {
            this.lotes.add(lote);
        }
    }
}
