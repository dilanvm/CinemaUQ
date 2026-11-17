package edu.uniquindio.cinemauq;

import edu.uniquindio.cinemauq.application.ServicioTarjeta;
import edu.uniquindio.cinemauq.domain.Administrador;
import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.MovimientoTarjeta;
import edu.uniquindio.cinemauq.domain.SaldoInsuficienteException;
import edu.uniquindio.cinemauq.domain.TarjetaInactivaException;
import edu.uniquindio.cinemauq.domain.TarjetaVirtual;
import edu.uniquindio.cinemauq.domain.TipoMovimiento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TarjetaVirtualTest {
    private TarjetaVirtual tarjeta;

    @BeforeEach
    public void setUp() {
        tarjeta = new TarjetaVirtual();
    }

    @Test
    public void testPagoCorrecto() {
        setUp();
        tarjeta.recargar(new BigDecimal("20000.00"));
        tarjeta.pagar(new BigDecimal("15000.00"), "Pago entradas");

        assertEquals(new BigDecimal("5000.00"), tarjeta.consultarSaldo());
        List<MovimientoTarjeta> movimientos = tarjeta.consultarMovimientos();
        assertEquals(2, movimientos.size());
        assertEquals(TipoMovimiento.PAGO, movimientos.get(1).getTipo());
        assertEquals(new BigDecimal("15000.00"), movimientos.get(1).getMonto());
    }

    @Test
    public void testSaldoInsuficiente() {
        setUp();
        tarjeta.recargar(new BigDecimal("10000.00"));

        assertThrows(SaldoInsuficienteException.class, () ->
                tarjeta.pagar(new BigDecimal("15000.00"), "Pago fallido")
        );
        assertEquals(new BigDecimal("10000.00"), tarjeta.consultarSaldo());
    }

    @Test
    public void testTarjetaInactiva() {
        setUp();
        tarjeta.recargar(new BigDecimal("20000.00"));
        tarjeta.desactivar();
        assertFalse(tarjeta.isActiva());

        assertThrows(TarjetaInactivaException.class, () ->
                tarjeta.pagar(new BigDecimal("5000.00"), "Pago inactiva")
        );
    }

    @Test
    public void testRecargaYReembolsoConMovimiento() {
        setUp();
        tarjeta.recargar(new BigDecimal("50000.00"));
        tarjeta.reembolsar(new BigDecimal("20000.00"), "Reembolso");

        assertEquals(new BigDecimal("70000.00"), tarjeta.consultarSaldo());
        List<MovimientoTarjeta> movimientos = tarjeta.consultarMovimientos();
        assertEquals(2, movimientos.size());
        assertEquals(TipoMovimiento.RECARGA, movimientos.get(0).getTipo());
        assertEquals(TipoMovimiento.REEMBOLSO, movimientos.get(1).getTipo());
    }

    @Test
    public void testServicioTarjetaRecargaAdmin() {
        setUp();
        ServicioTarjeta servicio = new ServicioTarjeta();
        Administrador admin = new Administrador(1L, "Admin", "admin@cinemauq.com", "pass");
        Cliente cliente = new Cliente(2L, "Cliente", "cli@cinemauq.com", "pass");

        servicio.recargar(admin, cliente, new BigDecimal("45000.00"));
        assertEquals(new BigDecimal("45000.00"), servicio.consultarSaldo(cliente));

        servicio.cambiarEstado(admin, cliente, false);
        assertFalse(cliente.getTarjetaVirtual().isActiva());
    }
}
