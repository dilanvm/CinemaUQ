package edu.uniquindio.cinemauq.domain;

import java.util.List;
import java.util.Optional;

public interface RepositorioPeliculas {
    Pelicula guardar(Pelicula pelicula);
    Optional<Pelicula> buscarPorId(Long id);
    List<Pelicula> listar();
    void eliminar(Long id);
}
