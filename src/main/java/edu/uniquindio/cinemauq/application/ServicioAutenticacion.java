package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.CredencialesInvalidasException;
import edu.uniquindio.cinemauq.domain.RepositorioUsuarios;
import edu.uniquindio.cinemauq.domain.Usuario;
import edu.uniquindio.cinemauq.domain.UsuarioDuplicadoException;

public class ServicioAutenticacion {
    private final RepositorioUsuarios repositorioUsuarios;

    public ServicioAutenticacion(RepositorioUsuarios repositorioUsuarios) {
        if (repositorioUsuarios == null) {
            throw new IllegalArgumentException("El repositorio de usuarios es obligatorio");
        }
        this.repositorioUsuarios = repositorioUsuarios;
    }

    public Cliente registrar(String nombre, String correo, String contrasena) {
        if (correo == null || correo.isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }
        if (this.repositorioUsuarios.buscarPorCorreo(correo.trim().toLowerCase()).isPresent()) {
            throw new UsuarioDuplicadoException("El correo ya se encuentra registrado");
        }
        Cliente nuevoCliente = new Cliente(null, nombre, correo, contrasena);
        return (Cliente) this.repositorioUsuarios.guardar(nuevoCliente);
    }

    public Usuario iniciarSesion(String correo, String contrasena) {
        if (correo == null || contrasena == null) {
            throw new CredencialesInvalidasException("Credenciales invalidas");
        }
        Usuario usuario = this.repositorioUsuarios.buscarPorCorreo(correo.trim().toLowerCase())
                .orElseThrow(() -> new CredencialesInvalidasException("Credenciales invalidas"));
        if (!usuario.getContrasena().equals(contrasena)) {
            throw new CredencialesInvalidasException("Credenciales invalidas");
        }
        return usuario;
    }
}
