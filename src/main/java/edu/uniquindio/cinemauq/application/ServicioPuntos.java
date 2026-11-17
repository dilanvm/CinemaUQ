package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.LotePuntos;
import edu.uniquindio.cinemauq.domain.MovimientoPuntos;

import java.time.LocalDate;
import java.util.List;

public class ServicioPuntos {
    public int consultarSaldo(Cliente cliente, LocalDate hoy) {
        if (cliente == null || hoy == null) {
            throw new IllegalArgumentException("El cliente y la fecha actual son obligatorios");
        }
        return cliente.getCuentaPuntos().consultarSaldoVigente(hoy);
    }

    public List<LotePuntos> consultarLotes(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        return cliente.getCuentaPuntos().consultarLotes();
    }

    public List<MovimientoPuntos> consultarMovimientos(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        return cliente.getCuentaPuntos().consultarMovimientos();
    }
}
