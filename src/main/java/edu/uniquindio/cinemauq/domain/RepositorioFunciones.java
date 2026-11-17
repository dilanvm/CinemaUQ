package edu.uniquindio.cinemauq.domain;

import java.util.List;
import java.util.Optional;

public interface RepositorioFunciones {
    Funcion guardar(Funcion funcion);
    Optional<Funcion> buscarPorId(Long id);
    List<Funcion> listar();
    void eliminar(Long id);
}
