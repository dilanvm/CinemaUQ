package edu.uniquindio.cinemauq;

import edu.uniquindio.cinemauq.application.AcumuladorPuntos;
import edu.uniquindio.cinemauq.application.CompraBuilder;
import edu.uniquindio.cinemauq.application.PublicadorCompras;
import edu.uniquindio.cinemauq.application.ServicioCompra;
import edu.uniquindio.cinemauq.application.ServicioDisponibilidad;
import edu.uniquindio.cinemauq.domain.Asiento;
import edu.uniquindio.cinemauq.domain.AsientoNoDisponibleException;
import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.EstadoFuncion;
import edu.uniquindio.cinemauq.domain.Funcion;
import edu.uniquindio.cinemauq.domain.Pelicula;
import edu.uniquindio.cinemauq.domain.Producto;
import edu.uniquindio.cinemauq.domain.RepositorioCompras;
import edu.uniquindio.cinemauq.domain.Sala;
import edu.uniquindio.cinemauq.domain.SaldoInsuficienteException;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompraTest {
    private Clock fixedClock;
    private ServicioDisponibilidad disponibilidad;
    private RepositorioCompras repoCompras;
    private PublicadorCompras publicador;
    private ServicioCompra servicioCompra;
    private Funcion funcion;
    private Asiento asiento1;
    private Asiento asiento2;

    @BeforeEach
    public void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-11-15T10:00:00Z"), ZoneId.of("America/Bogota"));
        disponibilidad = new ServicioDisponibilidad();
        repoCompras = new RepositorioComprasEnMemoria();
        publicador = new PublicadorCompras();
        publicador.registrarObservador(new AcumuladorPuntos(fixedClock));
        servicioCompra = new ServicioCompra(repoCompras, disponibilidad, publicador, fixedClock);

        Pelicula pelicula = new Pelicula(1L, "Avatar", "Aventura", 160, "+12", "Sci-Fi");
        Sala sala = new Sala("Sala 1", 1L, new ArrayList<>(), true);
        asiento1 = new Asiento(1L, "A", 1, TipoAsiento.GENERAL);
        asiento2 = new Asiento(2L, "A", 2, TipoAsiento.GENERAL);
        sala.agregarAsiento(asiento1);
        sala.agregarAsiento(asiento2);

        LocalDateTime fechaFutura = LocalDateTime.now(fixedClock).plusDays(2);
        funcion = new Funcion(1L, pelicula, sala, fechaFutura, new BigDecimal("10000.00"), EstadoFuncion.PROGRAMADA);
    }

    @Test
    public void testAsientoYaReservado() {
        setUp();
        disponibilidad.reservar(funcion, asiento1);
        assertFalse(disponibilidad.estaDisponible(funcion, asiento1));

        assertThrows(AsientoNoDisponibleException.class, () ->
                disponibilidad.reservar(funcion, asiento1)
        );
    }

    @Test
    public void testPagoFallidoLiberaAsientos() {
        setUp();
        Cliente cliente = new Cliente(1L, "Pobre", "pobre@correo.com", "1234");
        cliente.getTarjetaVirtual().recargar(new BigDecimal("2000.00"));

        Compra compra = new CompraBuilder(cliente, fixedClock)
                .agregarEntrada(funcion, asiento1)
                .construir();

        assertThrows(SaldoInsuficienteException.class, () ->
                servicioCompra.comprar(compra)
        );

        assertTrue(disponibilidad.estaDisponible(funcion, asiento1));
        assertEquals(new BigDecimal("2000.00"), cliente.getTarjetaVirtual().consultarSaldo());
    }

    @Test
    public void testCompraConPuntosParciales() {
        setUp();
        Cliente cliente = new Cliente(1L, "ConPuntos", "puntos@correo.com", "1234");
        cliente.getTarjetaVirtual().recargar(new BigDecimal("30000.00"));
        LocalDate hoy = LocalDate.now(fixedClock);
        cliente.getCuentaPuntos().acumular(100, hoy, "Carga inicial");

        Producto crispetas = new Producto("Crispetas", "Comida", new BigDecimal("5000.00"), true);

        Compra compra = new CompraBuilder(cliente, fixedClock)
                .agregarEntrada(funcion, asiento1)
                .agregarElemento(crispetas, 1)
                .usarPuntos(50)
                .construir();

        assertEquals(new BigDecimal("15000.00"), compra.getTotal());
        assertEquals(50, compra.getPuntosUsados());
        assertEquals(new BigDecimal("5000.00"), compra.getValorPuntos());
        assertEquals(new BigDecimal("10000.00"), compra.getMontoPagadoTarjeta());

        Compra confirmada = servicioCompra.comprar(compra);
        assertEquals("CONFIRMADA", confirmada.getEstado().getNombre());
        assertEquals(new BigDecimal("20000.00"), cliente.getTarjetaVirtual().consultarSaldo());
        assertFalse(disponibilidad.estaDisponible(funcion, asiento1));

        assertEquals(52, cliente.getCuentaPuntos().consultarSaldoVigente(hoy));
    }
}
