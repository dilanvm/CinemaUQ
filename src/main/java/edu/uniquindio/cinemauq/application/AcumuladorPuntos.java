package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.ConsumoPuntos;
import edu.uniquindio.cinemauq.domain.LotePuntos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class AcumuladorPuntos implements ObservadorCompra {
    private final Clock clock;

    public AcumuladorPuntos() {
        this(Clock.system(ZoneId.of("America/Bogota")));
    }

    public AcumuladorPuntos(Clock clock) {
        this.clock = clock != null ? clock : Clock.system(ZoneId.of("America/Bogota"));
    }

    @Override
    public void alConfirmarse(Compra compra) {
        if (compra == null || compra.getMontoPagadoTarjeta() == null) {
            return;
        }
        BigDecimal pagadoTarjeta = compra.getMontoPagadoTarjeta();
        if (pagadoTarjeta.compareTo(BigDecimal.ZERO) > 0) {
            int puntosGanados = pagadoTarjeta.divideToIntegralValue(BigDecimal.valueOf(5000)).intValue();
            if (puntosGanados > 0) {
                LocalDate hoy = LocalDate.now(clock);
                LotePuntos lote = compra.getCliente().getCuentaPuntos().acumular(
                        puntosGanados,
                        hoy,
                        "Acumulacion por compra #" + compra.getId()
                );
                compra.setPuntosGanados(puntosGanados);
                compra.setLoteGanado(lote);
            }
        }
    }

    @Override
    public void alCancelarse(Compra compra, BigDecimal porcentajeReembolsado) {
        if (compra == null || porcentajeReembolsado == null) {
            return;
        }
        LocalDate hoy = LocalDate.now(clock);
        List<ConsumoPuntos> consumos = compra.getConsumosPuntos();
        if (consumos != null && !consumos.isEmpty()) {
            compra.getCliente().getCuentaPuntos().reintegrar(
                    consumos,
                    porcentajeReembolsado,
                    hoy,
                    "Reintegro por cancelacion compra #" + compra.getId()
            );
        }
        if (compra.getLoteGanado() != null && compra.getPuntosGanados() > 0) {
            int puntosARevertir = BigDecimal.valueOf(compra.getPuntosGanados())
                    .multiply(porcentajeReembolsado)
                    .setScale(0, RoundingMode.UP)
                    .intValue();
            compra.getCliente().getCuentaPuntos().revertir(
                    compra.getLoteGanado(),
                    puntosARevertir,
                    hoy,
                    "Reversion por cancelacion compra #" + compra.getId()
            );
        }
    }
}
