package edu.uniquindio.cinemauq.domain;

import java.util.List;
import java.util.Optional;

public interface RepositorioSalas {
    Sala guardar(Sala sala);
    Optional<Sala> buscarPorId(Long id);
    List<Sala> listar();
    void eliminar(Long id);
}
