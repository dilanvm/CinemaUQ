package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.Entrada;
import edu.uniquindio.cinemauq.domain.EstadoFuncion;
import edu.uniquindio.cinemauq.domain.Funcion;
import edu.uniquindio.cinemauq.domain.RepositorioCompras;
import edu.uniquindio.cinemauq.domain.estado.EstadoConfirmada;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class ServicioCancelacion {
    private final RepositorioCompras repositorioCompras;
    private final ServicioDisponibilidad servicioDisponibilidad;
    private final PublicadorCompras publicadorCompras;
    private final PoliticaReembolso politicaReembolsoCliente;
    private final Clock clock;

    public ServicioCancelacion(RepositorioCompras repositorioCompras,
                               ServicioDisponibilidad servicioDisponibilidad,
                               PublicadorCompras publicadorCompras,
                               PoliticaReembolso politicaReembolsoCliente) {
        this(repositorioCompras, servicioDisponibilidad, publicadorCompras, politicaReembolsoCliente, Clock.system(ZoneId.of("America/Bogota")));
    }

    public ServicioCancelacion(RepositorioCompras repositorioCompras,
                               ServicioDisponibilidad servicioDisponibilidad,
                               PublicadorCompras publicadorCompras,
                               PoliticaReembolso politicaReembolsoCliente,
                               Clock clock) {
        if (repositorioCompras == null || servicioDisponibilidad == null || publicadorCompras == null || politicaReembolsoCliente == null) {
            throw new IllegalArgumentException("Todas las dependencias son obligatorias");
        }
        this.repositorioCompras = repositorioCompras;
        this.servicioDisponibilidad = servicioDisponibilidad;
        this.publicadorCompras = publicadorCompras;
        this.politicaReembolsoCliente = politicaReembolsoCliente;
        this.clock = clock != null ? clock : Clock.system(ZoneId.of("America/Bogota"));
    }

    public void cancelarPorCliente(Compra compra) {
        if (compra == null) {
            throw new IllegalArgumentException("La compra es obligatoria");
        }
        BigDecimal porcentaje = this.politicaReembolsoCliente.calcularPorcentaje(
                compra.getFuncion().getFechaHora(),
                LocalDateTime.now(this.clock)
        );
        compra.cancelar();
        BigDecimal montoReembolso = compra.getMontoPagadoTarjeta().multiply(porcentaje).setScale(2, RoundingMode.HALF_UP);
        if (montoReembolso.compareTo(BigDecimal.ZERO) > 0) {
            compra.getCliente().getTarjetaVirtual().reembolsar(montoReembolso, "Reembolso por cancelacion cliente compra #" + compra.getId());
        }
        compra.marcarReembolsada();
        for (Entrada entrada : compra.getEntradas()) {
            this.servicioDisponibilidad.liberar(compra.getFuncion(), entrada.getAsiento());
        }
        this.publicadorCompras.notificarCancelacion(compra, porcentaje);
        this.repositorioCompras.guardar(compra);
    }

    public void cancelarFuncion(Funcion funcion) {
        if (funcion == null) {
            throw new IllegalArgumentException("La funcion es obligatoria");
        }
        funcion.setEstadoFuncion(EstadoFuncion.CANCELADA);
        PoliticaReembolso politicaCine = new ReembolsoCancelacionCine();
        List<Compra> compras = this.repositorioCompras.buscarPorFuncion(funcion);
        for (Compra compra : compras) {
            if (compra.getEstado() instanceof EstadoConfirmada) {
                BigDecimal porcentaje = politicaCine.calcularPorcentaje(
                        funcion.getFechaHora(),
                        LocalDateTime.now(this.clock)
                );
                compra.cancelar();
                BigDecimal montoReembolso = compra.getMontoPagadoTarjeta().multiply(porcentaje).setScale(2, RoundingMode.HALF_UP);
                if (montoReembolso.compareTo(BigDecimal.ZERO) > 0) {
                    compra.getCliente().getTarjetaVirtual().reembolsar(montoReembolso, "Reembolso cancelacion funcion #" + funcion.getId());
                }
                compra.marcarReembolsada();
                for (Entrada entrada : compra.getEntradas()) {
                    this.servicioDisponibilidad.liberar(funcion, entrada.getAsiento());
                }
                this.publicadorCompras.notificarCancelacion(compra, porcentaje);
                this.repositorioCompras.guardar(compra);
            }
        }
    }
}
