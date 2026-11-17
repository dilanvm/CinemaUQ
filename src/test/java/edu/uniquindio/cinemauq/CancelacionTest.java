package edu.uniquindio.cinemauq;

import edu.uniquindio.cinemauq.application.AcumuladorPuntos;
import edu.uniquindio.cinemauq.application.CompraBuilder;
import edu.uniquindio.cinemauq.application.PublicadorCompras;
import edu.uniquindio.cinemauq.application.ReembolsoCancelacionCliente;
import edu.uniquindio.cinemauq.application.ServicioCancelacion;
import edu.uniquindio.cinemauq.application.ServicioCompra;
import edu.uniquindio.cinemauq.application.ServicioDisponibilidad;
import edu.uniquindio.cinemauq.domain.Asiento;
import edu.uniquindio.cinemauq.domain.CancelacionNoPermitidaException;
import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.EstadoFuncion;
import edu.uniquindio.cinemauq.domain.Funcion;
import edu.uniquindio.cinemauq.domain.Pelicula;
import edu.uniquindio.cinemauq.domain.ReembolsoDuplicadoException;
import edu.uniquindio.cinemauq.domain.RepositorioCompras;
import edu.uniquindio.cinemauq.domain.Sala;
import edu.uniquindio.cinemauq.domain.TipoAsiento;
import edu.uniquindio.cinemauq.infrastructure.RepositorioComprasEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CancelacionTest {
    private Clock fixedClock;
    private ServicioDisponibilidad disponibilidad;
    private RepositorioCompras repoCompras;
    private PublicadorCompras publicador;
    private ServicioCompra servicioCompra;
    private ServicioCancelacion servicioCancelacion;
    private Pelicula pelicula;
    private Sala sala;

    @BeforeEach
    public void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-11-15T10:00:00Z"), ZoneId.of("America/Bogota"));
        disponibilidad = new ServicioDisponibilidad();
        repoCompras = new RepositorioComprasEnMemoria();
        publicador = new PublicadorCompras();
        publicador.registrarObservador(new AcumuladorPuntos(fixedClock));
        servicioCompra = new ServicioCompra(repoCompras, disponibilidad, publicador, fixedClock);
        servicioCancelacion = new ServicioCancelacion(
                repoCompras,
                disponibilidad,
                publicador,
                new ReembolsoCancelacionCliente(),
                fixedClock
        );

        pelicula = new Pelicula(1L, "Batman", "Accion", 150, "+12", "Accion");
        sala = new Sala("Sala 2", 2L, new ArrayList<>(), true);
    }

    @Test
    public void testCancelacionMasDe24Horas() {
        setUp();
        Asiento asiento = new Asiento(1L, "B", 1, TipoAsiento.GENERAL);
        sala.agregarAsiento(asiento);
        LocalDateTime inicio = LocalDateTime.now(fixedClock).plusHours(30);
        Funcion funcion = new Funcion(10L, pelicula, sala, inicio, new BigDecimal("10000.00"), EstadoFuncion.PROGRAMADA);

        Cliente cliente = new Cliente(1L, "Cli", "cli@test.com", "1234");
        cliente.getTarjetaVirtual().recargar(new BigDecimal("10000.00"));

        Compra compra = new CompraBuilder(cliente, fixedClock)
                .agregarEntrada(funcion, asiento)
                .construir();

        Compra realizada = servicioCompra.comprar(compra);
        assertEquals(new BigDecimal("0.00"), cliente.getTarjetaVirtual().consultarSaldo());

        servicioCancelacion.cancelarPorCliente(realizada);
        assertEquals("REEMBOLSADA", realizada.getEstado().getNombre());
        assertEquals(new BigDecimal("10000.00"), cliente.getTarjetaVirtual().consultarSaldo());
        assertTrue(disponibilidad.estaDisponible(funcion, asiento));
    }

    @Test
    public void testCancelacionEntre2Y24Horas() {
        setUp();
        Asiento asiento = new Asiento(2L, "B", 2, TipoAsiento.GENERAL);
        sala.agregarAsiento(asiento);
        LocalDateTime inicio = LocalDateTime.now(fixedClock).plusHours(5);
        Funcion funcion = new Funcion(11L, pelicula, sala, inicio, new BigDecimal("10000.00"), EstadoFuncion.PROGRAMADA);

        Cliente cliente = new Cliente(1L, "Cli", "cli@test.com", "1234");
        cliente.getTarjetaVirtual().recargar(new BigDecimal("10000.00"));

        Compra compra = new CompraBuilder(cliente, fixedClock)
                .agregarEntrada(funcion, asiento)
                .construir();

        Compra realizada = servicioCompra.comprar(compra);
        assertEquals(new BigDecimal("0.00"), cliente.getTarjetaVirtual().consultarSaldo());

        servicioCancelacion.cancelarPorCliente(realizada);
        assertEquals("REEMBOLSADA", realizada.getEstado().getNombre());
        assertEquals(new BigDecimal("8000.00"), cliente.getTarjetaVirtual().consultarSaldo());
        assertTrue(disponibilidad.estaDisponible(funcion, asiento));
    }

    @Test
    public void testCancelacionMenosDe2Horas() {
        setUp();
        Asiento asiento = new Asiento(3L, "B", 3, TipoAsiento.GENERAL);
        sala.agregarAsiento(asiento);
        LocalDateTime inicio = LocalDateTime.now(fixedClock).plusMinutes(90);
        Funcion funcion = new Funcion(12L, pelicula, sala, inicio, new BigDecimal("10000.00"), EstadoFuncion.PROGRAMADA);

        Cliente cliente = new Cliente(1L, "Cli", "cli@test.com", "1234");
        cliente.getTarjetaVirtual().recargar(new BigDecimal("10000.00"));

        Compra compra = new CompraBuilder(cliente, fixedClock)
                .agregarEntrada(funcion, asiento)
                .construir();

        Compra realizada = servicioCompra.comprar(compra);

        assertThrows(CancelacionNoPermitidaException.class, () ->
                servicioCancelacion.cancelarPorCliente(realizada)
        );
        assertEquals("CONFIRMADA", realizada.getEstado().getNombre());
    }

    @Test
    public void testReembolsoDuplicado() {
        setUp();
        Asiento asiento = new Asiento(4L, "B", 4, TipoAsiento.GENERAL);
        sala.agregarAsiento(asiento);
        LocalDateTime inicio = LocalDateTime.now(fixedClock).plusHours(40);
        Funcion funcion = new Funcion(13L, pelicula, sala, inicio, new BigDecimal("10000.00"), EstadoFuncion.PROGRAMADA);

        Cliente cliente = new Cliente(1L, "Cli", "cli@test.com", "1234");
        cliente.getTarjetaVirtual().recargar(new BigDecimal("10000.00"));

        Compra compra = new CompraBuilder(cliente, fixedClock)
                .agregarEntrada(funcion, asiento)
                .construir();

        Compra realizada = servicioCompra.comprar(compra);
        servicioCancelacion.cancelarPorCliente(realizada);

        assertThrows(ReembolsoDuplicadoException.class, () ->
                realizada.marcarReembolsada()
        );
    }

    @Test
    public void testCancelacionFuncionPorCine() {
        setUp();
        Asiento asiento = new Asiento(5L, "B", 5, TipoAsiento.GENERAL);
        sala.agregarAsiento(asiento);
        LocalDateTime inicio = LocalDateTime.now(fixedClock).plusHours(1);
        Funcion funcion = new Funcion(14L, pelicula, sala, inicio, new BigDecimal("10000.00"), EstadoFuncion.PROGRAMADA);

        Cliente cliente = new Cliente(1L, "Cli", "cli@test.com", "1234");
        cliente.getTarjetaVirtual().recargar(new BigDecimal("10000.00"));

        Compra compra = new CompraBuilder(cliente, fixedClock)
                .agregarEntrada(funcion, asiento)
                .construir();

        Compra realizada = servicioCompra.comprar(compra);

        servicioCancelacion.cancelarFuncion(funcion);

        assertEquals(EstadoFuncion.CANCELADA, funcion.getEstadoFuncion());
        assertEquals("REEMBOLSADA", realizada.getEstado().getNombre());
        assertEquals(new BigDecimal("10000.00"), cliente.getTarjetaVirtual().consultarSaldo());
        assertTrue(disponibilidad.estaDisponible(funcion, asiento));
    }

    @Test
    public void testReintegroYReversionPuntos() {
        setUp();
        Asiento asiento = new Asiento(6L, "B", 6, TipoAsiento.GENERAL);
        sala.agregarAsiento(asiento);
        LocalDateTime inicio = LocalDateTime.now(fixedClock).plusHours(5);
        Funcion funcion = new Funcion(15L, pelicula, sala, inicio, new BigDecimal("20000.00"), EstadoFuncion.PROGRAMADA);

        LocalDate hoy = LocalDate.now(fixedClock);
        Cliente cliente = new Cliente(1L, "Cli", "cli@test.com", "1234");
        cliente.getTarjetaVirtual().recargar(new BigDecimal("10000.00"));
        cliente.getCuentaPuntos().acumular(100, hoy, "Inicial");

        Compra compra = new CompraBuilder(cliente, fixedClock)
                .agregarEntrada(funcion, asiento)
                .usarPuntos(100)
                .construir();

        Compra realizada = servicioCompra.comprar(compra);
        assertEquals(2, cliente.getCuentaPuntos().consultarSaldoVigente(hoy));

        servicioCancelacion.cancelarPorCliente(realizada);

        assertEquals(80, cliente.getCuentaPuntos().consultarSaldoVigente(hoy));
        assertEquals(new BigDecimal("8000.00"), cliente.getTarjetaVirtual().consultarSaldo());
    }
}
