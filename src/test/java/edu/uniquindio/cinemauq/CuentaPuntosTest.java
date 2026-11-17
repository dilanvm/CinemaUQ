package edu.uniquindio.cinemauq;

import edu.uniquindio.cinemauq.application.AcumuladorPuntos;
import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.ConsumoPuntos;
import edu.uniquindio.cinemauq.domain.CuentaPuntos;
import edu.uniquindio.cinemauq.domain.LotePuntos;
import edu.uniquindio.cinemauq.domain.MovimientoPuntos;
import edu.uniquindio.cinemauq.domain.PuntosInsuficientesException;
import edu.uniquindio.cinemauq.domain.TipoMovimientoPuntos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CuentaPuntosTest {
    private CuentaPuntos cuenta;
    private Clock fixedClock;
    private LocalDate hoy;

    @BeforeEach
    public void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-11-15T10:00:00Z"), ZoneId.of("America/Bogota"));
        hoy = LocalDate.now(fixedClock);
        cuenta = new CuentaPuntos();
    }

    @Test
    public void testAcumulacionRedondeadaHaciaAbajo() {
        setUp();
        Cliente cliente = new Cliente(1L, "Test", "test@correo.com", "1234");
        AcumuladorPuntos acumulador = new AcumuladorPuntos(fixedClock);

        Compra compra1 = new Compra();
        compra1.setId(101L);
        compra1.setCliente(cliente);
        compra1.setMontoPagadoTarjeta(new BigDecimal("14999.00"));
        acumulador.alConfirmarse(compra1);
        assertEquals(2, cliente.getCuentaPuntos().consultarSaldoVigente(hoy));

        Compra compra2 = new Compra();
        compra2.setId(102L);
        compra2.setCliente(cliente);
        compra2.setMontoPagadoTarjeta(new BigDecimal("5000.00"));
        acumulador.alConfirmarse(compra2);
        assertEquals(3, cliente.getCuentaPuntos().consultarSaldoVigente(hoy));

        Compra compra3 = new Compra();
        compra3.setId(103L);
        compra3.setCliente(cliente);
        compra3.setMontoPagadoTarjeta(new BigDecimal("4999.00"));
        acumulador.alConfirmarse(compra3);
        assertEquals(3, cliente.getCuentaPuntos().consultarSaldoVigente(hoy));
    }

    @Test
    public void testConsumoLoteQueVencePrimero() {
        setUp();
        LotePuntos loteProximo = new LotePuntos(50, 50, hoy.minusDays(100), hoy.plusDays(10));
        LotePuntos loteLejano = new LotePuntos(50, 50, hoy.minusDays(50), hoy.plusDays(30));
        cuenta.agregarLote(loteProximo);
        cuenta.agregarLote(loteLejano);

        List<ConsumoPuntos> consumos = cuenta.redimir(60, hoy, "Redencion test");

        assertEquals(2, consumos.size());
        assertEquals(50, consumos.get(0).getCantidad());
        assertEquals(loteProximo, consumos.get(0).getLote());
        assertEquals(0, loteProximo.getCantidadRestante());

        assertEquals(10, consumos.get(1).getCantidad());
        assertEquals(loteLejano, consumos.get(1).getLote());
        assertEquals(40, loteLejano.getCantidadRestante());

        assertEquals(40, cuenta.consultarSaldoVigente(hoy));
        List<MovimientoPuntos> movs = cuenta.consultarMovimientos();
        assertEquals(1, movs.size());
        assertEquals(TipoMovimientoPuntos.REDENCION, movs.get(0).getTipo());
        assertEquals(60, movs.get(0).getPuntos());
    }

    @Test
    public void testNoUsarPuntosVencidos() {
        setUp();
        LotePuntos loteVencido = new LotePuntos(100, 100, hoy.minusDays(400), hoy.minusDays(1));
        LotePuntos loteVigente = new LotePuntos(30, 30, hoy.minusDays(10), hoy.plusDays(300));
        cuenta.agregarLote(loteVencido);
        cuenta.agregarLote(loteVigente);

        assertEquals(30, cuenta.consultarSaldoVigente(hoy));

        assertThrows(PuntosInsuficientesException.class, () ->
                cuenta.redimir(50, hoy, "Intento redimir con vencidos")
        );
        assertEquals(30, cuenta.consultarSaldoVigente(hoy));
    }

    @Test
    public void testRedencionInsuficiente() {
        setUp();
        cuenta.acumular(20, hoy, "Carga 20");

        assertThrows(PuntosInsuficientesException.class, () ->
                cuenta.redimir(50, hoy, "Faltan puntos")
        );
        assertEquals(20, cuenta.consultarSaldoVigente(hoy));
    }
}
