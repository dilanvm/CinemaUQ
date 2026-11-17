package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Asiento;
import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.ConsumoPuntos;
import edu.uniquindio.cinemauq.domain.Entrada;
import edu.uniquindio.cinemauq.domain.PuntosInsuficientesException;
import edu.uniquindio.cinemauq.domain.RepositorioCompras;
import edu.uniquindio.cinemauq.domain.SaldoInsuficienteException;
import edu.uniquindio.cinemauq.domain.TarjetaInactivaException;
import edu.uniquindio.cinemauq.domain.TarjetaVirtual;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ServicioCompra {
    private final RepositorioCompras repositorioCompras;
    private final ServicioDisponibilidad servicioDisponibilidad;
    private final PublicadorCompras publicadorCompras;
    private final Clock clock;

    public ServicioCompra(RepositorioCompras repositorioCompras,
                          ServicioDisponibilidad servicioDisponibilidad,
                          PublicadorCompras publicadorCompras) {
        this(repositorioCompras, servicioDisponibilidad, publicadorCompras, Clock.system(ZoneId.of("America/Bogota")));
    }

    public ServicioCompra(RepositorioCompras repositorioCompras,
                          ServicioDisponibilidad servicioDisponibilidad,
                          PublicadorCompras publicadorCompras,
                          Clock clock) {
        if (repositorioCompras == null || servicioDisponibilidad == null || publicadorCompras == null) {
            throw new IllegalArgumentException("Todas las dependencias son obligatorias");
        }
        this.repositorioCompras = repositorioCompras;
        this.servicioDisponibilidad = servicioDisponibilidad;
        this.publicadorCompras = publicadorCompras;
        this.clock = clock != null ? clock : Clock.system(ZoneId.of("America/Bogota"));
    }

    public Compra comprar(Compra compra) {
        if (compra == null) {
            throw new IllegalArgumentException("La compra es obligatoria");
        }
        List<Asiento> reservados = new ArrayList<>();
        try {
            for (Entrada entrada : compra.getEntradas()) {
                this.servicioDisponibilidad.reservar(compra.getFuncion(), entrada.getAsiento());
                reservados.add(entrada.getAsiento());
            }

            if (compra.getPuntosUsados() > 0) {
                int saldoPuntos = compra.getCliente().getCuentaPuntos().consultarSaldoVigente(LocalDate.now(this.clock));
                if (saldoPuntos < compra.getPuntosUsados()) {
                    throw new PuntosInsuficientesException("Saldo de puntos insuficiente");
                }
            }

            TarjetaVirtual tarjeta = compra.getCliente().getTarjetaVirtual();
            if (!tarjeta.isActiva()) {
                throw new TarjetaInactivaException("La tarjeta virtual del cliente esta inactiva");
            }
            if (compra.getMontoPagadoTarjeta().compareTo(BigDecimal.ZERO) > 0 &&
                    tarjeta.consultarSaldo().compareTo(compra.getMontoPagadoTarjeta()) < 0) {
                throw new SaldoInsuficienteException("Saldo insuficiente en la tarjeta virtual");
            }

            if (compra.getMontoPagadoTarjeta().compareTo(BigDecimal.ZERO) > 0) {
                tarjeta.pagar(compra.getMontoPagadoTarjeta(), "Pago compra en funcion #" + compra.getFuncion().getId());
            }

            if (compra.getPuntosUsados() > 0) {
                List<ConsumoPuntos> consumos = compra.getCliente().getCuentaPuntos().redimir(
                        compra.getPuntosUsados(),
                        LocalDate.now(this.clock),
                        "Redencion en compra funcion #" + compra.getFuncion().getId()
                );
                compra.setConsumosPuntos(consumos);
            }

            compra.confirmar();
            Compra guardada = this.repositorioCompras.guardar(compra);
            this.publicadorCompras.notificarConfirmacion(guardada);
            return guardada;
        } catch (RuntimeException e) {
            for (Asiento asiento : reservados) {
                this.servicioDisponibilidad.liberar(compra.getFuncion(), asiento);
            }
            throw e;
        }
    }
}
