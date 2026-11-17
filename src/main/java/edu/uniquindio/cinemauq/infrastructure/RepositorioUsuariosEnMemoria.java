package edu.uniquindio.cinemauq.infrastructure;

import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.RepositorioUsuarios;
import edu.uniquindio.cinemauq.domain.Usuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class RepositorioUsuariosEnMemoria implements RepositorioUsuarios {
    private final Map<Long, Usuario> usuarios;
    private final AtomicLong contadorId;

    public RepositorioUsuariosEnMemoria() {
        this.usuarios = new HashMap<>();
        this.contadorId = new AtomicLong(1);
    }

    @Override
    public synchronized Usuario guardar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        if (usuario.getId() == null || usuario.getId() <= 0) {
            usuario.setId(this.contadorId.getAndIncrement());
        }
        this.usuarios.put(usuario.getId(), usuario);
        return usuario;
    }

    @Override
    public synchronized Optional<Usuario> buscarPorCorreo(String correo) {
        if (correo == null) {
            return Optional.empty();
        }
        String correoLimpio = correo.trim().toLowerCase();
        for (Usuario usuario : this.usuarios.values()) {
            if (usuario.getCorreo().equalsIgnoreCase(correoLimpio)) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }

    @Override
    public synchronized Optional<Usuario> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.usuarios.get(id));
    }

    @Override
    public synchronized List<Cliente> listarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        for (Usuario usuario : this.usuarios.values()) {
            if (usuario instanceof Cliente) {
                clientes.add((Cliente) usuario);
            }
        }
        return Collections.unmodifiableList(clientes);
    }
}
