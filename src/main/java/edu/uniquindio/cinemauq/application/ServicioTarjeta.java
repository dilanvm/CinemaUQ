package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Administrador;
import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.MovimientoTarjeta;

import java.math.BigDecimal;
import java.util.List;

public class ServicioTarjeta {
    public void recargar(Administrador administrador, Cliente cliente, BigDecimal monto) {
        if (administrador == null) {
            throw new IllegalArgumentException("Se requiere un administrador para recargar la tarjeta");
        }
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        cliente.getTarjetaVirtual().recargar(monto, "Recarga realizada por administrador " + administrador.getNombre());
    }

    public void cambiarEstado(Administrador administrador, Cliente cliente, boolean activa) {
        if (administrador == null) {
            throw new IllegalArgumentException("Se requiere un administrador para cambiar el estado de la tarjeta");
        }
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        if (activa) {
            cliente.getTarjetaVirtual().activar();
        } else {
            cliente.getTarjetaVirtual().desactivar();
        }
    }

    public BigDecimal consultarSaldo(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        return cliente.getTarjetaVirtual().consultarSaldo();
    }

    public List<MovimientoTarjeta> consultarMovimientos(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        return cliente.getTarjetaVirtual().consultarMovimientos();
    }
}
