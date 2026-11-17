package edu.uniquindio.cinemauq;

import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.CompraInvalidaException;
import edu.uniquindio.cinemauq.domain.ReembolsoDuplicadoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EstadoCompraTest {
    private Compra compra;

    @BeforeEach
    public void setUp() {
        compra = new Compra();
    }

    @Test
    public void testTransicionesValidas() {
        setUp();
        assertEquals("PENDIENTE", compra.getEstado().getNombre());

        compra.confirmar();
        assertEquals("CONFIRMADA", compra.getEstado().getNombre());

        compra.cancelar();
        assertEquals("CANCELADA", compra.getEstado().getNombre());

        compra.marcarReembolsada();
        assertEquals("REEMBOLSADA", compra.getEstado().getNombre());
    }

    @Test
    public void testTransicionInvalidaPendienteACancelada() {
        setUp();
        assertEquals("PENDIENTE", compra.getEstado().getNombre());
        assertThrows(CompraInvalidaException.class, () -> compra.cancelar());
    }

    @Test
    public void testTransicionInvalidaPendienteAReembolsada() {
        setUp();
        assertEquals("PENDIENTE", compra.getEstado().getNombre());
        assertThrows(CompraInvalidaException.class, () -> compra.marcarReembolsada());
    }

    @Test
    public void testTransicionInvalidaConfirmadaAConfirmada() {
        setUp();
        compra.confirmar();
        assertThrows(CompraInvalidaException.class, () -> compra.confirmar());
    }

    @Test
    public void testTransicionInvalidaConfirmadaAReembolsada() {
        setUp();
        compra.confirmar();
        assertThrows(CompraInvalidaException.class, () -> compra.marcarReembolsada());
    }

    @Test
    public void testTransicionInvalidaCanceladaAConfirmada() {
        setUp();
        compra.confirmar();
        compra.cancelar();
        assertThrows(CompraInvalidaException.class, () -> compra.confirmar());
    }

    @Test
    public void testTransicionInvalidaCanceladaACancelada() {
        setUp();
        compra.confirmar();
        compra.cancelar();
        assertThrows(CompraInvalidaException.class, () -> compra.cancelar());
    }

    @Test
    public void testTransicionInvalidaReembolsadaAMarcarReembolsada() {
        setUp();
        compra.confirmar();
        compra.cancelar();
        compra.marcarReembolsada();
        assertThrows(ReembolsoDuplicadoException.class, () -> compra.marcarReembolsada());
    }

    @Test
    public void testTransicionInvalidaReembolsadaAConfirmadaOCancelada() {
        setUp();
        compra.confirmar();
        compra.cancelar();
        compra.marcarReembolsada();
        assertThrows(CompraInvalidaException.class, () -> compra.confirmar());
        assertThrows(CompraInvalidaException.class, () -> compra.cancelar());
    }
}
