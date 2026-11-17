package edu.uniquindio.cinemauq.domain;

import java.util.List;
import java.util.Optional;

public interface RepositorioConfiteria {
    ElementoVendible guardar(ElementoVendible elemento);
    Optional<ElementoVendible> buscarPorNombre(String nombre);
    List<ElementoVendible> listar();
    void eliminar(String nombre);
}
