package edu.uniquindio.cinemauq.domain;

import java.util.List;
import java.util.Optional;

public interface RepositorioCompras {
    Compra guardar(Compra compra);
    Optional<Compra> buscarPorId(Long id);
    List<Compra> buscarPorCliente(Cliente cliente);
    List<Compra> buscarPorFuncion(Funcion funcion);
    List<Compra> listar();
}
