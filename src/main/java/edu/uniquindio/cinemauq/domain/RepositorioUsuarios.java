package edu.uniquindio.cinemauq.domain;

import java.util.List;
import java.util.Optional;

public interface RepositorioUsuarios {
    Usuario guardar(Usuario usuario);
    Optional<Usuario> buscarPorCorreo(String correo);
    Optional<Usuario> buscarPorId(Long id);
    List<Cliente> listarClientes();
}
